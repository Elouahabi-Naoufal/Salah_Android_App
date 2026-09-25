package com.salah.times;

import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class PrayerTimesService {
    private static final String TAG = "PrayerTimesService";
    private static final String BASE_URL = "https://www.yabiladi.com/prieres/details/%d/%s.html";
    private static final String USER_AGENT =
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    /** Fetch today's prayer times for a city (DB-first, then network). */
    public static CompletableFuture<PrayerTimes> fetchPrayerTimes(City city) {
        return CompletableFuture.supplyAsync(() -> {
            String today = todayDate();

            // 1. Try DB cache
            PrayerTimes cached = StorageManager.loadTodayFromDb(city, today);
            if (cached != null) {
                Log.d(TAG, "Cache hit: " + city.getNameEn());
                if (StorageManager.needsUpdate(city)) {
                    CompletableFuture.runAsync(() -> fetchAndStore(city));
                }
                return cached;
            }

            // 2. Fetch from network
            return fetchAndStore(city);
        });
    }

    /** Fetch full month from yabiladi, store all rows, return today's times. */
    private static PrayerTimes fetchAndStore(City city) {
        String url = String.format(Locale.US, BASE_URL, city.getId(), city.getSlug());
        Log.d(TAG, "Fetching: " + url);
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(15000)
                    .get();

            // New yabiladi layout (2026-09): 3x table.prayer-table
            //   [0] vertical today: Prière|Heure (6 rows)
            //   [1] 7-day forecast: Jour,Fajr,Dohr,Asr,Maghreb,Isha (8 rows)
            //   [2] full month: same headers, ~30+ rows  <-- we want this one
            // Fall back to legacy table.prayer / generic table for robustness.
            Element table = pickMonthTable(doc);
            if (table == null) throw new RuntimeException("prayer table not found for " + city.getNameEn());

            Elements rows = table.select("tr");
            DatabaseHelper db = StorageManager.getDb();
            String tableName = city.getTableName();

            db.getWritableDatabase().beginTransaction();
            try {
                for (int i = 1; i < rows.size(); i++) {          // skip header row
                    Elements cells = rows.get(i).select("td");
                    if (cells.size() < 6) continue;
                    String date    = normalizeDate(cells.get(0).text());  // e.g. "25/09 Aujourd'hui" -> "25/09"
                    String fajr    = cells.get(1).text().trim();
                    String dohr    = cells.get(2).text().trim();
                    String asr     = cells.get(3).text().trim();
                    String maghreb = cells.get(4).text().trim();
                    String isha    = cells.get(5).text().trim();
                    if (!isValidTime(fajr) || !isValidTime(dohr) || !isValidTime(asr)
                            || !isValidTime(maghreb) || !isValidTime(isha)) {
                        Log.w(TAG, "Skipping invalid row: " + date);
                        continue;
                    }
                    db.savePrayerTimes(tableName, date, fajr, dohr, asr, maghreb, isha);
                }
                db.getWritableDatabase().setTransactionSuccessful();
            } finally {
                db.getWritableDatabase().endTransaction();
            }

            StorageManager.markUpdated(city);
            Log.d(TAG, "Stored " + (rows.size() - 1) + " rows for " + city.getNameEn());

            String today = todayDate();
            PrayerTimes pt = StorageManager.loadTodayFromDb(city, today);
            if (pt != null) return pt;
            throw new RuntimeException("Today's row not found after fetch for " + city.getNameEn());

        } catch (Exception e) {
            Log.e(TAG, "fetchAndStore failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /** Force refresh all cities in background. */
    public static CompletableFuture<Void> forceUpdateAllCities() {
        return CompletableFuture.runAsync(() -> {
            List<City> all = CitiesData.getAllCities();
            for (City city : all) {
                try {
                    fetchAndStore(city);
                    Thread.sleep(1000);
                } catch (Exception e) {
                    Log.w(TAG, "Failed: " + city.getNameEn() + " - " + e.getMessage());
                }
            }
        });
    }

    /** Returns tomorrow's Fajr from DB if available, else fetches. */
    public static CompletableFuture<String> fetchTomorrowsFajr(City city) {
        return CompletableFuture.supplyAsync(() -> {
            String tomorrow = tomorrowDate();
            PrayerTimes pt = StorageManager.loadTodayFromDb(city, tomorrow);
            if (pt != null) return pt.getFajr();
            // Try fetching (will also populate tomorrow)
            try {
                fetchAndStore(city);
                PrayerTimes pt2 = StorageManager.loadTodayFromDb(city, tomorrow);
                if (pt2 != null) return pt2.getFajr();
            } catch (Exception ignored) {}
            return "05:30";
        });
    }

    // ── Date helpers ──────────────────────────────────────────────────────────

    /** Today as "dd/MM" matching yabiladi's date column. */
    static String todayDate() {
        return new SimpleDateFormat("dd/MM", Locale.getDefault()).format(new Date());
    }

    static String tomorrowDate() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_YEAR, 1);
        return new SimpleDateFormat("dd/MM", Locale.getDefault()).format(cal.getTime());
    }

    // ── New-layout parsing helpers ──────────────────────────────────────

    /**
     * Pick the month table: the largest table with a 6-column
     * Jour,Fajr,Dohr,Asr,Maghreb,Isha header. Prefers table.prayer-table,
     * falls back to legacy table.prayer, then any generic table.
     */
    static Element pickMonthTable(Document doc) {
        Elements candidates = doc.select("table.prayer-table");
        Element best = largestSixColTable(candidates);
        if (best != null) return best;
        Element legacy = doc.selectFirst("table.prayer");
        if (legacy != null && legacy.select("tr").size() > 1) return legacy;
        return largestSixColTable(doc.select("table"));
    }

    private static Element largestSixColTable(Elements tables) {
        Element best = null;
        int bestRows = 0;
        for (Element t : tables) {
            Elements rows = t.select("tr");
            if (rows.size() <= 1) continue;
            Elements headerCells = rows.get(0).select("th, td");
            if (headerCells.size() < 6) continue;   // skip vertical Prière|Heure table
            if (rows.size() > bestRows) {
                bestRows = rows.size();
                best = t;
            }
        }
        return best;
    }

    /**
     * "Vendredi 25/09 Aujourd'hui" / "25/09 Aujourd'hui" / "25/09" -> "25/09".
     * Falls back to trimmed raw text if no dd/MM found.
     */
    static String normalizeDate(String raw) {
        if (raw == null) return "";
        java.util.regex.Matcher m =
                java.util.regex.Pattern.compile("(\\d{1,2}/\\d{1,2})").matcher(raw);
        if (m.find()) return m.group(1);
        return raw.trim();
    }

    static boolean isValidTime(String t) {
        return t != null && t.matches("\\d{1,2}:\\d{2}");
    }
}
