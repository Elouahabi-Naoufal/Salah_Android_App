package com.salah.times;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import java.text.SimpleDateFormat;
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
            "Prayer Times Display",
            NotificationManager.IMPORTANCE_LOW
        );
        channel.setDescription("Shows current prayer times and countdown");
        channel.setShowBadge(false);
        
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }
    
    private void startForegroundService() {
        Notification notification = createPrayerNotification();
        startForeground(NOTIFICATION_ID, notification);
    }
    
    private void startPeriodicUpdates() {
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateNotification();
                handler.postDelayed(this, 30000); // Update every 30 seconds
            }
        };
        handler.post(updateRunnable);
    }
    
    private Notification createPrayerNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        String title = "🕌 " + TranslationManager.tr("app_title");
        String content = getCurrentPrayerInfo();
        
        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(getDetailedPrayerInfo()))
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build();
    }
    
    private void updateNotification() {
        loadCurrentPrayerTimes();
        Notification notification = createPrayerNotification();
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify(NOTIFICATION_ID, notification);
    }
    
    private void loadCurrentPrayerTimes() {
        City defaultCity = CitiesData.getCityByName(SettingsManager.getDefaultCity());
        
        if (defaultCity != null) {
            PrayerTimeWorker worker = new PrayerTimeWorker(this);
            worker.loadPrayerTimes(defaultCity, new PrayerTimeWorker.PrayerTimeCallback() {
                @Override
                public void onSuccess(PrayerTimes prayerTimes) {
                    currentPrayerTimes = prayerTimes;
                }
                
                @Override
                public void onError(String error) {
                    // Use cached data or defaults
                }
                
                @Override
                public void onCachedData(PrayerTimes prayerTimes, int daysRemaining) {
                    currentPrayerTimes = prayerTimes;
                }
            });
        }
    }
    
    private String getCurrentPrayerInfo() {
        if (currentPrayerTimes == null) {
            return TranslationManager.tr("loading");
        }
        
        String currentPrayer = PrayerHighlightManager.getCurrentPrayer(currentPrayerTimes);
        String nextPrayer = PrayerHighlightManager.getNextPrayer(currentPrayerTimes);
        
        if (currentPrayer != null) {
            return "Current: " + TranslationManager.tr(currentPrayer.toLowerCase()) + 
                   " | Next: " + TranslationManager.tr(nextPrayer.toLowerCase());
        }
        
        return "Next: " + TranslationManager.tr(nextPrayer.toLowerCase());
    }
    
    private String getDetailedPrayerInfo() {
        if (currentPrayerTimes == null) {
            return TranslationManager.tr("loading");
        }
        
        StringBuilder details = new StringBuilder();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = timeFormat.format(new Date());
        
        details.append("Current Time: ").append(currentTime).append("\n\n");
        
        String[] prayers = {"Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha"};
        String[] times = {
            currentPrayerTimes.getFajr(),
            currentPrayerTimes.getSunrise(),
            currentPrayerTimes.getDhuhr(),
            currentPrayerTimes.getAsr(),
            currentPrayerTimes.getMaghrib(),
            currentPrayerTimes.getIsha()
        };
        String[] icons = {"☽", "☀", "☉", "☀", "☾", "★"};
        
        String currentPrayer = PrayerHighlightManager.getCurrentPrayer(currentPrayerTimes);
        
        for (int i = 0; i < prayers.length; i++) {
            String prayerName = TranslationManager.tr(prayers[i].toLowerCase());
            boolean isCurrent = prayers[i].equals(currentPrayer);
            
            if (isCurrent) {
                details.append("► ");
            }
            details.append(icons[i]).append(" ")
                   .append(prayerName).append(": ")
                   .append(times[i]);
            if (isCurrent) {
                details.append(" ◄");
            }
            details.append("\n");
        }
        
        return details.toString();
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