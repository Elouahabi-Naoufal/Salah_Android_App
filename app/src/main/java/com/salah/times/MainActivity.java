package com.salah.times;

import android.content.Context;
import android.content.Intent;
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
    private TextView iqamaCountdown;
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
        
        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        setContentView(R.layout.activity_main);
        

        
        // Handle single instance
        SingleInstanceManager.handleSingleInstance(this);
        
        initViews();
        setupPrayerGrid();
        startClockUpdate();
        setAlarmsFromDatabase();
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
        iqamaCountdown = findViewById(R.id.iqama_countdown);
        prayerGrid = findViewById(R.id.prayer_grid);
        
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
            refreshApp();
        });
        
        // Long press refresh button for testing mode
        findViewById(R.id.refresh_button).setOnLongClickListener(v -> {
            showTestingDialog();
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
                updateAlarmsIfNeeded();
                handler.postDelayed(this, 60000); // Update every minute
            }
        };
        handler.post(updateTimeRunnable);
    }
    
    private void updateAlarmsIfNeeded() {
        if (currentPrayerTimes != null) {
            AlarmAppIntegration.addPrayerAlarms(this, currentPrayerTimes);
        }
    }
    
    private void updateClock() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat secondsFormat = new SimpleDateFormat("ss", Locale.getDefault());
        
        Date now = new Date();
        clockText.setText(timeFormat.format(now));
        
        // Update countdown every second
        handler.postDelayed(() -> updateLiveCountdown(), 1000 - Integer.parseInt(secondsFormat.format(now)));
        
        // Format date in current language
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
        // Apply test times if in testing mode
        this.currentPrayerTimes = TestingManager.getTestPrayerTimes(this, prayerTimes);
        
        // Add alarms to system alarm app
        AlarmAppIntegration.addPrayerAlarms(this, prayerTimes);
        
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
        // Update all text when returning from settings (language may have changed)
        TextView appTitle = findViewById(R.id.app_title);
        appTitle.setText(TranslationManager.tr("app_name"));
        
        updateLocationDisplay();
        
        TextView nextPrayerLabel = findViewById(R.id.next_prayer_label);
        nextPrayerLabel.setText(TranslationManager.tr("next_prayer"));
        
        Button refreshButton = findViewById(R.id.refresh_button);
        refreshButton.setText(TranslationManager.tr("refresh"));
        
        // Reload prayer times for new city
        loadPrayerTimes();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && updateTimeRunnable != null) {
            handler.removeCallbacks(updateTimeRunnable);
        }
    }
    

    
    private void refreshApp() {
        // Reset UI to loading state like app startup
        setupPrayerGrid();
        countdownText.setText("--:--:--");
        if (hijriText != null) {
            hijriText.setText(TranslationManager.tr("loading"));
        }
        
        // Clear current data
        currentPrayerTimes = null;
        tomorrowsFajr = null;
        
        // Reload everything
        loadPrayerTimes();
        
        Toast.makeText(this, TranslationManager.tr("messages.refreshing"), Toast.LENGTH_SHORT).show();
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
                iqamaCountdown.setText("");
            }
        }
    }
    
    private void updateIqamaCountdown(String nextPrayer, int remainingMinutes, int seconds) {
        int iqamaDelayMinutes = SettingsManager.getIqamaDelay(nextPrayer);
        
        // Show iqama countdown only when prayer time has passed and within iqama delay
        if (remainingMinutes <= 0 && Math.abs(remainingMinutes) < iqamaDelayMinutes) {
            int iqamaRemainingMinutes = iqamaDelayMinutes + remainingMinutes;
            int iqamaRemainingSeconds = 60 - seconds;
            
            if (iqamaRemainingMinutes > 0 || (iqamaRemainingMinutes == 0 && iqamaRemainingSeconds > 0)) {
                if (iqamaRemainingSeconds == 60) {
                    iqamaRemainingSeconds = 0;
                } else if (iqamaRemainingMinutes > 0) {
                    iqamaRemainingMinutes--;
                }
                
                String iqamaText = TranslationManager.tr("settings_items.iqama_in") + " " + String.format("%02d:%02d", iqamaRemainingMinutes, iqamaRemainingSeconds);
                iqamaCountdown.setText(iqamaText);
                iqamaCountdown.setTextColor(getColor(android.R.color.holo_red_light));
                return;
            }
        }
        
        iqamaCountdown.setText("");
    }
    

    
    private void showTestingDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Testing Mode - Adhan Test");
        
        String[] options = {"Test in 5 seconds", "Test in 30 seconds", "Test in 1 minute", "Test in 5 minutes", "Check permissions", "Disable testing mode"};
        
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    TestingManager.setNextPrayerInSeconds(this, 5);
                    Toast.makeText(this, "Adhan will play in 5 seconds", Toast.LENGTH_SHORT).show();
                    loadPrayerTimes();
                    break;
                case 1:
                    TestingManager.setNextPrayerInSeconds(this, 30);
                    Toast.makeText(this, "Adhan will play in 30 seconds", Toast.LENGTH_SHORT).show();
                    loadPrayerTimes();
                    break;
                case 2:
                    TestingManager.setNextPrayerInMinutes(this, 1);
                    Toast.makeText(this, "Adhan will play in 1 minute", Toast.LENGTH_SHORT).show();
                    loadPrayerTimes();
                    break;
                case 3:
                    TestingManager.setNextPrayerInMinutes(this, 5);
                    Toast.makeText(this, "Adhan will play in 5 minutes", Toast.LENGTH_SHORT).show();
                    loadPrayerTimes();
                    break;
                case 4:
                    checkAlarmPermissions();
                    break;
                case 5:
                    TestingManager.setTestingMode(this, false);
                    Toast.makeText(this, "Testing mode disabled", Toast.LENGTH_SHORT).show();
                    loadPrayerTimes();
                    break;
            }
        });
        
        builder.show();
    }
    
    private void setAlarmsFromDatabase() {
        try {
            String cityName = SettingsManager.getDefaultCity();
            String today = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());
            
            PrayerTimes times = DatabaseHelper.getInstance(this).loadPrayerTimes(cityName, today);
            if (times != null) {
                AlarmAppIntegration.addPrayerAlarms(this, times);
                android.util.Log.d("MainActivity", "Set alarms from database for today");
            }
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "Failed to set alarms from database", e);
        }
    }
    
    private void checkAlarmPermissions() {
        StringBuilder status = new StringBuilder("Alarm Permissions Status:\n\n");
        
        // Check exact alarm permission (Android 12+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && alarmManager.canScheduleExactAlarms()) {
                status.append("✓ Exact alarms: GRANTED\n");
            } else {
                status.append("✗ Exact alarms: DENIED\n");
            }
        } else {
            status.append("✓ Exact alarms: Not required (Android < 12)\n");
        }
        
        // Check notification permission
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                status.append("✓ Notifications: GRANTED\n");
            } else {
                status.append("✗ Notifications: DENIED\n");
            }
        } else {
            status.append("✓ Notifications: Not required (Android < 13)\n");
        }
        

        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Permission Status")
               .setMessage(status.toString())
               .setPositiveButton("OK", null)
               .setNeutralButton("Open Settings", (d, w) -> {
                   Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                   intent.setData(android.net.Uri.parse("package:" + getPackageName()));
                   startActivity(intent);
               })
               .show();
    }
}