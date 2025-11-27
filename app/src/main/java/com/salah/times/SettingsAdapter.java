package com.salah.times;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import java.util.List;

public class SettingsAdapter extends FragmentStateAdapter {
    private IqamaManager iqamaManager;
    
    public SettingsAdapter(FragmentActivity activity, IqamaManager iqamaManager) {
        super(activity);
        this.iqamaManager = iqamaManager;
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new GeneralSettingsFragment();
            case 1: return new IqamaSettingsFragment(iqamaManager);
            case 2: return new NotificationSettingsFragment();
            default: return new GeneralSettingsFragment();
        }
    }
    
    @Override
    public int getItemCount() {
        return 3;
    }
    
    public static class GeneralSettingsFragment extends Fragment {
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, android.os.Bundle savedInstanceState) {
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(16, 16, 16, 16);
            
            // Theme Selection
            TextView themeLabel = new TextView(getContext());
            themeLabel.setText("Theme");
            themeLabel.setTextSize(16);
            themeLabel.setTypeface(null, android.graphics.Typeface.BOLD);
            layout.addView(themeLabel);
            
            Spinner themeSpinner = new Spinner(getContext());
            String[] themes = {"Auto (System)", "Light Mode", "Dark Mode"};
            String[] themeCodes = {"auto", "light", "dark"};
            ArrayAdapter<String> themeAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, themes);
            themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            themeSpinner.setAdapter(themeAdapter);
            
            String currentTheme = SettingsManager.getTheme();
            for (int i = 0; i < themeCodes.length; i++) {
                if (themeCodes[i].equals(currentTheme)) {
                    themeSpinner.setSelection(i);
                    break;
                }
            }
            
            themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedTheme = themeCodes[position];
                    SettingsManager.setTheme(selectedTheme);
                    Toast.makeText(getContext(), "Theme changed to " + themes[position] + ". Restart app to apply.", Toast.LENGTH_LONG).show();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
            
            layout.addView(themeSpinner);
            
            // Spacing
            View space0 = new View(getContext());
            space0.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 32));
            layout.addView(space0);
            
            // City Selection
            TextView cityLabel = new TextView(getContext());
            cityLabel.setText("Default City");
            cityLabel.setTextSize(16);
            cityLabel.setTypeface(null, android.graphics.Typeface.BOLD);
            layout.addView(cityLabel);
            
            Spinner citySpinner = new Spinner(getContext());
            List<City> cities = CitiesData.getAllCities();
            String[] cityNames = new String[cities.size()];
            for (int i = 0; i < cities.size(); i++) {
                cityNames[i] = cities.get(i).getNameEn();
            }
            ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, cityNames);
            cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            citySpinner.setAdapter(cityAdapter);
            
            String currentCity = SettingsManager.getDefaultCity();
            for (int i = 0; i < cityNames.length; i++) {
                if (cityNames[i].equals(currentCity)) {
                    citySpinner.setSelection(i);
                    break;
                }
            }
            
            citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedCity = cityNames[position];
                    SettingsManager.setDefaultCity(selectedCity);
                    Toast.makeText(getContext(), "City changed to " + selectedCity + ". Restart app to see changes.", Toast.LENGTH_LONG).show();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
            
            layout.addView(citySpinner);
            
            // Auto Update Toggle
            CheckBox autoUpdateCheck = new CheckBox(getContext());
            autoUpdateCheck.setText("Auto Update Prayer Times Daily");
            autoUpdateCheck.setChecked(SettingsManager.getAutoUpdate());
            autoUpdateCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SettingsManager.setAutoUpdate(isChecked);
                String status = isChecked ? "enabled" : "disabled";
                Toast.makeText(getContext(), "Auto update " + status, Toast.LENGTH_SHORT).show();
            });
            layout.addView(autoUpdateCheck);
            
            // Restart App Button
            Button restartButton = new Button(getContext());
            restartButton.setText("Restart App");
            restartButton.setTextColor(Color.WHITE);
            restartButton.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Restarting app...", Toast.LENGTH_SHORT).show();
                
                Intent restartIntent = new Intent(getContext(), MainActivity.class);
                restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                
                android.app.AlarmManager alarmManager = (android.app.AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(
                    getContext(), 0, restartIntent, 
                    android.app.PendingIntent.FLAG_ONE_SHOT | android.app.PendingIntent.FLAG_IMMUTABLE
                );
                
                alarmManager.setExact(android.app.AlarmManager.RTC, 
                    System.currentTimeMillis() + 500, pendingIntent);
                
                if (getActivity() != null) {
                    getActivity().finishAffinity();
                }
                System.exit(0);
            });
            layout.addView(restartButton);
            
            return layout;
        }

    }
    
    public static class IqamaSettingsFragment extends Fragment {
        private IqamaManager iqamaManager;
        
        public IqamaSettingsFragment(IqamaManager iqamaManager) {
            this.iqamaManager = iqamaManager;
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, android.os.Bundle savedInstanceState) {
            ScrollView scrollView = new ScrollView(getContext());
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(16, 16, 16, 16);
            
            TextView title = new TextView(getContext());
            title.setText("Iqama Delay Settings");
            title.setTextSize(18);
            title.setTypeface(null, android.graphics.Typeface.BOLD);
            layout.addView(title);
            
            TextView description = new TextView(getContext());
            description.setText("Set the delay time between Adhan and Iqama for each prayer");
            description.setTextSize(14);
            description.setPadding(0, 8, 0, 24);
            layout.addView(description);
            
            String[] prayers = {"Fajr", "Dhuhr", "Asr", "Maghrib", "Isha"};
            String[] icons = {"☽", "☉", "☀", "☾", "★"};
            
            for (int i = 0; i < prayers.length; i++) {
                final String prayer = prayers[i];
                
                LinearLayout prayerLayout = new LinearLayout(getContext());
                prayerLayout.setOrientation(LinearLayout.HORIZONTAL);
                prayerLayout.setPadding(0, 12, 0, 12);
                prayerLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
                
                TextView prayerLabel = new TextView(getContext());
                prayerLabel.setText(icons[i] + " " + prayer);
                prayerLabel.setTextSize(16);
                prayerLabel.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                prayerLayout.addView(prayerLabel);
                
                Spinner delaySpinner = new Spinner(getContext());
                String[] delays = new String[61];
                for (int j = 0; j <= 60; j++) {
                    delays[j] = j + " min";
                }
                ArrayAdapter<String> delayAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, delays);
                delayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                delaySpinner.setAdapter(delayAdapter);
                delaySpinner.setSelection(iqamaManager.getIqamaDelay(prayer));
                
                delaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        iqamaManager.setIqamaDelay(prayer, position);
                        Toast.makeText(getContext(), prayer + " Iqama delay set to " + position + " minutes", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
                
                prayerLayout.addView(delaySpinner);
                layout.addView(prayerLayout);
            }
            
            TextView infoText = new TextView(getContext());
            infoText.setText("Iqama is the second call to prayer, indicating that the prayer is about to begin. Set appropriate delays based on your mosque's schedule.");
            infoText.setTextSize(12);
            layout.addView(infoText);
            
            scrollView.addView(layout);
            return scrollView;
        }
    }
    
    public static class NotificationSettingsFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, android.os.Bundle savedInstanceState) {
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(16, 16, 16, 16);
            
            TextView title = new TextView(getContext());
            title.setText("Notification Settings");
            title.setTextSize(18);
            title.setTypeface(null, android.graphics.Typeface.BOLD);
            layout.addView(title);
            
            CheckBox notificationsEnabled = new CheckBox(getContext());
            notificationsEnabled.setText("Enable Prayer Time Notifications");
            notificationsEnabled.setChecked(SettingsManager.getNotificationsEnabled());
            notificationsEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SettingsManager.setNotificationsEnabled(isChecked);
                String status = isChecked ? "enabled" : "disabled";
                Toast.makeText(getContext(), "Notifications " + status, Toast.LENGTH_SHORT).show();
                
                if (isChecked) {
                    Intent serviceIntent = new Intent(getContext(), PrayerNotificationService.class);
                    getContext().startForegroundService(serviceIntent);
                } else {
                    Intent serviceIntent = new Intent(getContext(), PrayerNotificationService.class);
                    getContext().stopService(serviceIntent);
                }
            });
            layout.addView(notificationsEnabled);
            
            TextView infoText = new TextView(getContext());
            infoText.setText("When enabled, you'll receive:\n\n• Current prayer time in notification bar\n• Countdown to next prayer\n• Persistent notification with all prayer times\n• Real-time updates");
            infoText.setTextSize(14);
            layout.addView(infoText);
            
            return layout;
        }
    }
}