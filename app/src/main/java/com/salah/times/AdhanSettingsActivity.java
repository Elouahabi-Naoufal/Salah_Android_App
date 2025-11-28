package com.salah.times;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class AdhanSettingsActivity extends AppCompatActivity {
    
    private Switch masterSwitch;
    private LinearLayout prayersContainer;
    private TextView ringtoneName;
    private SeekBar volumeSeekBar;
    
    private String[] prayers = {"Fajr", "Dhuhr", "Asr", "Maghrib", "Isha"};
    private String[] prayerIcons = {"☽", "☉", "☀", "☾", "★"};
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ThemeManager.applyTheme();
        setContentView(R.layout.activity_adhan_settings);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Prayer Alarms");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        initViews();
        setupMasterSwitch();
        setupPrayerToggles();
        setupRingtoneSelector();
        setupVolumeControl();
    }
    
    private void initViews() {
        masterSwitch = findViewById(R.id.master_switch);
        prayersContainer = findViewById(R.id.prayers_container);
        ringtoneName = findViewById(R.id.ringtone_name);
        volumeSeekBar = findViewById(R.id.volume_seekbar);
    }
    
    private void setupMasterSwitch() {
        masterSwitch.setChecked(SettingsManager.getAdanEnabled());
        masterSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SettingsManager.setAdanEnabled(isChecked);
            updatePrayerTogglesState(isChecked);
        });
    }
    
    private void setupPrayerToggles() {
        prayersContainer.removeAllViews();
        
        for (int i = 0; i < prayers.length; i++) {
            String prayer = prayers[i];
            String icon = prayerIcons[i];
            
            LinearLayout prayerItem = createPrayerToggleItem(prayer, icon);
            prayersContainer.addView(prayerItem);
            
            if (i < prayers.length - 1) {
                addDivider();
            }
        }
        
        updatePrayerTogglesState(masterSwitch.isChecked());
    }
    
    private LinearLayout createPrayerToggleItem(String prayer, String icon) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setGravity(android.view.Gravity.CENTER_VERTICAL);
        item.setPadding(24, 20, 24, 20);
        android.util.TypedValue outValue = new android.util.TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        item.setBackgroundResource(outValue.resourceId);
        
        // Icon
        TextView iconView = new TextView(this);
        iconView.setText(icon);
        iconView.setTextSize(24);
        iconView.setTextColor(getColor(R.color.primary_green));
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(60, LinearLayout.LayoutParams.WRAP_CONTENT);
        iconView.setLayoutParams(iconParams);
        item.addView(iconView);
        
        // Prayer name and time
        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        textParams.setMarginStart(16);
        textContainer.setLayoutParams(textParams);
        
        TextView prayerName = new TextView(this);
        prayerName.setText(prayer);
        prayerName.setTextSize(18);
        prayerName.setTextColor(getColor(R.color.text_primary));
        textContainer.addView(prayerName);
        
        TextView prayerTime = new TextView(this);
        prayerTime.setText(getPrayerTimeDisplay(prayer));
        prayerTime.setTextSize(14);
        prayerTime.setTextColor(getColor(R.color.text_secondary));
        textContainer.addView(prayerTime);
        
        item.addView(textContainer);
        
        // Toggle switch
        Switch prayerSwitch = new Switch(this);
        prayerSwitch.setChecked(SettingsManager.getPrayerAlarmEnabled(prayer));
        prayerSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SettingsManager.setPrayerAlarmEnabled(prayer, isChecked);
        });
        item.addView(prayerSwitch);
        
        return item;
    }
    
    private void addDivider() {
        View divider = new View(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 1);
        params.setMargins(80, 8, 24, 8);
        divider.setLayoutParams(params);
        divider.setBackgroundColor(getColor(R.color.divider));
        divider.setAlpha(0.3f);
        prayersContainer.addView(divider);
    }
    
    private void updatePrayerTogglesState(boolean enabled) {
        for (int i = 0; i < prayersContainer.getChildCount(); i++) {
            if (prayersContainer.getChildAt(i) instanceof LinearLayout) {
                LinearLayout item = (LinearLayout) prayersContainer.getChildAt(i);
                item.setAlpha(enabled ? 1.0f : 0.5f);
                
                // Find and disable/enable the switch
                for (int j = 0; j < item.getChildCount(); j++) {
                    if (item.getChildAt(j) instanceof Switch) {
                        item.getChildAt(j).setEnabled(enabled);
                    }
                }
            }
        }
    }
    
    private void setupRingtoneSelector() {
        findViewById(R.id.ringtone_setting).setOnClickListener(v -> {
            String[] options = {"Adhan", "System Alarm", "System Notification"};
            
            new android.app.AlertDialog.Builder(this)
                .setTitle("Select Ringtone")
                .setItems(options, (dialog, which) -> {
                    SettingsManager.setAdhanRingtone(which);
                    ringtoneName.setText(options[which]);
                })
                .show();
        });
        
        // Set current ringtone name
        String[] ringtones = {"Adhan", "System Alarm", "System Notification"};
        ringtoneName.setText(ringtones[SettingsManager.getAdhanRingtone()]);
    }
    
    private void setupVolumeControl() {
        volumeSeekBar.setProgress(SettingsManager.getAdhanVolume());
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    SettingsManager.setAdhanVolume(progress);
                }
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
    
    private String getPrayerTimeDisplay(String prayer) {
        // Get prayer times from storage or fetch them
        City defaultCity = CitiesData.getCityByName(SettingsManager.getDefaultCity());
        if (defaultCity != null) {
            try {
                org.json.JSONObject cachedData = StorageManager.loadCityData(defaultCity.getNameEn());
                if (cachedData != null) {
                    String today = new java.text.SimpleDateFormat("dd/MM", java.util.Locale.getDefault()).format(new java.util.Date());
                    org.json.JSONObject prayerTimes = cachedData.getJSONObject("prayer_times");
                    if (prayerTimes.has(today)) {
                        org.json.JSONObject todayPrayers = prayerTimes.getJSONObject(today);
                        
                        String time = "";
                        switch (prayer) {
                            case "Fajr": time = todayPrayers.getString("Fajr"); break;
                            case "Dhuhr": time = todayPrayers.getString("Dohr"); break;
                            case "Asr": time = todayPrayers.getString("Asr"); break;
                            case "Maghrib": time = todayPrayers.getString("Maghreb"); break;
                            case "Isha": time = todayPrayers.getString("Isha"); break;
                        }
                        
                        // Format according to system preference
                        boolean is24Hour = android.text.format.DateFormat.is24HourFormat(this);
                        if (!is24Hour && time != null && !time.isEmpty()) {
                            try {
                                java.text.SimpleDateFormat input = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.US);
                                java.text.SimpleDateFormat output = new java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault());
                                java.util.Date timeObj = input.parse(time);
                                return output.format(timeObj);
                            } catch (Exception e) {
                                return time;
                            }
                        }
                        return time;
                    }
                }
            } catch (Exception e) {
                android.util.Log.e("AdhanSettings", "Error getting prayer time: " + e.getMessage());
            }
        }
        return "--:--";
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}