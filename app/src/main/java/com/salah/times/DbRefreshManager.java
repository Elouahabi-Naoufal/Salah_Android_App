package com.salah.times;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import org.json.JSONObject;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Tracks days since last DB refresh.
 * Counter file: {filesDir}/db_refresh_counter.json
 * Schema: {"count": 5, "last_checked": "2026-04-28T12:00:00"}
 *
 * On each app open:
 *   - If today != last_checked date → increment count, update last_checked
 *   - If count >= 30 → show "30 days passed, refresh?" prompt
 *
 * On DB refresh (manual or forced): reset count to 0.
 */
public class DbRefreshManager {
    private static final String TAG = "DbRefreshManager";
    private static final String FILE_NAME = "db_refresh_counter.json";
    private static final int REFRESH_THRESHOLD = 30;

    private final Context context;
    private final File counterFile;

    public DbRefreshManager(Context context) {
        this.context = context.getApplicationContext();
        this.counterFile = new File(this.context.getFilesDir(), FILE_NAME);
    }

    /** Call on every app open (e.g. onResume of MainActivity). */
    public void checkOnOpen() {
        try {
            JSONObject state = load();
            String today = todayDateStr();
            String lastChecked = state.optString("last_checked", "");
            int count = state.optInt("count", 0);

            if (!today.equals(lastChecked)) {
                count++;
                state.put("count", count);
                state.put("last_checked", nowTimestamp());
                save(state);
                Log.d(TAG, "Day incremented, count=" + count);
            }
        } catch (Exception e) {
            Log.e(TAG, "checkOnOpen failed", e);
        }
    }

    /** Returns true if count >= 30 and a prompt should be shown. */
    public boolean shouldPromptRefresh() {
        try {
            return load().optInt("count", 0) >= REFRESH_THRESHOLD;
        } catch (Exception e) {
            return false;
        }
    }

    /** Reset counter to 0 (call after successful DB refresh). */
    public void resetCounter() {
        try {
            JSONObject state = new JSONObject();
            state.put("count", 0);
            state.put("last_checked", nowTimestamp());
            save(state);
            Log.d(TAG, "Counter reset to 0");
        } catch (Exception e) {
            Log.e(TAG, "resetCounter failed", e);
        }
    }

    /**
     * Show the 30-day prompt dialog.
     * onConfirm is run on the calling thread (wrap in background if needed).
     */
    public void showRefreshPrompt(android.app.Activity activity, Runnable onConfirm) {
        activity.runOnUiThread(() ->
            new AlertDialog.Builder(activity)
                .setTitle(TranslationManager.tr("refresh_db_prompt_title"))
                .setMessage(TranslationManager.tr("refresh_db_prompt_msg"))
                .setPositiveButton(TranslationManager.tr("yes"), (d, w) -> onConfirm.run())
                .setNegativeButton(TranslationManager.tr("no"), null)
                .show()
        );
    }

    /**
     * Show no-internet dialog with DB date range info.
     * Queries the first city's table for first/last date.
     */
    public void showNoInternetDialog(android.app.Activity activity) {
        String[] range = getDbDateRange();
        int cityCount = CitiesData.getAllCities().size();
        String msg = TranslationManager.tr("no_internet_msg")
                .replace("{cities}", String.valueOf(cityCount))
                .replace("{first_date}", range[0])
                .replace("{last_date}", range[1]);

        activity.runOnUiThread(() ->
            new AlertDialog.Builder(activity)
                .setTitle(TranslationManager.tr("no_internet_title"))
                .setMessage(msg)
                .setPositiveButton(TranslationManager.tr("ok"), null)
                .show()
        );
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /** Query any city table for first and last date. Returns ["?", "?"] on failure. */
    private String[] getDbDateRange() {
        try {
            DatabaseHelper db = DatabaseHelper.getInstance(context);
            // Use first city in the list
            City city = CitiesData.getAllCities().get(0);
            String table = city.getTableName();
            db.ensureCityTable(db.getReadableDatabase(), table);

            android.database.Cursor c1 = db.getReadableDatabase()
                    .rawQuery("SELECT date FROM " + table + " ORDER BY rowid ASC LIMIT 1", null);
            String first = "?";
            if (c1 != null && c1.moveToFirst()) { first = c1.getString(0); c1.close(); }

            android.database.Cursor c2 = db.getReadableDatabase()
                    .rawQuery("SELECT date FROM " + table + " ORDER BY rowid DESC LIMIT 1", null);
            String last = "?";
            if (c2 != null && c2.moveToFirst()) { last = c2.getString(0); c2.close(); }

            return new String[]{first, last};
        } catch (Exception e) {
            return new String[]{"?", "?"};
        }
    }

    private JSONObject load() throws Exception {
        if (!counterFile.exists()) return new JSONObject();
        StringBuilder sb = new StringBuilder();
        FileReader fr = new FileReader(counterFile);
        char[] buf = new char[512];
        int n;
        while ((n = fr.read(buf)) != -1) sb.append(buf, 0, n);
        fr.close();
        return new JSONObject(sb.toString());
    }

    private void save(JSONObject obj) throws Exception {
        FileWriter fw = new FileWriter(counterFile, false);
        fw.write(obj.toString());
        fw.close();
    }

    private static String todayDateStr() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    private static String nowTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(new Date());
    }
}
