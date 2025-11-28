package com.salah.times;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ThemeManager.applyTheme();
        
        setContentView(R.layout.activity_settings);
        
        getSupportActionBar().setTitle(TranslationManager.tr("settings"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        setupSettings();
    }
    
    private void setupSettings() {
        LinearLayout container = findViewById(R.id.settings_container);
        
        // Language Setting
        addSettingItem(container, "◉", TranslationManager.tr("settings_items.language"), getLanguageDescription(), v -> showLanguageDialog());
        
        // Theme Setting
        addSettingItem(container, "◐", TranslationManager.tr("settings_items.theme"), getThemeDescription(), v -> showThemeDialog());
        
        // City Setting
        String currentCityEn = SettingsManager.getDefaultCity();
        City currentCityObj = CitiesData.getCityByName(currentCityEn);
        String cityDisplayName = currentCityObj.getName(TranslationManager.getCurrentLanguage());
        addSettingItem(container, "●", TranslationManager.tr("settings_items.default_city"), cityDisplayName, v -> {
            Intent intent = new Intent(this, CitySelectionActivity.class);
            startActivityForResult(intent, 100);
        });
        
        addDivider(container);
        
        // Notifications
        addSettingItem(container, "◉", TranslationManager.tr("settings_items.notifications"), 
            SettingsManager.getNotificationsEnabled() ? TranslationManager.tr("settings_items.notifications_enabled") : TranslationManager.tr("settings_items.notifications_disabled"), 
            v -> toggleNotifications());
        
        // Adan Alarm
        addSettingItem(container, "🔔", TranslationManager.tr("settings_items.adan"), 
            SettingsManager.getAdanEnabled() ? TranslationManager.tr("settings_items.enabled") : TranslationManager.tr("settings_items.disabled"), 
            v -> toggleAdan());
        

        
        addDivider(container);
        
        // Iqama Settings
        addSettingItem(container, "◷", TranslationManager.tr("settings_items.iqama_delays"), TranslationManager.tr("settings_items.iqama_description"), v -> {
            showIqamaDialog();
        });
        
        addDivider(container);
        
        // Actions
        addSettingItem(container, "↻", TranslationManager.tr("settings_items.update_all_cities"), TranslationManager.tr("settings_items.update_description"), v -> updateAllCities());
        addSettingItem(container, "◯", TranslationManager.tr("settings_items.clear_cache"), TranslationManager.tr("settings_items.cache_description"), v -> clearCache());
        addSettingItem(container, "↻", TranslationManager.tr("settings_items.restart_app"), TranslationManager.tr("settings_items.restart_description"), v -> restartApp());
    }
    
    private void addSettingItem(LinearLayout container, String icon, String title, String subtitle, View.OnClickListener listener) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(24, 20, 24, 20);
        item.setGravity(android.view.Gravity.CENTER_VERTICAL);
        item.setClickable(true);
        item.setFocusable(true);
        android.util.TypedValue outValue = new android.util.TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        item.setBackgroundResource(outValue.resourceId);
        item.setOnClickListener(listener);
        
        // Icon container
        LinearLayout iconContainer = new LinearLayout(this);
        iconContainer.setLayoutParams(new LinearLayout.LayoutParams(48, 48));
        iconContainer.setGravity(android.view.Gravity.CENTER);
        
        TextView iconView = new TextView(this);
        iconView.setText(icon);
        iconView.setTextSize(22);
        iconView.setTextColor(getColor(R.color.primary_green));
        iconContainer.addView(iconView);
        item.addView(iconContainer);
        
        // Text container
        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        textParams.setMarginStart(16);
        textContainer.setLayoutParams(textParams);
        
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(18);
        titleView.setTextColor(getColor(R.color.text_primary));
        titleView.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL);
        textContainer.addView(titleView);
        
        TextView subtitleView = new TextView(this);
        subtitleView.setText(subtitle);
        subtitleView.setTextSize(15);
        subtitleView.setTextColor(getColor(R.color.text_secondary));
        LinearLayout.LayoutParams subtitleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subtitleParams.topMargin = 4;
        subtitleView.setLayoutParams(subtitleParams);
        textContainer.addView(subtitleView);
        
        item.addView(textContainer);
        
        // Arrow
        TextView arrow = new TextView(this);
        arrow.setText("›");
        arrow.setTextSize(20);
        arrow.setTextColor(getColor(R.color.text_secondary));
        arrow.setAlpha(0.6f);
        item.addView(arrow);
        
        container.addView(item);
    }
    
    private void addDivider(LinearLayout container) {
        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 24));
        container.addView(spacer);
        
        View divider = new View(this);
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
        dividerParams.setMargins(24, 0, 24, 0);
        divider.setLayoutParams(dividerParams);
        divider.setBackgroundColor(getColor(R.color.divider));
        divider.setAlpha(0.3f);
        container.addView(divider);
        
        View spacer2 = new View(this);
        spacer2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 8));
        container.addView(spacer2);
    }
    
    private String getLanguageDescription() {
        return TranslationManager.getLanguageName(SettingsManager.getLanguage());
    }
    
    private String getThemeDescription() {
        switch (SettingsManager.getTheme()) {
            case "light": return TranslationManager.tr("settings_items.theme_light");
            case "dark": return TranslationManager.tr("settings_items.theme_dark");
            default: return TranslationManager.tr("settings_items.theme_auto");
        }
    }
    
    private void showLanguageDialog() {
        String[] languages = TranslationManager.getAvailableLanguages();
        String[] languageNames = new String[languages.length];
        
        for (int i = 0; i < languages.length; i++) {
            languageNames[i] = TranslationManager.getLanguageName(languages[i]);
        }
        
        new android.app.AlertDialog.Builder(this)
            .setTitle(TranslationManager.tr("missing_strings.choose_language"))
            .setItems(languageNames, (dialog, which) -> {
                SettingsManager.setLanguage(languages[which]);
                recreate();
            })
            .show();
    }
    
    private void showThemeDialog() {
        String[] themes = {
            TranslationManager.tr("settings_items.theme_auto"),
            TranslationManager.tr("settings_items.theme_light"), 
            TranslationManager.tr("settings_items.theme_dark")
        };
        String[] codes = {"auto", "light", "dark"};
        
        new android.app.AlertDialog.Builder(this)
            .setTitle(TranslationManager.tr("missing_strings.choose_theme"))
            .setItems(themes, (dialog, which) -> {
                SettingsManager.setTheme(codes[which]);
                recreate();
            })
            .show();
    }
    
    private void toggleNotifications() {
        boolean enabled = !SettingsManager.getNotificationsEnabled();
        SettingsManager.setNotificationsEnabled(enabled);
        recreate();
    }
    
    private void toggleAdan() {
        boolean enabled = !SettingsManager.getAdanEnabled();
        
        if (enabled && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                // Request exact alarm permission
                android.content.Intent intent = new android.content.Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                Toast.makeText(this, "Please allow exact alarms for prayer notifications", Toast.LENGTH_LONG).show();
                return;
            }
        }
        
        SettingsManager.setAdanEnabled(enabled);
        recreate();
    }
    
    private void showIqamaDialog() {
        String[] prayers = {"Fajr", "Dhuhr", "Asr", "Maghrib", "Isha"};
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);
        
        for (String prayer : prayers) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);
            
            TextView label = new TextView(this);
            label.setText(prayer + ":");
            label.setTextSize(16);
            label.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            
            EditText input = new EditText(this);
            input.setText(String.valueOf(SettingsManager.getIqamaDelay(prayer)));
            input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
            input.setLayoutParams(new LinearLayout.LayoutParams(100, LinearLayout.LayoutParams.WRAP_CONTENT));
            input.setTag(prayer);
            
            TextView minLabel = new TextView(this);
            minLabel.setText(" min");
            minLabel.setTextSize(14);
            
            row.addView(label);
            row.addView(input);
            row.addView(minLabel);
            layout.addView(row);
        }
        
        new android.app.AlertDialog.Builder(this)
            .setTitle("Iqama Delays")
            .setView(layout)
            .setPositiveButton("Save", (dialog, which) -> {
                for (int i = 0; i < layout.getChildCount(); i++) {
                    LinearLayout row = (LinearLayout) layout.getChildAt(i);
                    EditText input = (EditText) row.getChildAt(1);
                    String prayer = (String) input.getTag();
                    try {
                        int minutes = Integer.parseInt(input.getText().toString());
                        SettingsManager.setIqamaDelay(prayer, minutes);
                    } catch (NumberFormatException e) {
                        // Keep default value
                    }
                }
                Toast.makeText(this, "Iqama delays saved", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    

    
    private void updateAllCities() {
        Toast.makeText(this, TranslationManager.tr("missing_strings.updating_cities"), Toast.LENGTH_SHORT).show();
    }
    
    private void clearCache() {
        StorageManager.clearAllCityData();
        Toast.makeText(this, TranslationManager.tr("missing_strings.cache_cleared_simple"), Toast.LENGTH_SHORT).show();
    }
    
    private void restartApp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            recreate(); // Refresh settings to show new city
        }
    }
    

    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}