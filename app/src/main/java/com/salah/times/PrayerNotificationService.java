package com.salah.times;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;
import androidx.core.app.NotificationCompat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PrayerNotificationService extends Service {
    private static final String CHANNEL_ID = "prayer_persistent";
    private static final int NOTIFICATION_ID = 1000;
    private Handler handler = new Handler();
    private Runnable updateRunnable;
    private PrayerTimes currentPrayerTimes;
    
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForegroundService();
        startPeriodicUpdates();
    }
    
    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
            CHANNEL_ID,
            "Prayer Times",
            NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("");
        channel.setShowBadge(false);
        channel.setSound(null, null);
        channel.enableVibration(false);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }
    
    private void startForegroundService() {
        Notification notification = createPrayerNotification();
        startForeground(NOTIFICATION_ID, notification);
    }
    
    private void startPeriodicUpdates() {
        loadCurrentPrayerTimes();
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateNotification();
                handler.postDelayed(this, 1000); // Update every second for countdown
            }
        };
        handler.post(updateRunnable);
    }
    
    private Notification createPrayerNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        RemoteViews customView = new RemoteViews(getPackageName(), R.layout.notification_prayer);
        
        String nextPrayer = getNextPrayerName();
        String nextTime = getNextPrayerTime();
        String countdown = getCountdownToNextPrayer();
        
        customView.setTextViewText(R.id.next_prayer_label, TranslationManager.tr("next_prayer"));
        customView.setTextViewText(R.id.next_prayer_name, nextPrayer);
        customView.setTextViewText(R.id.next_prayer_time, nextTime);
        customView.setTextViewText(R.id.countdown, countdown);
        
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setCustomContentView(customView)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setShowWhen(false)
            .setPriority(Notification.PRIORITY_MAX)
            .build();
        
        notification.contentView = customView;
        notification.visibility = Notification.VISIBILITY_PUBLIC;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        
        return notification;
    }
    
    private void updateNotification() {
        loadCurrentPrayerTimes();
        Notification notification = createPrayerNotification();
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify(NOTIFICATION_ID, notification);
    }
    
    private void loadCurrentPrayerTimes() {
        try {
            String cityName = SettingsManager.getDefaultCity();
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            currentPrayerTimes = DatabaseHelper.getInstance(this).loadPrayerTimes(cityName, today);
        } catch (Exception e) {
            android.util.Log.e("PrayerNotification", "Failed to load prayer times", e);
        }
    }
    
    private String getNextPrayerName() {
        if (currentPrayerTimes == null) return TranslationManager.tr("loading");
        String nextPrayer = PrayerHighlightManager.getNextPrayer(currentPrayerTimes);
        return TranslationManager.tr("prayers." + nextPrayer.toLowerCase());
    }
    
    private String getNextPrayerTime() {
        if (currentPrayerTimes == null) return "--:--";
        String nextPrayer = PrayerHighlightManager.getNextPrayer(currentPrayerTimes);
        return getTimeForPrayer(nextPrayer);
    }
    
    private String getTimeForPrayer(String prayer) {
        switch (prayer) {
            case "Fajr": return currentPrayerTimes.getFajr();
            case "Dhuhr": case "Dohr": return currentPrayerTimes.getDhuhr();
            case "Asr": return currentPrayerTimes.getAsr();
            case "Maghrib": case "Maghreb": return currentPrayerTimes.getMaghrib();
            case "Isha": return currentPrayerTimes.getIsha();
            default: return "--:--";
        }
    }
    
    private String getCountdownToNextPrayer() {
        if (currentPrayerTimes == null) return "--:--:--";
        
        try {
            Calendar now = Calendar.getInstance();
            int currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);
            int currentSeconds = now.get(Calendar.SECOND);
            
            String nextPrayer = PrayerHighlightManager.getNextPrayer(currentPrayerTimes);
            String nextTime = getTimeForPrayer(nextPrayer);
            
            String[] parts = nextTime.split(":");
            int prayerMinutes = Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
            
            int remainingMinutes = prayerMinutes - currentMinutes;
            if (remainingMinutes < 0) remainingMinutes += 24 * 60;
            
            int hours = remainingMinutes / 60;
            int minutes = remainingMinutes % 60;
            int seconds = 60 - currentSeconds;
            
            if (seconds == 60) {
                seconds = 0;
            } else if (minutes > 0) {
                minutes--;
            }
            
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        } catch (Exception e) {
            return "--:--:--";
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null && updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
    }
}