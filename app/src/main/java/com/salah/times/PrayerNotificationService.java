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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PrayerNotificationService extends Service {
    private static final String CHANNEL_ID = "prayer_persistent";
    private static final int NOTIFICATION_ID = 1000;

    private final Handler handler = new Handler();
    private Runnable updateRunnable;
    private PrayerTimes currentPrayerTimes;

    @Override
    public void onCreate() {
        super.onCreate();
        createChannel();
        startForeground(NOTIFICATION_ID, buildNotification());
        scheduleUpdates();
    }

    private void createChannel() {
        NotificationChannel ch = new NotificationChannel(
                CHANNEL_ID,
                "Prayer Times",
                NotificationManager.IMPORTANCE_LOW   // silent, no heads-up, no sound
        );
        ch.setDescription("");
        ch.setShowBadge(false);
        ch.setSound(null, null);
        ch.enableVibration(false);
        ch.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getSystemService(NotificationManager.class).createNotificationChannel(ch);
    }

    private void scheduleUpdates() {
        updateRunnable = new Runnable() {
            @Override public void run() {
                loadPrayerTimes();
                getSystemService(NotificationManager.class)
                        .notify(NOTIFICATION_ID, buildNotification());
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateRunnable);
    }

    private Notification buildNotification() {
        PendingIntent tap = PendingIntent.getActivity(
                this, 0,
                new Intent(this, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String nextPrayer  = getNextPrayerName();
        String nextTime    = getNextPrayerTime();
        String countdown   = getCountdown();
        String cityName    = getCityName();

        // Title:  "Fajr · 05:23"
        // Text:   "Casablanca  ·  in 02:14:38"
        String title = nextPrayer + "  ·  " + nextTime;
        String text  = TranslationManager.tr("next_prayer") + ": " + nextPrayer + "  ·  " + countdown;

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(tap)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .setSilent(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                // Expanded view shows all 5 prayers as BigTextStyle lines
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(buildExpandedText())
                        .setSummaryText(cityName))
                .build();
    }

    /** Builds the expanded text: all 5 prayer times on one block. */
    private String buildExpandedText() {
        if (currentPrayerTimes == null) return TranslationManager.tr("loading");
        String current = PrayerHighlightManager.getCurrentPrayer(currentPrayerTimes);
        StringBuilder sb = new StringBuilder();
        String[][] prayers = {
            {"Fajr",    currentPrayerTimes.getFajr()},
            {"Dohr",    currentPrayerTimes.getDhuhr()},
            {"Asr",     currentPrayerTimes.getAsr()},
            {"Maghreb", currentPrayerTimes.getMaghrib()},
            {"Isha",    currentPrayerTimes.getIsha()}
        };
        for (String[] p : prayers) {
            boolean isCurrent = p[0].equals(current);
            sb.append(isCurrent ? "▶ " : "    ");
            sb.append(TranslationManager.tr("prayers." + p[0].toLowerCase()));
            sb.append("  ");
            sb.append(p[1]);
            sb.append("\n");
        }
        return sb.toString().trim();
    }

    // ── Data helpers ──────────────────────────────────────────────────────────

    private void loadPrayerTimes() {
        try {
            City city = CitiesData.getCityByName(SettingsManager.getDefaultCity());
            if (city == null) return;
            String today = new SimpleDateFormat("dd/MM", Locale.getDefault()).format(new Date());
            currentPrayerTimes = DatabaseHelper.getInstance(this)
                    .loadPrayerTimes(city.getTableName(), today);
        } catch (Exception e) {
            android.util.Log.e("PrayerNotif", "load failed", e);
        }
    }

    private String getNextPrayerName() {
        if (currentPrayerTimes == null) return "—";
        String p = PrayerHighlightManager.getNextPrayer(currentPrayerTimes);
        return TranslationManager.tr("prayers." + p.toLowerCase());
    }

    private String getNextPrayerTime() {
        if (currentPrayerTimes == null) return "--:--";
        return timeForPrayer(PrayerHighlightManager.getNextPrayer(currentPrayerTimes));
    }

    private String timeForPrayer(String prayer) {
        if (currentPrayerTimes == null) return "--:--";
        switch (prayer) {
            case "Fajr":    return currentPrayerTimes.getFajr();
            case "Dohr":    return currentPrayerTimes.getDhuhr();
            case "Asr":     return currentPrayerTimes.getAsr();
            case "Maghreb": return currentPrayerTimes.getMaghrib();
            case "Isha":    return currentPrayerTimes.getIsha();
            default:        return "--:--";
        }
    }

    private String getCountdown() {
        if (currentPrayerTimes == null) return "--:--:--";
        try {
            Calendar now = Calendar.getInstance();
            int curMin = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);
            int curSec = now.get(Calendar.SECOND);

            String next = PrayerHighlightManager.getNextPrayer(currentPrayerTimes);
            String[] parts = timeForPrayer(next).split(":");
            int prayerMin = Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);

            int remMin = prayerMin - curMin;
            if (remMin < 0) remMin += 24 * 60;

            int h = remMin / 60;
            int m = remMin % 60;
            int s = 60 - curSec;
            if (s == 60) { s = 0; } else if (m > 0) { m--; }

            return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s);
        } catch (Exception e) {
            return "--:--:--";
        }
    }

    private String getCityName() {
        City city = CitiesData.getCityByName(SettingsManager.getDefaultCity());
        if (city == null) return SettingsManager.getDefaultCity();
        return city.getName(TranslationManager.getCurrentLanguage());
    }

    @Override public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (updateRunnable != null) handler.removeCallbacks(updateRunnable);
    }
}
