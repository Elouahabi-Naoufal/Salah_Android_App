package com.salah.times;

import java.util.HashMap;
import java.util.Map;

public class TranslationManager {
    private static final Map<String, Map<String, String>> translations = new HashMap<>();
    private static String currentLanguage = "en";
    
    static {
        initTranslations();
    }
    
    private static void initTranslations() {
        // English translations
        Map<String, String> en = new HashMap<>();
        en.put("app_title", "Salah Times");
        en.put("next_prayer", "NEXT PRAYER");
        en.put("loading", "Loading prayer times...");
        en.put("refresh", "Refresh");
        en.put("settings", "Settings");
        en.put("change_city", "Change City");
        en.put("fajr", "Fajr");
        en.put("sunrise", "Sunrise");
        en.put("chorok", "Chorok");
        en.put("dhuhr", "Dhuhr");
        en.put("dohr", "Dohr");
        en.put("asr", "Asr");
        en.put("maghrib", "Maghrib");
        en.put("maghreb", "Maghreb");
        en.put("isha", "Isha");
        translations.put("en", en);
        
        // Arabic translations
        Map<String, String> ar = new HashMap<>();
        ar.put("app_title", "مواقيت الصلاة");
        ar.put("next_prayer", "الصلاة القادمة");
        ar.put("loading", "جاري تحميل مواقيت الصلاة...");
        ar.put("refresh", "تحديث");
        ar.put("settings", "الإعدادات");
        ar.put("change_city", "تغيير المدينة");
        ar.put("fajr", "الفجر");
        ar.put("sunrise", "الشروق");
        ar.put("chorok", "الشروق");
        ar.put("dhuhr", "الظهر");
        ar.put("dohr", "الظهر");
        ar.put("asr", "العصر");
        ar.put("maghrib", "المغرب");
        ar.put("maghreb", "المغرب");
        ar.put("isha", "العشاء");
        translations.put("ar", ar);
        
        // French translations
        Map<String, String> fr = new HashMap<>();
        fr.put("app_title", "Horaires de Prière");
        fr.put("next_prayer", "PROCHAINE PRIÈRE");
        fr.put("loading", "Chargement des horaires...");
        fr.put("refresh", "Actualiser");
        fr.put("settings", "Paramètres");
        fr.put("change_city", "Changer de Ville");
        fr.put("fajr", "Fajr");
        fr.put("sunrise", "Lever du soleil");
        fr.put("chorok", "Lever du soleil");
        fr.put("dhuhr", "Dhuhr");
        fr.put("dohr", "Dohr");
        fr.put("asr", "Asr");
        fr.put("maghrib", "Maghrib");
        fr.put("maghreb", "Maghreb");
        fr.put("isha", "Isha");
        translations.put("fr", fr);
    }
    
    public static String tr(String key) {
        Map<String, String> langMap = translations.get(currentLanguage);
        return langMap != null ? langMap.getOrDefault(key, key) : key;
    }
    
    public static void setLanguage(String language) {
        if (translations.containsKey(language)) {
            currentLanguage = language;
        }
    }
    
    public static String getCurrentLanguage() {
        return currentLanguage;
    }
}