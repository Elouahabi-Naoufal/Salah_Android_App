package com.salah.times;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
    private TextView iqamaCountdownLabel;
    private TextView iqamaCountdown;
    private RecyclerView prayerGrid;
    private Handler handler = new Handler();
    private Runnable updateTimeRunnable;
    private Runnable countdownRunnable;
    private PrayerTimes currentPrayerTimes;
    private String tomorrowsFajr = null;
    private DbRefreshManager dbRefreshManager;
    private Button refreshDbButton;

    private final BroadcastReceiver scrapeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int success = intent.getIntExtra(ScrapeAllCitiesService.EXTRA_SUCCESS, 0);
            int failed  = intent.getIntExtra(ScrapeAllCitiesService.EXTRA_FAILED, 0);
            refreshDbButton.setEnabled(true);
            refreshDbButton.setText(TranslationManager.tr("refresh_db"));
            dbRefreshManager.resetCounter();
            loadPrayerTimes();
            Toast.makeText(MainActivity.this,
                    success + " cities updated" + (failed > 0 ? ", " + failed + " failed" : ""),
                    Toast.LENGTH_LONG).show();
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Apply theme
        ThemeManager.applyTheme();
        
        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        setContentView(R.layout.activity_main);
        

        
        // Handle single instance
        SingleInstanceManager.handleSingleInstance(this);
        
        dbRefreshManager = new DbRefreshManager(this);
        initViews();
        setupPrayerGrid();
        startClockUpdate();
        loadPrayerTimes();
        
        // Check if first time setup needed
        if (isFirstTimeUser()) {
            startLanguageSelection();
        }
        
        // Start persistent notification service
        startPrayerNotificationService();
    }
    
    private void initViews() {
        clockText = findViewById(R.id.clock_text);
        dateText = findViewById(R.id.date_text);
        hijriText = findViewById(R.id.hijri_text);
        countdownText = findViewById(R.id.countdown_text);
        iqamaCountdownLabel = findViewById(R.id.iqama_countdown_label);
        iqamaCountdown = findViewById(R.id.iqama_countdown);
        prayerGrid = findViewById(R.id.prayer_grid);
        
        iqamaCountdownLabel.setText(TranslationManager.tr("notifications.iqama_countdown"));
        iqamaCountdownLabel.setVisibility(android.view.View.GONE);
        iqamaCountdown.setVisibility(android.view.View.GONE);
        
        // Set current city name and app title
        TextView appTitle = findViewById(R.id.app_title);
        appTitle.setText(TranslationManager.tr("app_name"));
        
        updateLocationDisplay();
        
        TextView nextPrayerLabel = findViewById(R.id.next_prayer_label);
        nextPrayerLabel.setText(TranslationManager.tr("next_prayer"));
        
        Button refreshButton = findViewById(R.id.refresh_button);
        refreshButton.setText(TranslationManager.tr("refresh"));
        
        // Setup settings button
        findViewById(R.id.settings_button).setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
        
        // Setup refresh button
        findViewById(R.id.refresh_button).setOnClickListener(v -> {
            if (!dbRefreshManager.isOnline()) {
                dbRefreshManager.showNoInternetDialog(this);
            } else {
                refreshApp();
            }
        });

        // Setup Refresh DB button
        refreshDbButton = findViewById(R.id.refresh_db_button);
        refreshDbButton.setText(TranslationManager.tr("refresh_db"));
        refreshDbButton.setOnClickListener(v -> {
            if (!dbRefreshManager.isOnline()) {
                dbRefreshManager.showNoInternetDialog(this);
                return;
            }
            refreshDbButton.setEnabled(false);
            refreshDbButton.setText(TranslationManager.tr("refresh_db_updating"));
            startForegroundService(new Intent(this, ScrapeAllCitiesService.class));
        });
        
        // Setup refresh button LONG PRESS for testing mode
        findViewById(R.id.refresh_button).setOnLongClickListener(v -> {
            showTestMenu();
            return true;
        });
        
        // Setup adhkar button
        findViewById(R.id.adhkar_button).setOnClickListener(v -> {
            Intent intent = new Intent(this, AdhkarActivity.class);
            startActivity(intent);
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
        prayers.add(new PrayerAdapter.PrayerItem("Fajr", TranslationManager.tr("loading"), false));
        prayers.add(new PrayerAdapter.PrayerItem("Dohr", TranslationManager.tr("loading"), false));
        prayers.add(new PrayerAdapter.PrayerItem("Asr", TranslationManager.tr("loading"), false));
        prayers.add(new PrayerAdapter.PrayerItem("Maghreb", TranslationManager.tr("loading"), false));
        prayers.add(new PrayerAdapter.PrayerItem("Isha", TranslationManager.tr("loading"), false));
        
        PrayerAdapter adapter = new PrayerAdapter(prayers);
        prayerGrid.setAdapter(adapter);
    }
    
    private void startClockUpdate() {
        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                updateClock();
                handler.postDelayed(this, 60000);
            }
        };
        handler.post(updateTimeRunnable);
        
        countdownRunnable = new Runnable() {
            @Override
            public void run() {
                updateLiveCountdown();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(countdownRunnable);
    }
    
    private void updateClock() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date now = new Date();
        clockText.setText(timeFormat.format(now));
        
        String formattedDate = formatDateInCurrentLanguage(now);
        dateText.setText(formattedDate);
    }
    
    private String formatDateInCurrentLanguage(Date date) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(date);
        
        String dayName = TranslationManager.tr("days." + getDayKey(cal.get(java.util.Calendar.DAY_OF_WEEK)));
        String monthName = TranslationManager.tr("months." + getMonthKey(cal.get(java.util.Calendar.MONTH)));
        int dayOfMonth = cal.get(java.util.Calendar.DAY_OF_MONTH);
        int year = cal.get(java.util.Calendar.YEAR);
        
        return dayName + ", " + monthName + " " + dayOfMonth + ", " + year;
    }
    
    private String getDayKey(int dayOfWeek) {
        switch (dayOfWeek) {
            case java.util.Calendar.SUNDAY: return "sunday";
            case java.util.Calendar.MONDAY: return "monday";
            case java.util.Calendar.TUESDAY: return "tuesday";
            case java.util.Calendar.WEDNESDAY: return "wednesday";
            case java.util.Calendar.THURSDAY: return "thursday";
            case java.util.Calendar.FRIDAY: return "friday";
            case java.util.Calendar.SATURDAY: return "saturday";
            default: return "sunday";
        }
    }
    
    private String getMonthKey(int month) {
        switch (month) {
            case java.util.Calendar.JANUARY: return "january";
            case java.util.Calendar.FEBRUARY: return "february";
            case java.util.Calendar.MARCH: return "march";
            case java.util.Calendar.APRIL: return "april";
            case java.util.Calendar.MAY: return "may";
            case java.util.Calendar.JUNE: return "june";
            case java.util.Calendar.JULY: return "july";
            case java.util.Calendar.AUGUST: return "august";
            case java.util.Calendar.SEPTEMBER: return "september";
            case java.util.Calendar.OCTOBER: return "october";
            case java.util.Calendar.NOVEMBER: return "november";
            case java.util.Calendar.DECEMBER: return "december";
            default: return "january";
        }
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
        
        // Schedule prayer alarms
        PrayerAlarmScheduler.schedulePrayerAlarms(this, prayerTimes);
        
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
        String hijriDate = HijriDateManager.getHijriDate();
        if (hijriText != null) {
            hijriText.setText(hijriDate);
        }
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
    protected void onResume() {
        super.onResume();
        registerReceiver(scrapeReceiver,
                new IntentFilter(ScrapeAllCitiesService.ACTION_DONE),
                Context.RECEIVER_NOT_EXPORTED);
        // Update all text when returning from settings (language may have changed)
        TextView appTitle = findViewById(R.id.app_title);
        appTitle.setText(TranslationManager.tr("app_name"));
        
        updateLocationDisplay();
        
        TextView nextPrayerLabel = findViewById(R.id.next_prayer_label);
        nextPrayerLabel.setText(TranslationManager.tr("next_prayer"));
        
        Button refreshButton = findViewById(R.id.refresh_button);
        refreshButton.setText(TranslationManager.tr("refresh"));
        
        iqamaCountdownLabel.setText(TranslationManager.tr("notifications.iqama_countdown"));
        
        // Reload prayer times for new city
        loadPrayerTimes();

        // 30-day counter check
        dbRefreshManager.checkOnOpen();
        if (dbRefreshManager.shouldPromptRefresh()) {
            dbRefreshManager.showRefreshPrompt(this, () -> {
                Toast.makeText(this, TranslationManager.tr("refresh_db_updating"), Toast.LENGTH_SHORT).show();
                PrayerTimesService.forceUpdateAllCities().thenRun(() -> {
                    dbRefreshManager.resetCounter();
                    runOnUiThread(() -> loadPrayerTimes());
                });
            });
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        try { unregisterReceiver(scrapeReceiver); } catch (Exception ignored) {}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            if (updateTimeRunnable != null) handler.removeCallbacks(updateTimeRunnable);
            if (countdownRunnable != null) handler.removeCallbacks(countdownRunnable);
        }
    }
    

    
    private void refreshApp() {
        // Reset UI to loading state like app startup
        setupPrayerGrid();
        countdownText.setText("--:--:--");
        if (hijriText != null) {
            hijriText.setText(TranslationManager.tr("loading"));
        }

        currentPrayerTimes = null;
        tomorrowsFajr = null;

        // Reset 30-day counter on manual refresh
        dbRefreshManager.resetCounter();

        loadPrayerTimes();

        Toast.makeText(this, TranslationManager.tr("messages.refreshing"), Toast.LENGTH_SHORT).show();
    }
    
    private void showTestMenu() {
        String[] options = {
            "🧪 Test Mode: 15-sec intervals",
            "⏰ Test Iqama Countdown",
            "🔔 Test Fajr Notification NOW",
            "🔔 Test Dhuhr Notification NOW",
            "🔄 Exit Test Mode (Real Times)"
        };
        
        new android.app.AlertDialog.Builder(this)
            .setTitle("🧪 TESTING MODE")
            .setItems(options, (dialog, which) -> {
                switch(which) {
                    case 0:
                        activateTestMode();
                        break;
                    case 1:
                        activateIqamaTestMode();
                        break;
                    case 2:
                        TestingManager.triggerTestNotification(this, "fajr");
                        Toast.makeText(this, "Fajr notification triggered!", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        TestingManager.triggerTestNotification(this, "dhuhr");
                        Toast.makeText(this, "Dhuhr notification triggered!", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        exitTestMode();
                        break;
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void activateTestMode() {
        PrayerTimes testTimes = TestingManager.getTestPrayerTimes();
        TestingManager.activateTestMode(this, testTimes);
        updatePrayerTimesUI(testTimes);
        
        Toast.makeText(this, "🧪 TEST MODE ACTIVE\nFajr in 15s, Dhuhr in 30s, Asr in 45s\nNotifications will trigger!", Toast.LENGTH_LONG).show();
    }
    
    private void activateIqamaTestMode() {
        PrayerTimes testTimes = TestingManager.getIqamaTestTimes();
        TestingManager.activateTestMode(this, testTimes);
        updatePrayerTimesUI(testTimes);
        
        Toast.makeText(this, "⏰ IQAMA TEST MODE\nDhuhr was 2 min ago\nIqama countdown should appear!", Toast.LENGTH_LONG).show();
    }
    
    private void exitTestMode() {
        // Clear test times from database
        String cityName = SettingsManager.getDefaultCity();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        
        // Delete test prayer times from database
        db.getWritableDatabase().delete("prayer_times", "city_name = ? AND date = ?", new String[]{cityName, today});
        
        // Cancel any test alarms
        PrayerAlarmScheduler.cancelAllAlarms(this);
        
        // Force reload from internet (not cache)
        loadPrayerTimes();
        
        Toast.makeText(this, "✅ Exited test mode - Loading real prayer times...", Toast.LENGTH_SHORT).show();
    }
    
    private void showFeatureUnavailable(String featureName) {
        Toast.makeText(this, featureName + " " + TranslationManager.tr("messages.feature_coming_soon"), Toast.LENGTH_SHORT).show();
    }
    
    private boolean isFirstTimeUser() {
        return SalahApplication.getInstance().isFirstRun();
    }
    
    private void startLanguageSelection() {
        Intent intent = new Intent(this, LanguageSelectionActivity.class);
        startActivity(intent);
    }
    
    private void startCitySelection() {
        Intent intent = new Intent(this, CitySelectionActivity.class);
        startActivity(intent);
    }
    
    private void startPrayerNotificationService() {
        Intent serviceIntent = new Intent(this, PrayerNotificationService.class);
        startForegroundService(serviceIntent);
    }
    
    private void updateLocationDisplay() {
        TextView locationText = findViewById(R.id.location_text);
        String cityNameEn = SettingsManager.getDefaultCity();
        City currentCity = CitiesData.getCityByName(cityNameEn);
        String cityName = currentCity.getName(TranslationManager.getCurrentLanguage());
        locationText.setText(cityName + ", " + TranslationManager.tr("country_morocco"));
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
                
                // Update iqama countdown
                updateIqamaCountdown(nextPrayer, remainingMinutes, seconds);
                
            } catch (java.text.ParseException e) {
                countdownText.setText("--:--:--");
                iqamaCountdownLabel.setVisibility(android.view.View.GONE);
                iqamaCountdown.setVisibility(android.view.View.GONE);
            }
        }
    }
    
    private void updateIqamaCountdown(String nextPrayer, int remainingMinutes, int seconds) {
        int iqamaDelayMinutes = SettingsManager.getIqamaDelay(nextPrayer);
        
        android.util.Log.d("IqamaCountdown", "Prayer: " + nextPrayer + ", RemainingMin: " + remainingMinutes + ", IqamaDelay: " + iqamaDelayMinutes);
        
        // Show countdown when prayer time has passed (remainingMinutes is negative)
        // and we're still within the iqama delay period
        if (remainingMinutes <= 0 && Math.abs(remainingMinutes) < iqamaDelayMinutes) {
            int iqamaRemainingMinutes = iqamaDelayMinutes + remainingMinutes;
            int iqamaRemainingSeconds = 60 - seconds;
            
            if (iqamaRemainingMinutes > 0 || (iqamaRemainingMinutes == 0 && iqamaRemainingSeconds > 0)) {
                if (iqamaRemainingSeconds == 60) {
                    iqamaRemainingSeconds = 0;
                } else if (iqamaRemainingMinutes > 0) {
                    iqamaRemainingMinutes--;
                }
                
                String countdown = String.format("%02d:%02d", iqamaRemainingMinutes, iqamaRemainingSeconds);
                android.util.Log.d("IqamaCountdown", "Showing: " + countdown);
                iqamaCountdown.setText(countdown);
                iqamaCountdownLabel.setVisibility(android.view.View.VISIBLE);
                iqamaCountdown.setVisibility(android.view.View.VISIBLE);
                return;
            }
        }
        
        android.util.Log.d("IqamaCountdown", "Hiding - not in iqama period");
        iqamaCountdownLabel.setVisibility(android.view.View.GONE);
        iqamaCountdown.setVisibility(android.view.View.GONE);
    }
    

    

}