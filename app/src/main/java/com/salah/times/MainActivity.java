package com.salah.times;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    
    private TextView clockText;
    private TextView dateText;
    private TextView hijriText;

    private TextView countdownText;
    private RecyclerView prayerGrid;
    private Handler handler = new Handler();
    private Runnable updateTimeRunnable;
    private PrayerTimes currentPrayerTimes;
    private String tomorrowsFajr = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Apply theme
        ThemeManager.applyTheme();
        
        setContentView(R.layout.activity_main);
        

        
        // Handle single instance
        SingleInstanceManager.handleSingleInstance(this);
        
        initViews();
        setupPrayerGrid();
        startClockUpdate();
        loadPrayerTimes();
        
        // Check if first time setup needed
        if (SettingsManager.getDefaultCity().equals("Casablanca") && !hasUserSelectedCity()) {
            startCitySelection();
        }
        
        // Start persistent notification service
        startPrayerNotificationService();
    }
    
    private void initViews() {
        clockText = findViewById(R.id.clock_text);
        dateText = findViewById(R.id.date_text);
        hijriText = findViewById(R.id.hijri_text);
        countdownText = findViewById(R.id.countdown_text);
        prayerGrid = findViewById(R.id.prayer_grid);
        
        // Setup settings button
        findViewById(R.id.settings_button).setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
        
        // Setup refresh button
        findViewById(R.id.refresh_button).setOnClickListener(v -> {
            refreshApp();
        });
    }
    
    private void setupPrayerGrid() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        // Make the first item (Fajr) span 2 columns to center it
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? 2 : 1; // First item spans 2 columns
            }
        });
        prayerGrid.setLayoutManager(gridLayoutManager);
        
        // Initialize with loading state - 5 essential prayers only
        List<PrayerAdapter.PrayerItem> prayers = new ArrayList<>();
        prayers.add(new PrayerAdapter.PrayerItem("Fajr", "Loading...", false));
        prayers.add(new PrayerAdapter.PrayerItem("Dohr", "Loading...", false));
        prayers.add(new PrayerAdapter.PrayerItem("Asr", "Loading...", false));
        prayers.add(new PrayerAdapter.PrayerItem("Maghreb", "Loading...", false));
        prayers.add(new PrayerAdapter.PrayerItem("Isha", "Loading...", false));
        
        PrayerAdapter adapter = new PrayerAdapter(prayers);
        prayerGrid.setAdapter(adapter);
    }
    
    private void startClockUpdate() {
        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                updateClock();
                handler.postDelayed(this, 1000); // Update every second
            }
        };
        handler.post(updateTimeRunnable);
    }
    
    private void updateClock() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        
        Date now = new Date();
        clockText.setText(timeFormat.format(now));
        dateText.setText(dateFormat.format(now));
        
        // Update countdown in real-time
        updateLiveCountdown();
    }
    
    private void loadPrayerTimes() {
        City defaultCity = CitiesData.getCityByName(SettingsManager.getDefaultCity());
        if (defaultCity != null) {
            PrayerTimeWorker worker = new PrayerTimeWorker(this);
            worker.loadPrayerTimes(defaultCity, new PrayerTimeWorker.PrayerTimeCallback() {
                @Override
                public void onSuccess(PrayerTimes prayerTimes) {
                    runOnUiThread(() -> updatePrayerTimesUI(prayerTimes));
                }
                
                @Override
                public void onError(String error) {
                    runOnUiThread(() -> showError(error));
                }
                
                @Override
                public void onCachedData(PrayerTimes prayerTimes, int daysRemaining) {
                    runOnUiThread(() -> updatePrayerTimesUI(prayerTimes));
                }
            });
        }
    }
    
    private void updatePrayerTimesUI(PrayerTimes prayerTimes) {
        this.currentPrayerTimes = prayerTimes;
        
        // Fetch tomorrow's Fajr for countdown calculation
        City defaultCity = CitiesData.getCityByName(SettingsManager.getDefaultCity());
        if (defaultCity != null) {
            PrayerTimesService.fetchTomorrowsFajr(defaultCity)
                .thenAccept(fajrTime -> {
                    tomorrowsFajr = fajrTime;
                    android.util.Log.d("MainActivity", "Tomorrow's Fajr: " + tomorrowsFajr);
                })
                .exceptionally(throwable -> {
                    tomorrowsFajr = "05:30"; // Fallback
                    return null;
                });
        }
        
        // Update prayer grid with highlighting
        updatePrayerGrid(prayerTimes);
        
        // Update Hijri date
        updateHijriDate();
        
        // Initial countdown update
        updateLiveCountdown();
    }
    
    private void updatePrayerGrid(PrayerTimes prayerTimes) {
        List<PrayerAdapter.PrayerItem> prayers = new ArrayList<>();
        String currentPrayer = PrayerHighlightManager.getCurrentPrayer(prayerTimes);
        
        prayers.add(new PrayerAdapter.PrayerItem("Fajr", prayerTimes.getFajr(), "Fajr".equals(currentPrayer)));
        prayers.add(new PrayerAdapter.PrayerItem("Dohr", prayerTimes.getDhuhr(), "Dohr".equals(currentPrayer)));
        prayers.add(new PrayerAdapter.PrayerItem("Asr", prayerTimes.getAsr(), "Asr".equals(currentPrayer)));
        prayers.add(new PrayerAdapter.PrayerItem("Maghreb", prayerTimes.getMaghrib(), "Maghreb".equals(currentPrayer)));
        prayers.add(new PrayerAdapter.PrayerItem("Isha", prayerTimes.getIsha(), "Isha".equals(currentPrayer)));
        
        PrayerAdapter adapter = (PrayerAdapter) prayerGrid.getAdapter();
        if (adapter != null) {
            adapter.updatePrayers(prayers);
        }
    }
    
    private void updateHijriDate() {
        HijriDateManager.fetchHijriDate()
            .thenAccept(hijriDate -> runOnUiThread(() -> {
                if (hijriText != null) {
                    hijriText.setText(hijriDate);
                }
            }))
            .exceptionally(throwable -> {
                runOnUiThread(() -> {
                    if (hijriText != null) {
                        hijriText.setText(TranslationManager.tr("hijri_unavailable"));
                    }
                });
                return null;
            });
    }
    
    private String getTimeForPrayer(String prayer, PrayerTimes prayerTimes) {
        switch (prayer) {
            case "Fajr": return prayerTimes.getFajr();
            case "Dohr": return prayerTimes.getDhuhr();
            case "Asr": return prayerTimes.getAsr();
            case "Maghreb": return prayerTimes.getMaghrib();
            case "Isha": return prayerTimes.getIsha();
            default: return "00:00";
        }
    }
    
    private void showError(String error) {
        countdownText.setText("--:--:--");
        if (hijriText != null) {
            hijriText.setText(TranslationManager.tr("hijri_unavailable"));
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && updateTimeRunnable != null) {
            handler.removeCallbacks(updateTimeRunnable);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_monthly_calendar) {
            showFeatureUnavailable("Monthly Calendar");
            return true;
        } else if (id == R.id.action_weekly_schedule) {
            showFeatureUnavailable("Weekly Schedule");
            return true;
        } else if (id == R.id.action_timezone_view) {
            showFeatureUnavailable("Multiple Timezones");
            return true;
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void refreshApp() {
        // Reset UI to loading state like app startup
        setupPrayerGrid();
        countdownText.setText("--:--:--");
        if (hijriText != null) {
            hijriText.setText("Loading...");
        }
        
        // Clear current data
        currentPrayerTimes = null;
        tomorrowsFajr = null;
        
        // Reload everything
        loadPrayerTimes();
        
        Toast.makeText(this, "Refreshing prayer times...", Toast.LENGTH_SHORT).show();
    }
    
    private void showFeatureUnavailable(String featureName) {
        Toast.makeText(this, featureName + " feature coming soon!", Toast.LENGTH_SHORT).show();
    }
    
    private boolean hasUserSelectedCity() {
        return !SettingsManager.getDefaultCity().equals("Casablanca");
    }
    
    private void startCitySelection() {
        Intent intent = new Intent(this, CitySelectionActivity.class);
        startActivity(intent);
    }
    
    private void startPrayerNotificationService() {
        Intent serviceIntent = new Intent(this, PrayerNotificationService.class);
        startForegroundService(serviceIntent);
    }
    

    
    private boolean isAfterIsha(PrayerTimes prayerTimes) {
        java.util.Calendar now = java.util.Calendar.getInstance();
        int currentMinutes = now.get(java.util.Calendar.HOUR_OF_DAY) * 60 + now.get(java.util.Calendar.MINUTE);
        
        try {
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
            java.util.Date ishaTime = format.parse(prayerTimes.getIsha());
            java.util.Calendar ishaCal = java.util.Calendar.getInstance();
            ishaCal.setTime(ishaTime);
            int ishaMinutes = ishaCal.get(java.util.Calendar.HOUR_OF_DAY) * 60 + ishaCal.get(java.util.Calendar.MINUTE);
            
            return currentMinutes >= ishaMinutes;
        } catch (java.text.ParseException e) {
            return false;
        }
    }
    
    private String formatCountdown(long milliseconds) {
        long totalSeconds = milliseconds / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    
    private void updateLiveCountdown() {
        if (currentPrayerTimes != null) {
            java.util.Calendar now = java.util.Calendar.getInstance();
            int currentMinutes = now.get(java.util.Calendar.HOUR_OF_DAY) * 60 + now.get(java.util.Calendar.MINUTE);
            int currentSeconds = now.get(java.util.Calendar.SECOND);
            
            String nextPrayer = PrayerHighlightManager.getNextPrayer(currentPrayerTimes);
            String nextPrayerTime = getTimeForPrayer(nextPrayer, currentPrayerTimes);
            
            try {
                java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
                java.util.Date prayerDate = format.parse(nextPrayerTime);
                java.util.Calendar prayerCal = java.util.Calendar.getInstance();
                prayerCal.setTime(prayerDate);
                int prayerMinutes = prayerCal.get(java.util.Calendar.HOUR_OF_DAY) * 60 + prayerCal.get(java.util.Calendar.MINUTE);
                
                int remainingMinutes;
                
                // Check if we're after Isha and next prayer is Fajr (tomorrow)
                if ("Fajr".equals(nextPrayer) && isAfterIsha(currentPrayerTimes) && tomorrowsFajr != null) {
                    // Use tomorrow's Fajr time
                    java.util.Date tomorrowFajrDate = format.parse(tomorrowsFajr);
                    java.util.Calendar tomorrowFajrCal = java.util.Calendar.getInstance();
                    tomorrowFajrCal.setTime(tomorrowFajrDate);
                    int tomorrowFajrMinutes = tomorrowFajrCal.get(java.util.Calendar.HOUR_OF_DAY) * 60 + tomorrowFajrCal.get(java.util.Calendar.MINUTE);
                    remainingMinutes = (24 * 60) - currentMinutes + tomorrowFajrMinutes;
                } else if (prayerMinutes <= currentMinutes) {
                    // Tomorrow's prayer (general case)
                    remainingMinutes = (24 * 60) - currentMinutes + prayerMinutes;
                } else {
                    // Today's prayer
                    remainingMinutes = prayerMinutes - currentMinutes;
                }
                
                int hours = remainingMinutes / 60;
                int minutes = remainingMinutes % 60;
                int seconds = 60 - currentSeconds;
                
                if (seconds == 60) {
                    seconds = 0;
                } else if (minutes > 0) {
                    minutes--;
                }
                
                String countdown = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                countdownText.setText(countdown);
                
            } catch (java.text.ParseException e) {
                countdownText.setText("--:--:--");
            }
        }
    }
}