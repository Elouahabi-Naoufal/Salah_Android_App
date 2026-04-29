package com.salah.times;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.List;

public class ScrapeAllCitiesService extends Service {
    private static final String TAG = "ScrapeAllCities";
    private static final String CHANNEL_ID = "scrape_channel";
    private static final int NOTIF_ID = 9001;
    private static final String USER_AGENT =
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    private static final String BASE_URL = "https://www.yabiladi.com/prieres/details/%d/%s.html";

    public static final String ACTION_DONE   = "com.salah.times.SCRAPE_DONE";
    public static final String EXTRA_SUCCESS = "success_count";
    public static final String EXTRA_FAILED  = "failed_count";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createChannel();
        startForeground(NOTIF_ID, buildNotification("Starting...", 0, 0));
        new Thread(this::scrapeAll).start();
        return START_NOT_STICKY;
    }

    private void scrapeAll() {
        List<City> cities = CitiesData.getAllCities();
        int total = cities.size();
        int success = 0, failed = 0;

        DatabaseHelper db = DatabaseHelper.getInstance(this);

        for (int i = 0; i < total; i++) {
            City city = cities.get(i);
            String tableName = city.getTableName();
            String url = String.format(java.util.Locale.US, BASE_URL, city.getId(), city.getSlug());

            notify("Scraping " + city.getNameEn() + " (" + (i + 1) + "/" + total + ")", i + 1, total);
            Log.d(TAG, "Fetching: " + url);

            try {
                org.jsoup.nodes.Document doc = Jsoup.connect(url)
                        .userAgent(USER_AGENT)
                        .timeout(15000)
                        .get();

                Element table = doc.selectFirst("table.prayer");
                if (table == null) {
                    Log.w(TAG, "SKIP " + city.getNameEn() + " - table.prayer not found");
                    failed++;
                    continue;
                }

                Elements rows = table.select("tr");
                android.database.sqlite.SQLiteDatabase sqlDb = db.getWritableDatabase();
                db.ensureCityTable(sqlDb, tableName);

                // DELETE existing rows so re-scrape is clean
                sqlDb.delete(tableName, null, null);

                sqlDb.beginTransaction();
                try {
                    int rowCount = 0;
                    for (int r = 1; r < rows.size(); r++) {   // skip header
                        Elements cells = rows.get(r).select("td");
                        if (cells.size() < 6) continue;
                        db.savePrayerTimes(
                                tableName,
                                cells.get(0).text().trim(),   // date  e.g. "15/07"
                                cells.get(1).text().trim(),   // fajr
                                cells.get(2).text().trim(),   // dohr
                                cells.get(3).text().trim(),   // asr
                                cells.get(4).text().trim(),   // maghreb
                                cells.get(5).text().trim()    // isha
                        );
                        rowCount++;
                    }
                    sqlDb.setTransactionSuccessful();
                    Log.d(TAG, "OK  " + city.getNameEn() + " -> " + rowCount + " rows");
                    success++;
                } finally {
                    sqlDb.endTransaction();
                }

                StorageManager.markUpdated(city);
                Thread.sleep(1000);   // exact same 1s delay as Python script

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                Log.w(TAG, "FAIL " + city.getNameEn() + ": " + e.getMessage());
                failed++;
            }
        }

        Log.i(TAG, "Done! " + success + "/" + total + " cities scraped, " + failed + " failed");

        // Broadcast result back to MainActivity
        Intent done = new Intent(ACTION_DONE);
        done.putExtra(EXTRA_SUCCESS, success);
        done.putExtra(EXTRA_FAILED, failed);
        sendBroadcast(done);

        notify("Done: " + success + "/" + total + " cities updated", total, total);
        stopForeground(false);
        stopSelf();
    }

    // ── Notification helpers ──────────────────────────────────────────────────

    private void createChannel() {
        NotificationChannel ch = new NotificationChannel(
                CHANNEL_ID, "DB Scrape", NotificationManager.IMPORTANCE_LOW);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(ch);
    }

    private Notification buildNotification(String text, int progress, int max) {
        NotificationCompat.Builder b = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Updating prayer times DB")
                .setContentText(text)
                .setSmallIcon(android.R.drawable.ic_popup_sync)
                .setOngoing(true)
                .setOnlyAlertOnce(true);
        if (max > 0) b.setProgress(max, progress, false);
        return b.build();
    }

    private void notify(String text, int progress, int max) {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIF_ID, buildNotification(text, progress, max));
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
