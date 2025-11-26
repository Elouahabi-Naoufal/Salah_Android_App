package com.salah.times;

import java.util.ArrayList;
import java.util.List;

public class PrayerTimes {
    private String date;
    private String fajr;
    private String sunrise;
    private String dhuhr;
    private String asr;
    private String maghrib;
    private String isha;
    private String chorok; // Calculated sunrise
    
    public PrayerTimes(String date, String fajr, String sunrise, String dhuhr, 
                      String asr, String maghrib, String isha) {
        this.date = date;
        this.fajr = fajr;
        this.sunrise = sunrise;
        this.dhuhr = dhuhr;
        this.asr = asr;
        this.maghrib = maghrib;
        this.isha = isha;
    }
    
    // Getters
    public String getDate() { return date; }
    public String getFajr() { return fajr; }
    public String getSunrise() { return sunrise; }
    public String getDhuhr() { return dhuhr; }
    public String getAsr() { return asr; }
    public String getMaghrib() { return maghrib; }
    public String getIsha() { return isha; }
    public String getChorok() { return chorok != null ? chorok : sunrise; }
    
    // Setters
    public void setChorok(String chorok) { this.chorok = chorok; }
    
    public List<Prayer> getPrayerList() {
        List<Prayer> prayers = new ArrayList<>();
        prayers.add(new Prayer("Fajr", fajr, "☽"));
        prayers.add(new Prayer("Chorok", getChorok(), "☀"));
        prayers.add(new Prayer("Dohr", dhuhr, "☉"));
        prayers.add(new Prayer("Asr", asr, "☀"));
        prayers.add(new Prayer("Maghreb", maghrib, "☾"));
        prayers.add(new Prayer("Isha", isha, "★"));
        return prayers;
    }
    
    public static class Prayer {
        private String name;
        private String time;
        private String icon;
        
        public Prayer(String name, String time, String icon) {
            this.name = name;
            this.time = time;
            this.icon = icon;
        }
        
        public String getName() { return name; }
        public String getTime() { return time; }
        public String getIcon() { return icon; }
    }
}