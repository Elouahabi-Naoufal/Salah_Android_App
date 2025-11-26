package com.salah.times;

import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class PrayerTimesService {
    private static final String TAG = "PrayerTimesService";
    private static final String BASE_URL = "https://www.yabiladi.com/prieres/details/";

    public static CompletableFuture<PrayerTimes> fetchPrayerTimes(City city) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = BASE_URL + city.getId() + "/city.html";
                Log.d(TAG, "Fetching prayer times from: " + url);
                
                Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();

                // Find prayer times table
                Elements tables = doc.select("table");
                Log.d(TAG, "Found " + tables.size() + " tables");
                
                Element prayerTable = null;
                for (Element table : tables) {
                    Elements rows = table.select("tr");
                    if (rows.size() > 1) {
                        Elements firstRowCells = rows.get(1).select("td");
                        if (firstRowCells.size() >= 5) {
                            prayerTable = table;
                            break;
                        }
                    }
                }
                
                if (prayerTable == null) {
                    throw new RuntimeException("No suitable prayer times table found");
                }
                
                Elements rows = prayerTable.select("tr");
                if (rows.size() < 2) {
                    throw new RuntimeException("Table has no data rows");
                }
                
                // Use first data row (today's times)
                Elements cells = rows.get(1).select("td");
                Log.d(TAG, "Using first data row with " + cells.size() + " cells");
                
                // Log all cell contents for debugging
                for (int i = 0; i < cells.size(); i++) {
                    Log.d(TAG, "Cell " + i + ": " + cells.get(i).text().trim());
                }
                
                // Extract prayer times - Date, Fajr, Dohr, Asr, Maghreb, Isha
                String date = cells.size() > 0 ? cells.get(0).text().trim() : getCurrentDate();
                String fajr = cells.size() > 1 ? cells.get(1).text().trim() : "05:30";
                String dohr = cells.size() > 2 ? cells.get(2).text().trim() : "13:00";
                String asr = cells.size() > 3 ? cells.get(3).text().trim() : "16:30";
                String maghreb = cells.size() > 4 ? cells.get(4).text().trim() : "19:00";
                String isha = cells.size() > 5 ? cells.get(5).text().trim() : "20:30";
                
                Log.d(TAG, "Extracted times - Date: " + date + ", Fajr: " + fajr + ", Dohr: " + dohr + ", Asr: " + asr + ", Maghreb: " + maghreb + ", Isha: " + isha);
                
                // Validate times
                if (!isValidTime(fajr)) {
                    Log.w(TAG, "Invalid Fajr time: " + fajr);
                    fajr = "05:30";
                }
                if (!isValidTime(dohr)) {
                    Log.w(TAG, "Invalid Dohr time: " + dohr);
                    dohr = "13:00";
                }
                if (!isValidTime(asr)) {
                    Log.w(TAG, "Invalid Asr time: " + asr);
                    asr = "16:30";
                }
                if (!isValidTime(maghreb)) {
                    Log.w(TAG, "Invalid Maghreb time: " + maghreb);
                    maghreb = "19:00";
                }
                if (!isValidTime(isha)) {
                    Log.w(TAG, "Invalid Isha time: " + isha);
                    isha = "20:30";
                }

                PrayerTimes result = new PrayerTimes(date, fajr, "00:00", dohr, asr, maghreb, isha);
                Log.d(TAG, "Successfully created PrayerTimes object");
                return result;
                
            } catch (IOException e) {
                Log.e(TAG, "Network error fetching prayer times", e);
                throw new RuntimeException("Network error: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Error parsing prayer times", e);
                throw new RuntimeException("Parsing error: " + e.getMessage());
            }
        });
    }

    
    private static boolean isValidTime(String time) {
        return time != null && time.matches("\\d{1,2}:\\d{2}");
    }
    
    private static String getCurrentDate() {
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM", java.util.Locale.getDefault());
        return dateFormat.format(new java.util.Date());
    }
}