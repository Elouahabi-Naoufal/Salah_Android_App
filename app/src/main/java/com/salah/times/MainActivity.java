package com.salah.times;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Handle single instance
        SingleInstanceManager.handleSingleInstance(this);
        
        initViews();
        setupPrayerGrid();
        startClockUpdate();
        loadPrayerTimes();
        
        // Check if first time setup needed
        SharedPrefsManager prefsManager = new SharedPrefsManager(this);
        if (prefsManager.getDefaultCity().equals("Casablanca") && !hasUserSelectedCity()) {
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
    }
    
    private void setupPrayerGrid() {
        prayerGrid.setLayoutManager(new GridLayoutManager(this, 1));
        
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
    }
    
    private void loadPrayerTimes() {
        SharedPrefsManager prefsManager = new SharedPrefsManager(this);
        City defaultCity = CitiesData.getCityByName(prefsManager.getDefaultCity());
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
        // Update next prayer display
        String nextPrayer = PrayerHighlightManager.getNextPrayer(prayerTimes);
        String nextPrayerDisplay = TranslationManager.tr(nextPrayer.toLowerCase());
        
        // Check if next prayer is tomorrow's Fajr (after Isha)
        if ("Fajr".equals(nextPrayer) && isAfterIsha(prayerTimes)) {
            nextPrayerDisplay += " (" + TranslationManager.tr("tomorrow") + ")";
        }
        
        // Show only countdown to next prayer
        long timeUntilNext = PrayerHighlightManager.getTimeUntilNextPrayer(prayerTimes);
        String countdown = formatCountdown(timeUntilNext);
        android.util.Log.d("MainActivity", "Countdown: " + countdown + ", timeUntilNext: " + timeUntilNext);
        countdownText.setText(countdown);
        

        
        // Update prayer grid with highlighting
        updatePrayerGrid(prayerTimes);
        
        // Update Hijri date
        updateHijriDate();
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
    
    private void showFeatureUnavailable(String featureName) {
        Toast.makeText(this, featureName + " feature coming soon!", Toast.LENGTH_SHORT).show();
    }
    
    private boolean hasUserSelectedCity() {
        SharedPrefsManager prefsManager = new SharedPrefsManager(this);
        return !prefsManager.getDefaultCity().equals("Casablanca") || 
               getSharedPreferences("app_prefs", MODE_PRIVATE).getBoolean("city_selected", false);
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
}