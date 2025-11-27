package com.salah.times;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
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
        
        public GeneralSettingsFragment() {
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, android.os.Bundle savedInstanceState) {
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(32, 32, 32, 32);
            
            // Language selection
            TextView langLabel = new TextView(getContext());
            langLabel.setText("Language");
            langLabel.setTextSize(16);
            layout.addView(langLabel);
            
            Spinner langSpinner = new Spinner(getContext());
            String[] languages = {"English", "العربية", "Français"};
            String[] langCodes = {"en", "ar", "fr"};
            ArrayAdapter<String> langAdapter = new ArrayAdapter<>(getContext(), 
                android.R.layout.simple_spinner_item, languages);
            langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            langSpinner.setAdapter(langAdapter);
            
            // Set current language
            String currentLang = SettingsManager.getLanguage();
            for (int i = 0; i < langCodes.length; i++) {
                if (langCodes[i].equals(currentLang)) {
                    langSpinner.setSelection(i);
                    break;
                }
            }
            
            langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    SettingsManager.setLanguage(langCodes[position]);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
            
            layout.addView(langSpinner);
            
            // City selection
            TextView cityLabel = new TextView(getContext());
            cityLabel.setText("Default City");
            cityLabel.setTextSize(16);
            cityLabel.setPadding(0, 32, 0, 8);
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
            
            // Set current city
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
                    SettingsManager.setDefaultCity(cityNames[position]);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
            
            layout.addView(citySpinner);
            
            // Theme selection
            TextView themeLabel = new TextView(getContext());
            themeLabel.setText("Theme");
            themeLabel.setTextSize(16);
            themeLabel.setPadding(0, 32, 0, 8);
            layout.addView(themeLabel);
            
            Spinner themeSpinner = new Spinner(getContext());
            String[] themes = {"Auto", "Light", "Dark"};
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
                    SettingsManager.setTheme(themeCodes[position]);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
            
            layout.addView(themeSpinner);
            
            // Auto update toggle
            CheckBox autoUpdateCheck = new CheckBox(getContext());
            autoUpdateCheck.setText("Auto Update Prayer Times");
            autoUpdateCheck.setChecked(SettingsManager.getAutoUpdate());
            autoUpdateCheck.setPadding(0, 32, 0, 0);
            autoUpdateCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SettingsManager.setAutoUpdate(isChecked);
            });
            layout.addView(autoUpdateCheck);
            
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
            layout.setPadding(32, 32, 32, 32);
            
            String[] prayers = {"Fajr", "Dhuhr", "Asr", "Maghrib", "Isha"};
            String[] icons = {"☽", "☉", "☀", "☾", "★"};
            
            for (int i = 0; i < prayers.length; i++) {
                LinearLayout prayerLayout = new LinearLayout(getContext());
                prayerLayout.setOrientation(LinearLayout.HORIZONTAL);
                prayerLayout.setPadding(0, 16, 0, 16);
                
                TextView icon = new TextView(getContext());
                icon.setText(icons[i]);
                icon.setTextSize(20);
                icon.setPadding(0, 0, 16, 0);
                prayerLayout.addView(icon);
                
                TextView prayerName = new TextView(getContext());
                prayerName.setText(prayers[i]);
                prayerName.setTextSize(16);
                prayerName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                prayerLayout.addView(prayerName);
                
                Spinner delaySpinner = new Spinner(getContext());
                String[] delays = new String[61];
                for (int j = 0; j <= 60; j++) {
                    delays[j] = j + " min";
                }
                ArrayAdapter<String> delayAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, delays);
                delayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                delaySpinner.setAdapter(delayAdapter);
                delaySpinner.setSelection(iqamaManager.getIqamaDelay(prayers[i]));
                prayerLayout.addView(delaySpinner);
                
                layout.addView(prayerLayout);
            }
            
            scrollView.addView(layout);
            return scrollView;
        }
    }
    
    public static class NotificationSettingsFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, android.os.Bundle savedInstanceState) {
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(32, 32, 32, 32);
            
            TextView title = new TextView(getContext());
            title.setText("Notification Settings");
            title.setTextSize(18);
            layout.addView(title);
            
            CheckBox notificationsEnabled = new CheckBox(getContext());
            notificationsEnabled.setText("Enable Notifications");
            notificationsEnabled.setChecked(SettingsManager.getNotificationsEnabled());
            notificationsEnabled.setPadding(0, 16, 0, 0);
            notificationsEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SettingsManager.setNotificationsEnabled(isChecked);
            });
            layout.addView(notificationsEnabled);
            
            return layout;
        }
    }
}