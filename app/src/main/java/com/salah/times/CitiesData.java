package com.salah.times;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CitiesData {
    private static final Map<String, City> cities = new HashMap<>();
    
    static {
        // Initialize cities from Python data
        cities.put("Tangier", new City(101, "Tangier", "Tangier", "طنجة", "Tanger", 35.7595, -5.8340));
        cities.put("Casablanca", new City(71, "Casablanca", "Casablanca", "الدار البيضاء", "Casablanca", 33.5731, -7.5898));
        cities.put("Rabat", new City(95, "Rabat", "Rabat", "الرباط", "Rabat", 34.0209, -6.8416));
        cities.put("Marrakech", new City(88, "Marrakech", "Marrakech", "مراكش", "Marrakech", 31.6295, -7.9811));
        cities.put("Fes", new City(78, "Fes", "Fes", "فاس", "Fes", 34.0181, -5.0078));
        cities.put("Agadir", new City(66, "Agadir", "Agadir", "أكادير", "Agadir", 30.4278, -9.5981));
        cities.put("Meknes", new City(89, "Meknes", "Meknes", "مكناس", "Meknes", 33.8935, -5.5473));
        cities.put("Oujda", new City(93, "Oujda", "Oujda", "وجدة", "Oujda", 34.6814, -1.9086));
    }
    
    public static List<City> getAllCities() {
        return new ArrayList<>(cities.values());
    }
    
    public static City getCity(String key) {
        return cities.get(key);
    }
    
    public static List<City> searchCities(String query, String language) {
        List<City> results = new ArrayList<>();
        for (City city : cities.values()) {
            String name = city.getName(language);
            if (name.toLowerCase().contains(query.toLowerCase())) {
                results.add(city);
            }
        }
        return results;
    }
    
    public static City getCityByName(String name) {
        for (City city : cities.values()) {
            if (city.getNameEn().equals(name)) {
                return city;
            }
        }
        return cities.get("Casablanca"); // Default city
    }
}