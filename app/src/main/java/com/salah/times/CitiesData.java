package com.salah.times;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CitiesData {
    // key = English name, value = City(id, slug, nameEn, nameAr, nameFr)
    private static final Map<String, City> cities = new LinkedHashMap<>();

    static {
        cities.put("Agadir",                new City(66,  "agadir",                "Agadir",                "أكادير",              "Agadir"));
        cities.put("Al Hoceima",            new City(79,  "al-hoceima",            "Al Hoceima",            "الحسيمة",             "Al Hoceima"));
        cities.put("Assila",                new City(67,  "assila",                "Assila",                "أصيلة",               "Asilah"));
        cities.put("Beni Mellal",           new City(68,  "beni-mellal",           "Beni Mellal",           "بني ملال",            "Beni Mellal"));
        cities.put("Berkane",               new City(69,  "berkane",               "Berkane",               "بركان",               "Berkane"));
        cities.put("Boulemane",             new City(70,  "boulemane",             "Boulemane",             "بولمان",              "Boulemane"));
        cities.put("Casablanca",            new City(71,  "casablanca",            "Casablanca",            "الدار البيضاء",       "Casablanca"));
        cities.put("Chefchaouen",           new City(72,  "chefchaouen",           "Chefchaouen",           "شفشاون",              "Chefchaouen"));
        cities.put("Dakhla",                new City(73,  "dakhla",                "Dakhla",                "الداخلة",             "Dakhla"));
        cities.put("El Jadida",             new City(74,  "el-jadida",             "El Jadida",             "الجديدة",             "El Jadida"));
        cities.put("Errachidia",            new City(75,  "errachidia",            "Errachidia",            "الراشيدية",           "Errachidia"));
        cities.put("Essaouira",             new City(76,  "essaouira",             "Essaouira",             "الصويرة",             "Essaouira"));
        cities.put("Fes",                   new City(78,  "fes",                   "Fes",                   "فاس",                 "Fes"));
        cities.put("Ifrane",                new City(80,  "ifrane",                "Ifrane",                "إفران",               "Ifrane"));
        cities.put("Kalaat Sraghna",        new City(94,  "kalaat-sraghna",        "Kalaat Sraghna",        "قلعة السراغنة",       "Kalaat es-Sraghna"));
        cities.put("Kenitra",               new City(81,  "kenitra",               "Kenitra",               "القنيطرة",            "Kenitra"));
        cities.put("Khenifra",              new City(82,  "khenifra",              "Khenifra",              "خنيفرة",              "Khenifra"));
        cities.put("Khouribga",             new City(83,  "khouribga",             "Khouribga",             "خريبكة",              "Khouribga"));
        cities.put("Ksar Lekbir",           new City(84,  "ksar-lekbir",           "Ksar Lekbir",           "القصر الكبير",        "Ksar el-Kebir"));
        cities.put("Laayoune",              new City(85,  "laayoune",              "Laayoune",              "العيون",              "Laayoune"));
        cities.put("Lagouira",              new City(86,  "lagouira",              "Lagouira",              "الكويرة",             "Lagouira"));
        cities.put("Larache",               new City(87,  "larache",               "Larache",               "العرائش",             "Larache"));
        cities.put("Marrakech",             new City(88,  "marrakech",             "Marrakech",             "مراكش",               "Marrakech"));
        cities.put("Meknes",                new City(89,  "meknes",                "Meknes",                "مكناس",               "Meknes"));
        cities.put("Mohammedia",            new City(90,  "mohammedia",            "Mohammedia",            "المحمدية",            "Mohammedia"));
        cities.put("Moulay Idriss Zerhoun", new City(108, "moulay-idriss-zerhoun", "Moulay Idriss Zerhoun", "مولاي إدريس زرهون",  "Moulay Idriss Zerhoun"));
        cities.put("Nador",                 new City(91,  "nador",                 "Nador",                 "الناظور",             "Nador"));
        cities.put("Ouazzane",              new City(92,  "ouazzane",              "Ouazzane",              "وزان",                "Ouazzane"));
        cities.put("Oujda",                 new City(93,  "oujda",                 "Oujda",                 "وجدة",                "Oujda"));
        cities.put("Rabat",                 new City(95,  "rabat",                 "Rabat",                 "الرباط",              "Rabat"));
        cities.put("Safi",                  new City(96,  "safi",                  "Safi",                  "آسفي",                "Safi"));
        cities.put("Sefrou",                new City(97,  "sefrou",                "Sefrou",                "صفرو",                "Sefrou"));
        cities.put("Settat",                new City(98,  "settat",                "Settat",                "سطات",                "Settat"));
        cities.put("Sidi Kacem",            new City(99,  "sidi-kacem",            "Sidi Kacem",            "سيدي قاسم",           "Sidi Kacem"));
        cities.put("Smara",                 new City(77,  "smara",                 "Smara",                 "السمارة",             "Smara"));
        cities.put("Tan-Tan",               new City(102, "tan-tan",               "Tan-Tan",               "طانطان",              "Tan-Tan"));
        cities.put("Tanger",                new City(101, "tanger",                "Tanger",                "طنجة",                "Tanger"));
        cities.put("Taounate",              new City(104, "taounate",              "Taounate",              "تاونات",              "Taounate"));
        cities.put("Taroudant",             new City(103, "taroudant",             "Taroudant",             "تارودانت",            "Taroudant"));
        cities.put("Taza",                  new City(105, "taza",                  "Taza",                  "تازة",                "Taza"));
        cities.put("Tetouan",               new City(100, "tetouan",               "Tetouan",               "تطوان",               "Tetouan"));
        cities.put("Tiznit",                new City(106, "tiznit",                "Tiznit",                "تزنيت",               "Tiznit"));
        cities.put("Zagora",                new City(107, "zagora",                "Zagora",                "زاكورة",              "Zagora"));
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
            if (city.getName(language).toLowerCase().contains(query.toLowerCase())) {
                results.add(city);
            }
        }
        return results;
    }

    public static City getCityByName(String name) {
        City city = cities.get(name);
        if (city != null) return city;
        // fallback: search by nameEn
        for (City c : cities.values()) {
            if (c.getNameEn().equals(name)) return c;
        }
        return cities.get("Tanger"); // default
    }
}
