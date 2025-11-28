package com.salah.times;

import java.util.Calendar;

public class HijriDateManager {
    
    public static String getHijriDate() {
        Calendar cal = Calendar.getInstance();
        int gregorianYear = cal.get(Calendar.YEAR);
        
        int hijriYear = (int) ((gregorianYear - 622) * 1.030684);
        
        String language = TranslationManager.getCurrentLanguage();
        if ("ar".equals(language)) {
            return hijriYear + " هـ";
        } else {
            return hijriYear + " AH";
        }
    }
}