package com.salah.times;

import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TranslationManager {
    private static String currentLanguage = "en";
    private static Map<String, JSONObject> translations = new HashMap<>();
    private static Context appContext;
    
    public static void init(Context context) {
        appContext = context.getApplicationContext();
        loadTranslations();
    }
    
    private static void loadTranslations() {
        String[] languages = {"en", "ar", "fr", "es"};
        
        for (String lang : languages) {
            try {
                String fileName = "config/languages/" + lang + ".json";
                InputStream is = appContext.getAssets().open(fileName);
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                is.close();
                
                String json = new String(buffer, "UTF-8");
                translations.put(lang, new JSONObject(json));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static String tr(String key) {
        return tr(key, new String[]{});
    }
    
    public static String tr(String key, String... params) {
        String result = getTranslation(key);
        
        // Replace placeholders {0}, {1}, etc. with parameters
        for (int i = 0; i < params.length; i++) {
            result = result.replace("{" + i + "}", params[i]);
        }
        
        return result;
    }
    
    private static String getTranslation(String key) {
        JSONObject langObj = translations.get(currentLanguage);
        
        // Try to get from current language
        String result = getNestedValue(langObj, key);
        if (result != null) return result;
        
        // Fallback to English
        JSONObject enObj = translations.get("en");
        result = getNestedValue(enObj, key);
        if (result != null) return result;
        
        // Return key if not found
        return key;
    }
    
    private static String getNestedValue(JSONObject obj, String key) {
        if (obj == null) return null;
        
        try {
            // Try direct key first
            if (obj.has(key)) {
                return obj.getString(key);
            }
            
            // Try nested keys (e.g., "prayers.fajr")
            String[] parts = key.split("\\.");
            JSONObject current = obj;
            
            for (int i = 0; i < parts.length - 1; i++) {
                if (current.has(parts[i])) {
                    current = current.getJSONObject(parts[i]);
                } else {
                    return null;
                }
            }
            
            String lastKey = parts[parts.length - 1];
            if (current.has(lastKey)) {
                return current.getString(lastKey);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static void setLanguage(String language) {
        currentLanguage = language;
    }
    
    public static String getCurrentLanguage() {
        return currentLanguage;
    }
    
    public static String[] getAvailableLanguages() {
        return new String[]{"en", "ar", "fr", "es"};
    }
    
    public static String getLanguageName(String code) {
        switch (code) {
            case "en": return "English";
            case "ar": return "العربية";
            case "fr": return "Français";
            case "es": return "Español";
            default: return code;
        }
    }
}