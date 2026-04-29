package com.salah.times;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "salah.db";
    private static final int DATABASE_VERSION = 3;
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE settings (key TEXT PRIMARY KEY, value TEXT)");
        db.execSQL("CREATE TABLE iqama_delays (prayer TEXT PRIMARY KEY, delay_minutes INTEGER)");
        // Per-city tables are created on demand in ensureCityTable()
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop old monolithic prayer_times table if upgrading
        db.execSQL("DROP TABLE IF EXISTS prayer_times");
        db.execSQL("DROP TABLE IF EXISTS update_tracking");
        onCreate(db);
    }

    /** Creates the per-city table if it doesn't exist yet. */
    public void ensureCityTable(SQLiteDatabase db, String tableName) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName +
                " (date TEXT PRIMARY KEY, fajr TEXT, dohr TEXT, asr TEXT, maghreb TEXT, isha TEXT)");
    }

    // ── Settings ─────────────────────────────────────────────────────────────

    public void saveSetting(String key, String value) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("key", key);
        v.put("value", value);
        db.insertWithOnConflict("settings", null, v, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public String getSetting(String key, String defaultValue) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("settings", new String[]{"value"}, "key=?", new String[]{key}, null, null, null);
        if (c != null && c.moveToFirst()) {
            String val = c.getString(0);
            c.close();
            return val;
        }
        if (c != null) c.close();
        return defaultValue;
    }

    // ── Per-city prayer times ─────────────────────────────────────────────────

    /**
     * Insert or replace a row in the city's table.
     * @param tableName city.getTableName()  e.g. "al_hoceima"
     */
    public void savePrayerTimes(String tableName, String date, String fajr,
                                String dohr, String asr, String maghreb, String isha) {
        SQLiteDatabase db = getWritableDatabase();
        ensureCityTable(db, tableName);
        ContentValues v = new ContentValues();
        v.put("date",    date);
        v.put("fajr",    fajr);
        v.put("dohr",    dohr);
        v.put("asr",     asr);
        v.put("maghreb", maghreb);
        v.put("isha",    isha);
        db.insertWithOnConflict(tableName, null, v, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Load prayer times for a specific date from the city's table.
     * Returns null if not found.
     */
    public PrayerTimes loadPrayerTimes(String tableName, String date) {
        SQLiteDatabase db = getReadableDatabase();
        ensureCityTable(db, tableName);
        Cursor c = db.query(tableName, null, "date=?", new String[]{date}, null, null, null);
        if (c != null && c.moveToFirst()) {
            PrayerTimes pt = new PrayerTimes(
                c.getString(c.getColumnIndexOrThrow("date")),
                c.getString(c.getColumnIndexOrThrow("fajr")),
                "00:00",
                c.getString(c.getColumnIndexOrThrow("dohr")),
                c.getString(c.getColumnIndexOrThrow("asr")),
                c.getString(c.getColumnIndexOrThrow("maghreb")),
                c.getString(c.getColumnIndexOrThrow("isha"))
            );
            c.close();
            return pt;
        }
        if (c != null) c.close();
        return null;
    }

    public int countRowsForCity(String tableName) {
        SQLiteDatabase db = getReadableDatabase();
        ensureCityTable(db, tableName);
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
        int count = 0;
        if (c != null && c.moveToFirst()) { count = c.getInt(0); c.close(); }
        return count;
    }

    // ── Iqama delays ──────────────────────────────────────────────────────────

    public void setIqamaDelay(String prayer, int minutes) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("prayer", prayer.toLowerCase());
        v.put("delay_minutes", minutes);
        db.insertWithOnConflict("iqama_delays", null, v, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public int getIqamaDelay(String prayer, int defaultValue) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("iqama_delays", new String[]{"delay_minutes"},
                "prayer=?", new String[]{prayer.toLowerCase()}, null, null, null);
        if (c != null && c.moveToFirst()) {
            int d = c.getInt(0); c.close(); return d;
        }
        if (c != null) c.close();
        return defaultValue;
    }
}
