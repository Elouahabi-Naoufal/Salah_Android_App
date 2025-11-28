package com.salah.times;

import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationManager {
    private static final String CHANNEL_ID = "prayer_times";
    private static final int NOTIFICATION_ID = 1001;
    private Context context;
    private android.app.NotificationManager notificationManager;
    
    public NotificationManager(Context context) {
        this.context = context;
        this.notificationManager = (android.app.NotificationManager) 
            context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }
    
    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
            CHANNEL_ID,
            "Prayer Times",
            android.app.NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Prayer time notifications");
        channel.enableVibration(true);
        channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), null);
        notificationManager.createNotificationChannel(channel);
    }
    
    public void showPrayerNotification(String prayerName, String time) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🔔 " + TranslationManager.tr(prayerName.toLowerCase()) + " Time")
            .setContentText("Prayer time: " + time)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            .setVibrate(new long[]{0, 500, 200, 500});
        
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
    
    public void cancelNotifications() {
        notificationManager.cancel(NOTIFICATION_ID);
    }
    
    public static void showAdhanNotification(Context context, String prayer) {
        android.app.NotificationManager notificationManager = 
            (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🕌 Adhan - " + prayer.substring(0, 1).toUpperCase() + prayer.substring(1))
            .setContentText("It's time for " + prayer + " prayer")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(new long[]{0, 1000, 500, 1000});
        
        notificationManager.notify(2000 + prayer.hashCode(), builder.build());
    }
}