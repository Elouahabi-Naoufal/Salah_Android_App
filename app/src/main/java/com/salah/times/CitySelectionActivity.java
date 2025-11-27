package com.salah.times;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class CitySelectionActivity extends AppCompatActivity {
    private EditText searchInput;
    private ListView cityList;
    private ArrayAdapter<String> adapter;
    private List<City> allCities;
    private List<String> displayCities;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Apply theme
        ThemeManager.applyTheme();
        
        setContentView(R.layout.activity_city_selection);
        initViews();
        setupCityList();
    }
    
    private void initViews() {
        TextView welcomeText = findViewById(R.id.welcome_text);
        welcomeText.setText(TranslationManager.tr("missing_strings.welcome_with_icon"));
        
        TextView instructionText = findViewById(R.id.instruction_text);
        instructionText.setText(TranslationManager.tr("missing_strings.select_city_instruction"));
        
        searchInput = findViewById(R.id.search_input);
        searchInput.setHint(TranslationManager.tr("city_selection.search_city"));
        
        cityList = findViewById(R.id.city_list);
        
        Button cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setText(TranslationManager.tr("cancel"));
        cancelButton.setOnClickListener(v -> finish());
        
        Button selectButton = findViewById(R.id.select_button);
        selectButton.setText(TranslationManager.tr("missing_strings.set_as_default"));
        selectButton.setOnClickListener(v -> selectCity());
    }
    
    private void setupCityList() {
        allCities = CitiesData.getAllCities();
        displayCities = new ArrayList<>();
        
        String currentLang = TranslationManager.getCurrentLanguage();
        for (City city : allCities) {
            displayCities.add(city.getName(currentLang));
        }
        
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, displayCities);
        cityList.setAdapter(adapter);
        cityList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
        // Default selection (Casablanca)
        String defaultCity = SettingsManager.getDefaultCity();
        for (int i = 0; i < allCities.size(); i++) {
            if (allCities.get(i).getNameEn().equals(defaultCity)) {
                cityList.setItemChecked(i, true);
                break;
            }
        }
        
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCities(s.toString());
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void filterCities(String query) {
        displayCities.clear();
        allCities.clear();
        
        String currentLang = TranslationManager.getCurrentLanguage();
        
        if (query.isEmpty()) {
            allCities.addAll(CitiesData.getAllCities());
            for (City city : allCities) {
                displayCities.add(city.getName(currentLang));
            }
        } else {
            // Search in current language
            List<City> searchResults = CitiesData.searchCities(query, currentLang);
            allCities.addAll(searchResults);
            for (City city : searchResults) {
                displayCities.add(city.getName(currentLang));
            }
        }
        
        adapter.notifyDataSetChanged();
        
        // Auto-select first item if available
        if (!displayCities.isEmpty()) {
            cityList.setItemChecked(0, true);
        }
    }
    
    private void selectCity() {
        int selectedPosition = cityList.getCheckedItemPosition();
        if (selectedPosition >= 0 && selectedPosition < allCities.size()) {
            City selectedCity = allCities.get(selectedPosition);
            SettingsManager.setDefaultCity(selectedCity.getNameEn());
            
            // Return to main activity
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}