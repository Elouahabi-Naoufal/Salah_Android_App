package com.salah.times;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class PrayerAlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "prayer_alerts";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String prayerName = intent.getStringExtra("prayer_name");
        if (prayerName == null) return;
        
        // Check if notifications are enabled
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        boolean notificationsEnabled = Boolean.parseBoolean(db.getSetting("notifications_enabled", "true"));
        
        if (!notificationsEnabled) {
            return; // Don't show notification if disabled
        }
        
        createNotificationChannel(context);
        showPrayerNotification(context, prayerName);
    }
    
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Prayer Alerts",
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for prayer times");
            channel.enableVibration(true);
            
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
    
    private void showPrayerNotification(Context context, String prayerName) {
        String translatedPrayer = TranslationManager.tr("prayers." + prayerName.toLowerCase());
        String title = TranslationManager.tr("notifications.prayer_time");
        String message = TranslationManager.tr("notifications.prayer_now", translatedPrayer);
        
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(notificationSound)
            .setVibrate(new long[]{0, 500, 200, 500});
        
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(prayerName.hashCode(), builder.build());
        }
    }
}
