package com.salah.times;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;

public class AdhanSettingsActivity extends AppCompatActivity {
    
    private Switch masterSwitch;
    private LinearLayout prayersContainer;
    private TextView ringtoneName;
    private SeekBar volumeSeekBar;
    private MediaPlayer previewPlayer;
    private Ringtone previewRingtone;
    private android.os.Handler previewHandler = new android.os.Handler();
    private Runnable previewRunnable;
    
    private String[] prayers = {"Fajr", "Dhuhr", "Asr", "Maghrib", "Isha"};
    private String[] prayerIcons = {"☽", "☉", "☀", "☾", "★"};
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ThemeManager.applyTheme();
        setContentView(R.layout.activity_adhan_settings);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(TranslationManager.tr("adhan_settings.prayer_alarms"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        initViews();
        setupTexts();
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
    
    private void setupTexts() {
        TextView prayerAlarmsTitle = findViewById(R.id.prayer_alarms_title);
        prayerAlarmsTitle.setText(TranslationManager.tr("adhan_settings.prayer_alarms"));
        
        TextView soundTitle = findViewById(R.id.sound_title);
        soundTitle.setText(TranslationManager.tr("adhan_settings.sound"));
        
        TextView ringtoneLabel = findViewById(R.id.ringtone_label);
        ringtoneLabel.setText(TranslationManager.tr("adhan_settings.ringtone"));
        
        TextView volumeLabel = findViewById(R.id.volume_label);
        volumeLabel.setText(TranslationManager.tr("adhan_settings.volume"));
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
        prayerName.setText(TranslationManager.tr("prayers." + prayer.toLowerCase()));
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
            // Reschedule alarms to apply changes immediately
            rescheduleAlarms();
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
            String[] options = {
                TranslationManager.tr("adhan_settings.adhan"), 
                TranslationManager.tr("adhan_settings.system_alarm"), 
                TranslationManager.tr("adhan_settings.system_notification")
            };
            
            new android.app.AlertDialog.Builder(this)
                .setTitle(TranslationManager.tr("adhan_settings.select_ringtone"))
                .setItems(options, (dialog, which) -> {
                    SettingsManager.setAdhanRingtone(which);
                    ringtoneName.setText(options[which]);
                })
                .show();
        });
        
        // Set current ringtone name
        String[] ringtones = {
            TranslationManager.tr("adhan_settings.adhan"), 
            TranslationManager.tr("adhan_settings.system_alarm"), 
            TranslationManager.tr("adhan_settings.system_notification")
        };
        ringtoneName.setText(ringtones[SettingsManager.getAdhanRingtone()]);
    }
    
    private void setupVolumeControl() {
        volumeSeekBar.setProgress(SettingsManager.getAdhanVolume());
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    SettingsManager.setAdhanVolume(progress);
                    
                    // Cancel previous preview
                    stopPreview();
                    if (previewRunnable != null) {
                        previewHandler.removeCallbacks(previewRunnable);
                    }
                    
                    // Schedule new preview with delay
                    previewRunnable = () -> playVolumePreview(progress);
                    previewHandler.postDelayed(previewRunnable, 300);
                }
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopPreview();
            }
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Don't play on stop - only on delay
            }
        });
    }
    
    private void playVolumePreview(int volume) {
        if (volume == 0) return;
        
        int ringtoneType = SettingsManager.getAdhanRingtone();
        float volumeLevel = volume / 100.0f;
        
        try {
            if (ringtoneType == 0) { // Adhan
                Uri adhanUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.adhan);
                previewRingtone = RingtoneManager.getRingtone(this, adhanUri);
                if (previewRingtone != null) {
                    previewRingtone.setVolume(volumeLevel);
                    previewRingtone.play();
                    
                    previewHandler.postDelayed(() -> {
                        if (previewRingtone != null && previewRingtone.isPlaying()) {
                            previewRingtone.stop();
                        }
                    }, 1500);
                }
            } else { // System sounds
                stopPreview(); // Stop any existing ringtone
                
                Uri uri = ringtoneType == 1 ? 
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM) :
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    
                if (uri != null) {
                    previewRingtone = RingtoneManager.getRingtone(this, uri);
                    if (previewRingtone != null) {
                        previewRingtone.setVolume(volumeLevel);
                        previewRingtone.play();
                        
                        previewHandler.postDelayed(() -> {
                            if (previewRingtone != null && previewRingtone.isPlaying()) {
                                previewRingtone.stop();
                            }
                        }, 1500);
                    }
                }
            }
        } catch (Exception e) {
            android.util.Log.e("AdhanSettings", "Error playing volume preview: " + e.getMessage());
        }
    }
    
    private void stopPreview() {
        // Stop MediaPlayer completely for adhan
        if (previewPlayer != null) {
            try {
                if (previewPlayer.isPlaying()) {
                    previewPlayer.stop();
                }
            } catch (Exception e) {
                android.util.Log.e("AdhanSettings", "Error stopping preview player", e);
            }
        }
        
        if (previewRingtone != null) {
            try {
                if (previewRingtone.isPlaying()) {
                    previewRingtone.stop();
                }
            } catch (Exception e) {
                android.util.Log.e("AdhanSettings", "Error stopping preview ringtone", e);
            }
            previewRingtone = null;
        }
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
    
    private void rescheduleAlarms() {
        // Get current prayer times and reschedule alarms
        City defaultCity = CitiesData.getCityByName(SettingsManager.getDefaultCity());
        if (defaultCity != null) {
            try {
                org.json.JSONObject cachedData = StorageManager.loadCityData(defaultCity.getNameEn());
                if (cachedData != null) {
                    String today = new java.text.SimpleDateFormat("dd/MM", java.util.Locale.getDefault()).format(new java.util.Date());
                    org.json.JSONObject prayerTimesJson = cachedData.getJSONObject("prayer_times");
                    if (prayerTimesJson.has(today)) {
                        org.json.JSONObject todayPrayers = prayerTimesJson.getJSONObject(today);
                        PrayerTimes prayerTimes = new PrayerTimes(
                            todayPrayers.getString("Date"),
                            todayPrayers.getString("Fajr"),
                            "00:00",
                            todayPrayers.getString("Dohr"),
                            todayPrayers.getString("Asr"),
                            todayPrayers.getString("Maghreb"),
                            todayPrayers.getString("Isha")
                        );
                        PrayerAlarmManager.scheduleAllPrayerAlarms(this, prayerTimes);
                    }
                }
            } catch (Exception e) {
                android.util.Log.e("AdhanSettings", "Error rescheduling alarms: " + e.getMessage());
            }
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPreview();
        
        // Release MediaPlayer on destroy
        if (previewPlayer != null) {
            try {
                previewPlayer.release();
            } catch (Exception e) {
                android.util.Log.e("AdhanSettings", "Error releasing preview player", e);
            }
            previewPlayer = null;
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}