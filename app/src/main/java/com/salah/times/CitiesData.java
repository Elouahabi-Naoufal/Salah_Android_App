package com.salah.times;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CitiesData {
    private static final Map<String, City> cities = new HashMap<>();
    
    static {
        // Initialize all 42 cities from Python data
        cities.put("Tangier", new City(101, "Tangier", "Tangier", "طنجة", "Tanger", 35.7595, -5.8340));
        cities.put("Casablanca", new City(71, "Casablanca", "Casablanca", "الدار البيضاء", "Casablanca", 33.5731, -7.5898));
        cities.put("Rabat", new City(95, "Rabat", "Rabat", "الرباط", "Rabat", 34.0209, -6.8416));
        cities.put("Marrakech", new City(88, "Marrakech", "Marrakech", "مراكش", "Marrakech", 31.6295, -7.9811));
        cities.put("Fes", new City(78, "Fes", "Fes", "فاس", "Fes", 34.0181, -5.0078));
        cities.put("Agadir", new City(66, "Agadir", "Agadir", "أكادير", "Agadir", 30.4278, -9.5981));
        cities.put("Meknes", new City(89, "Meknes", "Meknes", "مكناس", "Meknes", 33.8935, -5.5473));
        cities.put("Oujda", new City(93, "Oujda", "Oujda", "وجدة", "Oujda", 34.6814, -1.9086));
        cities.put("Kenitra", new City(81, "Kenitra", "Kenitra", "القنيطرة", "Kenitra", 34.2610, -6.5802));
        cities.put("Tetouan", new City(100, "Tetouan", "Tetouan", "تطوان", "Tetouan", 35.5889, -5.3626));
        cities.put("Safi", new City(96, "Safi", "Safi", "آسفي", "Safi", 32.2994, -9.2372));
        cities.put("Mohammedia", new City(90, "Mohammedia", "Mohammedia", "المحمدية", "Mohammedia", 33.6866, -7.3837));
        cities.put("Khouribga", new City(83, "Khouribga", "Khouribga", "خريبكة", "Khouribga", 32.8811, -6.9063));
        cities.put("El Jadida", new City(74, "El Jadida", "El Jadida", "الجديدة", "El Jadida", 33.2316, -8.5007));
        cities.put("Taza", new City(105, "Taza", "Taza", "تازة", "Taza", 34.2133, -4.0103));
        cities.put("Nador", new City(91, "Nador", "Nador", "الناظور", "Nador", 35.1681, -2.9287));
        cities.put("Settat", new City(98, "Settat", "Settat", "سطات", "Settat", 33.0018, -7.6164));
        cities.put("Larache", new City(87, "Larache", "Larache", "العرائش", "Larache", 35.1932, -6.1563));
        cities.put("Khenifra", new City(82, "Khenifra", "Khenifra", "خنيفرة", "Khenifra", 32.9359, -5.6675));
        cities.put("Essaouira", new City(76, "Essaouira", "Essaouira", "الصويرة", "Essaouira", 31.5085, -9.7595));
        cities.put("Chefchaouen", new City(72, "Chefchaouen", "Chefchaouen", "شفشاون", "Chefchaouen", 35.1688, -5.2636));
        cities.put("Beni Mellal", new City(68, "Beni Mellal", "Beni Mellal", "بني ملال", "Beni Mellal", 32.3373, -6.3498));
        cities.put("Al Hoceima", new City(79, "Al Hoceima", "Al Hoceima", "الحسيمة", "Al Hoceima", 35.2517, -3.9316));
        cities.put("Taroudant", new City(103, "Taroudant", "Taroudant", "تارودانت", "Taroudant", 30.4703, -8.8770));
        cities.put("Ouazzane", new City(92, "Ouazzane", "Ouazzane", "وزان", "Ouazzane", 34.7936, -5.5836));
        cities.put("Sefrou", new City(97, "Sefrou", "Sefrou", "صفرو", "Sefrou", 33.8307, -4.8372));
        cities.put("Berkane", new City(69, "Berkane", "Berkane", "بركان", "Berkane", 34.9218, -2.3200));
        cities.put("Errachidia", new City(75, "Errachidia", "Errachidia", "الراشيدية", "Errachidia", 31.9314, -4.4244));
        cities.put("Laayoune", new City(85, "Laayoune", "Laayoune", "العيون", "Laayoune", 27.1253, -13.1625));
        cities.put("Tiznit", new City(106, "Tiznit", "Tiznit", "تزنيت", "Tiznit", 29.6974, -9.7316));
        cities.put("Ifrane", new City(80, "Ifrane", "Ifrane", "إفران", "Ifrane", 33.5228, -5.1106));
        cities.put("Zagora", new City(107, "Zagora", "Zagora", "زاكورة", "Zagora", 30.3314, -5.8372));
        cities.put("Dakhla", new City(73, "Dakhla", "Dakhla", "الداخلة", "Dakhla", 23.6848, -15.9570));
        cities.put("Tan Tan", new City(102, "Tan Tan", "Tan Tan", "طانطان", "Tan-Tan", 28.4378, -11.1031));
        cities.put("Sidi Kacem", new City(99, "Sidi Kacem", "Sidi Kacem", "سيدي قاسم", "Sidi Kacem", 34.2214, -5.7081));
        cities.put("Ksar Lekbir", new City(84, "Ksar Lekbir", "Ksar Lekbir", "القصر الكبير", "Ksar el-Kebir", 35.0119, -5.9033));
        cities.put("Taounate", new City(104, "Taounate", "Taounate", "تاونات", "Taounate", 34.5386, -4.6372));
        cities.put("Assila", new City(67, "Assila", "Assila", "أصيلة", "Asilah", 35.4650, -6.0362));
        cities.put("Boulemane", new City(70, "Boulemane", "Boulemane", "بولمان", "Boulemane", 33.3614, -4.7331));
        cities.put("Kalaat Sraghna", new City(94, "Kalaat Sraghna", "Kalaat Sraghna", "قلعة السراغنة", "Kalaat es-Sraghna", 32.0587, -7.4103));
        cities.put("Lagouira", new City(86, "Lagouira", "Lagouira", "الكويرة", "Lagouira", 20.9331, -17.0439));
        cities.put("Moulay Idriss Zerhoun", new City(108, "Moulay Idriss Zerhoun", "Moulay Idriss Zerhoun", "مولاي إدريس زرهون", "Moulay Idriss Zerhoun", 34.0581, -5.5203));
        cities.put("Smara", new City(77, "Smara", "Smara", "السمارة", "Smara", 26.7386, -11.6719));
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
        return cities.get("Tangier"); // Default city
    }
}