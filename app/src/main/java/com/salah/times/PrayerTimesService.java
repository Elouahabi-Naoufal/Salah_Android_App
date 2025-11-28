package com.salah.times;

import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PrayerTimesService {
    private static final String TAG = "PrayerTimesService";
    private static final String BASE_URL = "https://www.yabiladi.com/prieres/details/";

    public static CompletableFuture<PrayerTimes> fetchPrayerTimes(City city) {
        return CompletableFuture.supplyAsync(() -> {
            // First try to load from cache
            JSONObject cachedData = StorageManager.loadCityData(city.getNameEn());
            if (cachedData != null) {
                try {
                    String today = new SimpleDateFormat("dd/MM", Locale.getDefault()).format(new Date());
                    JSONObject prayerTimes = cachedData.getJSONObject("prayer_times");
                    if (prayerTimes.has(today)) {
                        JSONObject todayPrayers = prayerTimes.getJSONObject(today);
                        PrayerTimes prayers = new PrayerTimes(
                            todayPrayers.getString("Date"),
                            todayPrayers.getString("Fajr"),
                            "00:00",
                            todayPrayers.getString("Dohr"),
                            todayPrayers.getString("Asr"),
                            todayPrayers.getString("Maghreb"),
                            todayPrayers.getString("Isha")
                        );
                        Log.d(TAG, "Loaded from cache: " + city.getNameEn());
                        
                        // Start background update if needed
                        checkAndUpdateAllCitiesInBackground();
                        return prayers;
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Cache load failed, fetching from web");
                }
            }
            
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

                // Save full month data to storage
                saveFullMonthData(doc, city.getNameEn());
                
                // Start background update if needed
                checkAndUpdateAllCitiesInBackground();
                
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
    
    private static void saveFullMonthData(Document doc, String cityName) {
        try {
            Element table = null;
            Elements tables = doc.select("table");
            for (Element t : tables) {
                Elements rows = t.select("tr");
                if (rows.size() > 1) {
                    table = t;
                    break;
                }
            }
            
            if (table == null) return;
            
            JSONObject monthData = new JSONObject();
            Elements rows = table.select("tr");
            
            for (int i = 1; i < rows.size(); i++) {
                Elements cols = rows.get(i).select("td");
                if (cols.size() >= 6) {
                    String date = cols.get(0).text().trim();
                    JSONObject dayPrayers = new JSONObject();
                    dayPrayers.put("Date", date);
                    dayPrayers.put("Fajr", cols.get(1).text().trim());
                    dayPrayers.put("Dohr", cols.get(2).text().trim());
                    dayPrayers.put("Asr", cols.get(3).text().trim());
                    dayPrayers.put("Maghreb", cols.get(4).text().trim());
                    dayPrayers.put("Isha", cols.get(5).text().trim());
                    monthData.put(date, dayPrayers);
                }
            }
            
            StorageManager.saveCityData(cityName, monthData);
            StorageManager.saveLastUpdate();
            Log.d(TAG, "Saved month data for " + cityName);
        } catch (Exception e) {
            Log.e(TAG, "Error saving month data", e);
        }
    }
    
    private static void checkAndUpdateAllCitiesInBackground() {
        CompletableFuture.runAsync(() -> {
            int cityCount = StorageManager.countCityFiles();
            boolean dateNeedsUpdate = StorageManager.shouldUpdateToday();
            boolean citiesIncomplete = cityCount < 42;
            
            // Always update if cities are incomplete or date is outdated
            if (citiesIncomplete || dateNeedsUpdate) {
                Log.d(TAG, "Starting background update of all cities... (Current: " + cityCount + "/42, Date check: " + dateNeedsUpdate + ")");
                updateAllCitiesDatabase();
                
                // Keep updating until we have all 42 cities
                while (StorageManager.countCityFiles() < 42) {
                    Log.d(TAG, "Still missing cities, retrying... (" + StorageManager.countCityFiles() + "/42)");
                    try {
                        Thread.sleep(5000); // Wait 5 seconds before retry
                        updateAllCitiesDatabase();
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            } else {
                Log.d(TAG, "All cities up to date (" + cityCount + "/42)");
            }
        });
    }
    
    public static CompletableFuture<Void> forceUpdateAllCities() {
        return CompletableFuture.runAsync(() -> {
            Log.i(TAG, "Force updating all cities database...");
            updateAllCitiesDatabase();
        });
    }
    
    private static void updateAllCitiesDatabase() {
        int successCount = 0;
        java.util.List<String> failedCities = new java.util.ArrayList<>();
        java.util.List<City> allCities = CitiesData.getAllCities();
        
        Log.i(TAG, "Starting update of " + allCities.size() + " cities...");
        
        for (City city : allCities) {
            try {
                String url = BASE_URL + city.getId() + "/city.html";
                Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(15000)
                    .get();
                
                saveFullMonthData(doc, city.getNameEn());
                successCount++;
                Log.d(TAG, "✓ Updated " + city.getNameEn() + " (" + successCount + "/" + allCities.size() + ")");
                
                // Small delay to avoid overwhelming the server
                Thread.sleep(200);
                
            } catch (Exception e) {
                failedCities.add(city.getNameEn());
                Log.w(TAG, "✗ Failed to update " + city.getNameEn() + ": " + e.getMessage());
            }
        }
        
        // Count actual files in cities folder
        int actualFiles = StorageManager.countCityFiles();
        
        // Update last_update.json with results
        StorageManager.updateLastUpdateWithResults(actualFiles, failedCities);
        
        Log.i(TAG, "Database update completed: " + actualFiles + "/" + allCities.size() + " cities successfully updated");
        if (!failedCities.isEmpty()) {
            Log.w(TAG, "Failed cities: " + String.join(", ", failedCities));
        }
    }

    
    private static boolean isValidTime(String time) {
        return time != null && time.matches("\\d{1,2}:\\d{2}");
    }
    
    private static String getCurrentDate() {
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM", java.util.Locale.getDefault());
        return dateFormat.format(new java.util.Date());
    }
    
    public static CompletableFuture<String> fetchTomorrowsFajr(City city) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = BASE_URL + city.getId() + "/city.html";
                Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();

                Elements tables = doc.select("table");
                Element prayerTable = null;
                for (Element table : tables) {
                    Elements rows = table.select("tr");
                    if (rows.size() > 2) { // Need at least header + today + tomorrow
                        prayerTable = table;
                        break;
                    }
                }
                
                if (prayerTable == null) {
                    return "05:30"; // Fallback
                }
                
                Elements rows = prayerTable.select("tr");
                if (rows.size() < 3) {
                    return "05:30"; // Fallback
                }
                
                // Get tomorrow's row (third row: header, today, tomorrow)
                Elements tomorrowCells = rows.get(2).select("td");
                if (tomorrowCells.size() > 1) {
                    String tomorrowFajr = tomorrowCells.get(1).text().trim();
                    if (isValidTime(tomorrowFajr)) {
                        return tomorrowFajr;
                    }
                }
                
                return "05:30"; // Fallback
                
            } catch (Exception e) {
                Log.e(TAG, "Error fetching tomorrow's Fajr", e);
                return "05:30"; // Fallback
            }
        });
    }
}