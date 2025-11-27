package com.salah.times;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class LanguageSelectionActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Apply theme
        ThemeManager.applyTheme();
        
        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        setContentView(R.layout.activity_language_selection);
        
        initViews();
    }
    
    private void initViews() {
        TextView welcomeText = findViewById(R.id.welcome_text);
        welcomeText.setText(TranslationManager.tr("missing_strings.welcome_with_icon"));
        
        TextView instructionText = findViewById(R.id.instruction_text);
        instructionText.setText(TranslationManager.tr("language_selection.select_language"));
        
        ListView languageList = findViewById(R.id.language_list);
        
        String[] languages = TranslationManager.getAvailableLanguages();
        String[] languageNames = new String[languages.length];
        
        for (int i = 0; i < languages.length; i++) {
            languageNames[i] = TranslationManager.getLanguageName(languages[i]);
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, languageNames);
        languageList.setAdapter(adapter);
        languageList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        languageList.setItemChecked(1, true); // Default to Arabic (index 1)
        
        Button continueButton = findViewById(R.id.continue_button);
        continueButton.setText(TranslationManager.tr("continue"));
        continueButton.setOnClickListener(v -> {
            int selectedPosition = languageList.getCheckedItemPosition();
            if (selectedPosition >= 0) {
                String selectedLanguage = languages[selectedPosition];
                SettingsManager.setLanguage(selectedLanguage);
                
                // Go to city selection
                Intent intent = new Intent(this, CitySelectionActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}