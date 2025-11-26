package com.salah.times;

public class City {
    private int id;
    private String key;
    private String nameEn;
    private String nameAr;
    private String nameFr;
    private double latitude;
    private double longitude;
    
    public City(int id, String key, String nameEn, String nameAr, String nameFr, double latitude, double longitude) {
        this.id = id;
        this.key = key;
        this.nameEn = nameEn;
        this.nameAr = nameAr;
        this.nameFr = nameFr;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    // Getters
    public int getId() { return id; }
    public String getKey() { return key; }
    public String getNameEn() { return nameEn; }
    public String getNameAr() { return nameAr; }
    public String getNameFr() { return nameFr; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    
    public String getName(String language) {
        switch (language) {
            case "ar": return nameAr;
            case "fr": return nameFr;
            default: return nameEn;
        }
    }
}