package com.salah.times;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.json.JSONObject;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "salah_times.db";
    private static final int DATABASE_VERSION = 2;
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
        db.execSQL("CREATE TABLE settings (key TEXT PRIMARY KEY, value TEXT, updated_at INTEGER)");
        db.execSQL("CREATE TABLE prayer_times (id INTEGER PRIMARY KEY AUTOINCREMENT, city_name TEXT NOT NULL, date TEXT NOT NULL, fajr TEXT, sunrise TEXT, dhuhr TEXT, asr TEXT, maghrib TEXT, isha TEXT, last_updated INTEGER, UNIQUE(city_name, date))");
        db.execSQL("CREATE TABLE iqama_delays (prayer TEXT PRIMARY KEY, delay_minutes INTEGER)");
        db.execSQL("CREATE TABLE prayer_alarms (prayer TEXT PRIMARY KEY, enabled INTEGER DEFAULT 1)");
        db.execSQL("CREATE TABLE update_tracking (id INTEGER PRIMARY KEY, last_update_date TEXT)");
        db.execSQL("CREATE TABLE alarm_tracking (date TEXT PRIMARY KEY, alarms_set INTEGER DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS update_tracking");
            db.execSQL("CREATE TABLE update_tracking (id INTEGER PRIMARY KEY, last_update_date TEXT)");
        }
    }

    public void saveSetting(String key, String value) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("key", key);
        values.put("value", value);
        values.put("updated_at", System.currentTimeMillis());
        db.insertWithOnConflict("settings", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public String getSetting(String key, String defaultValue) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("settings", new String[]{"value"}, "key = ?", new String[]{key}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String value = cursor.getString(0);
            cursor.close();
            return value;
        }
        if (cursor != null) cursor.close();
        return defaultValue;
    }

    public void savePrayerTimes(String cityName, String date, String fajr, String sunrise, String dhuhr, String asr, String maghrib, String isha) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("city_name", cityName);
        values.put("date", date);
        values.put("fajr", fajr);
        values.put("sunrise", sunrise);
        values.put("dhuhr", dhuhr);
        values.put("asr", asr);
        values.put("maghrib", maghrib);
        values.put("isha", isha);
        values.put("last_updated", System.currentTimeMillis());
        db.insertWithOnConflict("prayer_times", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public PrayerTimes loadPrayerTimes(String cityName, String date) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("prayer_times", null, "city_name = ? AND date = ?", new String[]{cityName, date}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            PrayerTimes times = new PrayerTimes(
                cursor.getString(cursor.getColumnIndexOrThrow("date")),
                cursor.getString(cursor.getColumnIndexOrThrow("fajr")),
                cursor.getString(cursor.getColumnIndexOrThrow("sunrise")),
                cursor.getString(cursor.getColumnIndexOrThrow("dhuhr")),
                cursor.getString(cursor.getColumnIndexOrThrow("asr")),
                cursor.getString(cursor.getColumnIndexOrThrow("maghrib")),
                cursor.getString(cursor.getColumnIndexOrThrow("isha"))
            );
            cursor.close();
            return times;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public void setIqamaDelay(String prayer, int minutes) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("prayer", prayer.toLowerCase());
        values.put("delay_minutes", minutes);
        db.insertWithOnConflict("iqama_delays", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public int getIqamaDelay(String prayer, int defaultValue) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("iqama_delays", new String[]{"delay_minutes"}, "prayer = ?", new String[]{prayer.toLowerCase()}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int delay = cursor.getInt(0);
            cursor.close();
            return delay;
        }
        if (cursor != null) cursor.close();
        return defaultValue;
    }

    public void setPrayerAlarmEnabled(String prayer, boolean enabled) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("prayer", prayer.toLowerCase());
        values.put("enabled", enabled ? 1 : 0);
        db.insertWithOnConflict("prayer_alarms", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public boolean getPrayerAlarmEnabled(String prayer, boolean defaultValue) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("prayer_alarms", new String[]{"enabled"}, "prayer = ?", new String[]{prayer.toLowerCase()}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            boolean enabled = cursor.getInt(0) == 1;
            cursor.close();
            return enabled;
        }
        if (cursor != null) cursor.close();
        return defaultValue;
    }

    public void markAlarmsSet(String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("alarms_set", 1);
        db.insertWithOnConflict("alarm_tracking", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public boolean areAlarmsSet(String date) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("alarm_tracking", new String[]{"alarms_set"}, "date = ?", new String[]{date}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            boolean set = cursor.getInt(0) == 1;
            cursor.close();
            return set;
        }
        if (cursor != null) cursor.close();
        return false;
    }

    public void clearAllData() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM prayer_times");
        db.execSQL("DELETE FROM update_tracking");
    }
    
    public void setLastUpdateDate(String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", 1);
        values.put("last_update_date", date);
        db.insertWithOnConflict("update_tracking", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
    
    public String getLastUpdateDate() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("update_tracking", new String[]{"last_update_date"}, "id = 1", null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String date = cursor.getString(0);
            cursor.close();
            return date;
        }
        if (cursor != null) cursor.close();
        return null;
    }
}
