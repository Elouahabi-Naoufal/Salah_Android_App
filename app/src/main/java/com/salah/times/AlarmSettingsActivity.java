package com.salah.times;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class AlarmSettingsActivity extends AppCompatActivity {
    private String[] prayers = {"fajr", "dhuhr", "asr", "maghrib", "isha"};
    private String[] days = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.applyTheme();
        setContentView(R.layout.activity_alarm_settings);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(TranslationManager.tr("alarms.alarms"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        setupBulkToggle();
        setupPrayerAlarms();
    }
    
    private void setupBulkToggle() {
        TextView bulkLabel = findViewById(R.id.bulk_enable_label);
        bulkLabel.setText(TranslationManager.tr("alarms.bulk_enable"));
        
        SwitchMaterial bulkSwitch = findViewById(R.id.bulk_enable_switch);
        bulkSwitch.setChecked(areAllAlarmsEnabled());
        
        bulkSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (String prayer : prayers) {
                for (String day : days) {
                    DatabaseHelper.getInstance(this).saveSetting("alarm_" + prayer + "_" + day, String.valueOf(isChecked));
                }
            }
            updatePrayerAlarms();
        });
    }
    
    private boolean areAllAlarmsEnabled() {
        for (String prayer : prayers) {
            for (String day : days) {
                if (!Boolean.parseBoolean(DatabaseHelper.getInstance(this).getSetting("alarm_" + prayer + "_" + day, "true"))) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private void setupPrayerAlarms() {
        LinearLayout container = findViewById(R.id.alarms_container);
        container.removeAllViews();
        
        for (String prayer : prayers) {
            addPrayerAlarmItem(container, prayer);
        }
    }
    
    private void updatePrayerAlarms() {
        setupPrayerAlarms();
        SwitchMaterial bulkSwitch = findViewById(R.id.bulk_enable_switch);
        bulkSwitch.setChecked(areAllAlarmsEnabled());
    }
    
    private void addPrayerAlarmItem(LinearLayout container, String prayer) {
        View item = getLayoutInflater().inflate(R.layout.item_prayer_alarm, container, false);
        
        TextView prayerName = item.findViewById(R.id.prayer_name);
        prayerName.setText(TranslationManager.tr("prayers." + prayer));
        
        TextView daysSummary = item.findViewById(R.id.days_summary);
        daysSummary.setText(TranslationManager.tr("alarms.configure_days"));
        
        SwitchMaterial prayerSwitch = item.findViewById(R.id.prayer_switch);
        boolean anyEnabled = false;
        for (String day : days) {
            if (Boolean.parseBoolean(DatabaseHelper.getInstance(this).getSetting("alarm_" + prayer + "_" + day, "true"))) {
                anyEnabled = true;
                break;
            }
        }
        prayerSwitch.setChecked(anyEnabled);
        
        prayerSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (String day : days) {
                DatabaseHelper.getInstance(this).saveSetting("alarm_" + prayer + "_" + day, String.valueOf(isChecked));
            }
            updateDaysSummary(daysSummary, prayer);
            updateBulkSwitch();
        });
        
        item.setOnClickListener(v -> showDaySelectionDialog(prayer, prayerSwitch, daysSummary));
        
        updateDaysSummary(daysSummary, prayer);
        container.addView(item);
    }
    
    private void updateDaysSummary(TextView daysSummary, String prayer) {
        int enabledCount = 0;
        for (String day : days) {
            if (Boolean.parseBoolean(DatabaseHelper.getInstance(this).getSetting("alarm_" + prayer + "_" + day, "true"))) {
                enabledCount++;
            }
        }
        
        if (enabledCount == 0) {
            daysSummary.setText(TranslationManager.tr("alarms.configure_days"));
        } else if (enabledCount == 7) {
            daysSummary.setText(TranslationManager.tr("days.monday") + " - " + TranslationManager.tr("days.sunday"));
        } else {
            daysSummary.setText(enabledCount + " " + TranslationManager.tr("days.monday").toLowerCase() + "s");
        }
    }
    
    private void updateBulkSwitch() {
        SwitchMaterial bulkSwitch = findViewById(R.id.bulk_enable_switch);
        bulkSwitch.setOnCheckedChangeListener(null);
        bulkSwitch.setChecked(areAllAlarmsEnabled());
        bulkSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (String prayer : prayers) {
                for (String day : days) {
                    DatabaseHelper.getInstance(this).saveSetting("alarm_" + prayer + "_" + day, String.valueOf(isChecked));
                }
            }
            updatePrayerAlarms();
        });
    }
    
    private void showDaySelectionDialog(String prayer, SwitchMaterial prayerSwitch, TextView daysSummary) {
        boolean[] checkedDays = new boolean[days.length];
        for (int i = 0; i < days.length; i++) {
            checkedDays[i] = Boolean.parseBoolean(DatabaseHelper.getInstance(this).getSetting("alarm_" + prayer + "_" + days[i], "true"));
        }
        
        String[] dayNames = new String[days.length];
        for (int i = 0; i < days.length; i++) {
            dayNames[i] = TranslationManager.tr("days." + days[i]);
        }
        
        new android.app.AlertDialog.Builder(this)
            .setTitle(TranslationManager.tr("prayers." + prayer) + " - " + TranslationManager.tr("alarms.select_days"))
            .setMultiChoiceItems(dayNames, checkedDays, (dialog, which, isChecked) -> {
                DatabaseHelper.getInstance(this).saveSetting("alarm_" + prayer + "_" + days[which], String.valueOf(isChecked));
            })
            .setPositiveButton(TranslationManager.tr("ok"), (dialog, which) -> {
                boolean anyEnabled = false;
                for (String day : days) {
                    if (Boolean.parseBoolean(DatabaseHelper.getInstance(this).getSetting("alarm_" + prayer + "_" + day, "true"))) {
                        anyEnabled = true;
                        break;
                    }
                }
                prayerSwitch.setChecked(anyEnabled);
                updateDaysSummary(daysSummary, prayer);
                updateBulkSwitch();
            })
            .setNegativeButton(TranslationManager.tr("cancel"), null)
            .show();
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
