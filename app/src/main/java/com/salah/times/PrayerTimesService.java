package com.salah.times;

import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PrayerTimesService {
    private static final String TAG = "PrayerTimesService";
    private static final String BASE_URL = "https://www.yabiladi.com/horaires-priere/";

    public static CompletableFuture<PrayerTimes> fetchPrayerTimes(City city) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = BASE_URL + city.getId() + "-1.html";
                Log.d(TAG, "Fetching prayer times from: " + url);
                
                Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();

                Element table = doc.select("table.horaire").first();
                if (table == null) {
                    throw new RuntimeException("Prayer times table not found");
                }

                Elements rows = table.select("tr");
                if (rows.size() < 2) {
                    throw new RuntimeException("No prayer times data found");
                }

                // Get today's prayer times (first data row)
                Element todayRow = rows.get(1);
                Elements cells = todayRow.select("td");
                
                if (cells.size() < 7) {
                    throw new RuntimeException("Incomplete prayer times data");
                }

                String date = cells.get(0).text().trim();
                String fajr = cells.get(1).text().trim();
                String sunrise = cells.get(2).text().trim();
                String dhuhr = cells.get(3).text().trim();
                String asr = cells.get(4).text().trim();
                String maghrib = cells.get(5).text().trim();
                String isha = cells.get(6).text().trim();

                return new PrayerTimes(date, fajr, sunrise, dhuhr, asr, maghrib, isha);
                
            } catch (IOException e) {
                Log.e(TAG, "Network error fetching prayer times", e);
                throw new RuntimeException("Network error: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Error parsing prayer times", e);
                throw new RuntimeException("Parsing error: " + e.getMessage());
            }
        });
    }
}