package com.salah.times;

import android.content.Context;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StorageManager {
    private static DatabaseHelper db;

    public static void init(Context context) {
        db = DatabaseHelper.getInstance(context.getApplicationContext());
    }

    static DatabaseHelper getDb() { return db; }

    /** Load prayer times for a city on a given date (format "dd/MM"). */
    public static PrayerTimes loadTodayFromDb(City city, String date) {
        return db.loadPrayerTimes(city.getTableName(), date);
    }

    /** Returns true if the city's table has no rows (never fetched). */
    public static boolean needsUpdate(City city) {
        return db.countRowsForCity(city.getTableName()) == 0;
    }

    /** Record that this city was just updated (store today's date in settings). */
    public static void markUpdated(City city) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        db.saveSetting("last_update_" + city.getTableName(), today);
    }

    // ── Kept for compatibility with existing callers ───────────────────────────

    public static boolean hasCityData(String cityName) {
        City city = CitiesData.getCityByName(cityName);
        return city != null && db.countRowsForCity(city.getTableName()) > 0;
    }

    public static void clearAllCityData() {
        // Drop all per-city tables by iterating known cities
        android.database.sqlite.SQLiteDatabase sqlDb = db.getWritableDatabase();
        for (City city : CitiesData.getAllCities()) {
            sqlDb.execSQL("DROP TABLE IF EXISTS " + city.getTableName());
        }
    }
}
