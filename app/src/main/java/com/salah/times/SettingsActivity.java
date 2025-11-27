package com.salah.times;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ThemeManager.applyTheme();
        
        setContentView(R.layout.activity_settings);
        
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        setupSettings();
    }
    
    private void setupSettings() {
        LinearLayout container = findViewById(R.id.settings_container);
        TextView currentCity = findViewById(R.id.current_city);
        currentCity.setText(SettingsManager.getDefaultCity());
        
        // Theme Setting
        addSettingItem(container, "◐", "Theme", getThemeDescription(), v -> showThemeDialog());
        
        // City Setting
        addSettingItem(container, "●", "Default City", SettingsManager.getDefaultCity(), v -> {
            Intent intent = new Intent(this, CitySelectionActivity.class);
            startActivityForResult(intent, 100);
        });
        
        addDivider(container);
        
        // Notifications
        addSettingItem(container, "◉", "Notifications", 
            SettingsManager.getNotificationsEnabled() ? "Enabled" : "Disabled", 
            v -> toggleNotifications());
        
        // Auto Update
        addSettingItem(container, "↻", "Auto Update", 
            SettingsManager.getAutoUpdate() ? "Daily" : "Manual", 
            v -> toggleAutoUpdate());
        
        addDivider(container);
        
        // Iqama Settings
        addSettingItem(container, "◷", "Iqama Delays", "Configure prayer delays", v -> {
            // Show iqama dialog
        });
        
        addDivider(container);
        
        // Actions
        addSettingItem(container, "↻", "Update All Cities", "Refresh prayer data", v -> updateAllCities());
        addSettingItem(container, "◯", "Clear Cache", "Reset stored data", v -> clearCache());
        addSettingItem(container, "↻", "Restart App", "Apply changes", v -> restartApp());
    }
    
    private void addSettingItem(LinearLayout container, String icon, String title, String subtitle, View.OnClickListener listener) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(16, 16, 16, 16);
        item.setGravity(android.view.Gravity.CENTER_VERTICAL);
        item.setClickable(true);
        item.setFocusable(true);
        android.util.TypedValue outValue = new android.util.TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        item.setBackgroundResource(outValue.resourceId);
        item.setOnClickListener(listener);
        
        // Icon
        TextView iconView = new TextView(this);
        iconView.setText(icon);
        iconView.setTextSize(20);
        iconView.setPadding(0, 0, 16, 0);
        item.addView(iconView);
        
        // Text container
        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        textContainer.setLayoutParams(textParams);
        
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(16);
        titleView.setTextColor(getColor(R.color.text_primary));
        textContainer.addView(titleView);
        
        TextView subtitleView = new TextView(this);
        subtitleView.setText(subtitle);
        subtitleView.setTextSize(14);
        subtitleView.setTextColor(getColor(R.color.text_secondary));
        textContainer.addView(subtitleView);
        
        item.addView(textContainer);
        
        // Arrow
        TextView arrow = new TextView(this);
        arrow.setText(">");
        arrow.setTextColor(getColor(R.color.text_secondary));
        item.addView(arrow);
        
        container.addView(item);
    }
    
    private void addDivider(LinearLayout container) {
        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
        divider.setBackgroundColor(getColor(R.color.divider));
        container.addView(divider);
    }
    
    private String getThemeDescription() {
        switch (SettingsManager.getTheme()) {
            case "light": return "Light";
            case "dark": return "Dark";
            default: return "Auto";
        }
    }
    
    private void showThemeDialog() {
        String[] themes = {"Auto", "Light", "Dark"};
        String[] codes = {"auto", "light", "dark"};
        
        new android.app.AlertDialog.Builder(this)
            .setTitle("Choose Theme")
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
    
    private void toggleAutoUpdate() {
        boolean enabled = !SettingsManager.getAutoUpdate();
        SettingsManager.setAutoUpdate(enabled);
        recreate();
    }
    
    private void updateAllCities() {
        Toast.makeText(this, "Updating all cities...", Toast.LENGTH_SHORT).show();
    }
    
    private void clearCache() {
        StorageManager.clearAllCityData();
        Toast.makeText(this, "Cache cleared", Toast.LENGTH_SHORT).show();
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