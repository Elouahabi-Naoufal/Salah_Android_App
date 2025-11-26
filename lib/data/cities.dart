class City {
  final int id;
  final String key;
  final String en;
  final String ar;
  final String fr;
  final double lat;
  final double lon;

  City({
    required this.id,
    required this.key,
    required this.en,
    required this.ar,
    required this.fr,
    required this.lat,
    required this.lon,
  });
}

class Cities {
  static final Map<String, City> cities = {
    'Tangier': City(id: 101, key: 'Tangier', en: 'Tangier', ar: 'طنجة', fr: 'Tanger', lat: 35.7595, lon: -5.8340),
    'Casablanca': City(id: 71, key: 'Casablanca', en: 'Casablanca', ar: 'الدار البيضاء', fr: 'Casablanca', lat: 33.5731, lon: -7.5898),
    'Rabat': City(id: 95, key: 'Rabat', en: 'Rabat', ar: 'الرباط', fr: 'Rabat', lat: 34.0209, lon: -6.8416),
    'Marrakech': City(id: 88, key: 'Marrakech', en: 'Marrakech', ar: 'مراكش', fr: 'Marrakech', lat: 31.6295, lon: -7.9811),
    'Fes': City(id: 78, key: 'Fes', en: 'Fes', ar: 'فاس', fr: 'Fes', lat: 34.0181, lon: -5.0078),
    'Agadir': City(id: 66, key: 'Agadir', en: 'Agadir', ar: 'أكادير', fr: 'Agadir', lat: 30.4278, lon: -9.5981),
    'Meknes': City(id: 89, key: 'Meknes', en: 'Meknes', ar: 'مكناس', fr: 'Meknes', lat: 33.8935, lon: -5.5473),
    'Oujda': City(id: 93, key: 'Oujda', en: 'Oujda', ar: 'وجدة', fr: 'Oujda', lat: 34.6814, lon: -1.9086),
  };

  static List<City> getAllCities() => cities.values.toList();
  
  static City? getCity(String key) => cities[key];
  
  static List<City> searchCities(String query, String language) {
    return cities.values.where((city) {
      String name = '';
      switch (language) {
        case 'ar': name = city.ar; break;
        case 'fr': name = city.fr; break;
        default: name = city.en; break;
      }
      return name.toLowerCase().contains(query.toLowerCase());
    }).toList();
  }
}