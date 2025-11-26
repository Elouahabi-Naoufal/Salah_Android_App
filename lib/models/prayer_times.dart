class PrayerTimes {
  final String date;
  final String fajr;
  final String sunrise;
  final String dhuhr;
  final String asr;
  final String maghrib;
  final String isha;
  final String? chorok; // Calculated sunrise

  PrayerTimes({
    required this.date,
    required this.fajr,
    required this.sunrise,
    required this.dhuhr,
    required this.asr,
    required this.maghrib,
    required this.isha,
    this.chorok,
  });

  factory PrayerTimes.fromJson(Map<String, dynamic> json) {
    return PrayerTimes(
      date: json['Date'] ?? '',
      fajr: json['Fajr'] ?? '',
      sunrise: json['Sunrise'] ?? json['Chorok'] ?? '',
      dhuhr: json['Dohr'] ?? json['Dhuhr'] ?? '',
      asr: json['Asr'] ?? '',
      maghrib: json['Maghreb'] ?? json['Maghrib'] ?? '',
      isha: json['Isha'] ?? '',
      chorok: json['Chorok'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'Date': date,
      'Fajr': fajr,
      'Sunrise': sunrise,
      'Dohr': dhuhr,
      'Asr': asr,
      'Maghreb': maghrib,
      'Isha': isha,
      if (chorok != null) 'Chorok': chorok,
    };
  }

  List<Prayer> getPrayerList() {
    return [
      Prayer('Fajr', fajr, '☽'),
      Prayer('Chorok', chorok ?? sunrise, '☀'),
      Prayer('Dohr', dhuhr, '☉'),
      Prayer('Asr', asr, '☀'),
      Prayer('Maghreb', maghrib, '☾'),
      Prayer('Isha', isha, '★'),
    ];
  }
}

class Prayer {
  final String name;
  final String time;
  final String icon;

  Prayer(this.name, this.time, this.icon);
}