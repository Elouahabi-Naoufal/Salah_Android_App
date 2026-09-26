package com.salah.times;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Auto-generated from https://www.yabiladi.com/prieres/ (2026-09-25).
 * 38 pays, 419 villes. Slugs/IDs match yabiladi detail URLs:
 * https://www.yabiladi.com/prieres/details/{id}/{slug}.html
 */
public class CitiesData {
    // key = French ville name (unique site-wide), value = City(id, slug, en, ar, fr, country)
    private static final Map<String, City> cities = new LinkedHashMap<>();

    static {
        // ── Maroc (43 villes) ──
        cities.put("Agadir", new City(66, "agadir", "Agadir", "أكادير", "Agadir", "Agadir", "Maroc"));
        cities.put("Al Hoceima", new City(79, "al-hoceima", "Al Hoceima", "الحسيمة", "Al Hoceima", "Alhucemas", "Maroc"));
        cities.put("Assila", new City(67, "assila", "Assila", "أصيلة", "Assila", "Arcila", "Maroc"));
        cities.put("Beni Mellal", new City(68, "beni-mellal", "Beni Mellal", "بني ملال", "Beni Mellal", "Beni Melal", "Maroc"));
        cities.put("Berkane", new City(69, "berkane", "Berkane", "بركان", "Berkane", "Berkane", "Maroc"));
        cities.put("Boulemane", new City(70, "boulemane", "Boulemane", "بولمان", "Boulemane", "Bulman", "Maroc"));
        cities.put("Casablanca", new City(71, "casablanca", "Casablanca", "الدار البيضاء", "Casablanca", "Casablanca", "Maroc"));
        cities.put("Chefchaouen", new City(72, "chefchaouen", "Chefchaouen", "شفشاون", "Chefchaouen", "Chauen", "Maroc"));
        cities.put("Dakhla", new City(73, "dakhla", "Dakhla", "الداخلة", "Dakhla", "Dajla", "Maroc"));
        cities.put("El Jadida", new City(74, "el-jadida", "El Jadida", "الجديدة", "El Jadida", "El Yadida", "Maroc"));
        cities.put("Errachidia", new City(75, "errachidia", "Errachidia", "الراشيدية", "Errachidia", "Errachidía", "Maroc"));
        cities.put("Essaouira", new City(76, "essaouira", "Essaouira", "الصويرة", "Essaouira", "Esauira", "Maroc"));
        cities.put("Fès", new City(78, "fes", "Fès", "فاس", "Fès", "Fez", "Maroc"));
        cities.put("Ifrane", new City(80, "ifrane", "Ifrane", "إفران", "Ifrane", "Ifrán", "Maroc"));
        cities.put("Kalâat Sraghna", new City(94, "kalaat-sraghna", "Kalâat Sraghna", "قلعة السراغنة", "Kalâat Sraghna", "El Kelaa", "Maroc"));
        cities.put("Khenifra", new City(82, "khenifra", "Khenifra", "خنيفرة", "Khenifra", "Jenifra", "Maroc"));
        cities.put("Khouribga", new City(83, "khouribga", "Khouribga", "خريبكة", "Khouribga", "Juribga", "Maroc"));
        cities.put("Ksar Lekbir", new City(84, "ksar-lekbir", "Ksar Lekbir", "القصر الكبير", "Ksar Lekbir", "Alcazarquivir", "Maroc"));
        cities.put("Kénitra", new City(81, "kenitra", "Kénitra", "القنيطرة", "Kénitra", "Kenitra", "Maroc"));
        cities.put("Lagouira", new City(86, "lagouira", "Lagouira", "الكويرة", "Lagouira", "La Güera", "Maroc"));
        cities.put("Larache", new City(87, "larache", "Larache", "العرائش", "Larache", "Larache", "Maroc"));
        cities.put("Laâyoune", new City(85, "laayoune", "Laâyoune", "العيون", "Laâyoune", "El Aaiún", "Maroc"));
        cities.put("Marrakech", new City(88, "marrakech", "Marrakech", "مراكش", "Marrakech", "Marrakech", "Maroc"));
        cities.put("Meknès", new City(89, "meknes", "Meknès", "مكناس", "Meknès", "Mequínez", "Maroc"));
        cities.put("Mohammédia", new City(90, "mohammedia", "Mohammédia", "المحمدية", "Mohammédia", "Mohammedia", "Maroc"));
        cities.put("Moulay Idriss Zerhoun", new City(108, "moulay-idriss-zerhoun", "Moulay Idriss Zerhoun", "مولاي إدريس زرهون", "Moulay Idriss Zerhoun", "Mulay Idrís Zerhún", "Maroc"));
        cities.put("Nador", new City(91, "nador", "Nador", "الناظور", "Nador", "Nador", "Maroc"));
        cities.put("Ouazzane", new City(92, "ouazzane", "Ouazzane", "وزان", "Ouazzane", "Uezán", "Maroc"));
        cities.put("Oujda", new City(93, "oujda", "Oujda", "وجدة", "Oujda", "Oujda", "Maroc"));
        cities.put("Rabat", new City(95, "rabat", "Rabat", "الرباط", "Rabat", "Rabat", "Maroc"));
        cities.put("Safi", new City(96, "safi", "Safi", "آسفي", "Safi", "Safí", "Maroc"));
        cities.put("Sefrou", new City(97, "sefrou", "Sefrou", "صفرو", "Sefrou", "Sefrú", "Maroc"));
        cities.put("Settat", new City(98, "settat", "Settat", "سطات", "Settat", "Settat", "Maroc"));
        cities.put("Sidi Kacem", new City(99, "sidi-kacem", "Sidi Kacem", "سيدي قاسم", "Sidi Kacem", "Sidi Kasem", "Maroc"));
        cities.put("Smara", new City(77, "smara", "Smara", "السمارة", "Smara", "Esmara", "Maroc"));
        cities.put("Tan-Tan", new City(102, "tan-tan", "Tan-Tan", "طانطان", "Tan-Tan", "Tan-Tan", "Maroc"));
        cities.put("Tanger", new City(101, "tanger", "Tanger", "طنجة", "Tanger", "Tánger", "Maroc"));
        cities.put("Taounate", new City(104, "taounate", "Taounate", "تاونات", "Taounate", "Taounate", "Maroc"));
        cities.put("Taroudant", new City(103, "taroudant", "Taroudant", "تارودانت", "Taroudant", "Tarudant", "Maroc"));
        cities.put("Taza", new City(105, "taza", "Taza", "تازة", "Taza", "Taza", "Maroc"));
        cities.put("Tiznit", new City(106, "tiznit", "Tiznit", "تزنيت", "Tiznit", "Tiznit", "Maroc"));
        cities.put("Tétouan", new City(100, "tetouan", "Tétouan", "تطوان", "Tétouan", "Tetuán", "Maroc"));
        cities.put("Zagora", new City(107, "zagora", "Zagora", "زاكورة", "Zagora", "Zagora", "Maroc"));

        // ── Afrique du sud (6 villes) ──
        cities.put("Boksburg", new City(370, "boksburg", "Boksburg", "بوكسبرغ", "Boksburg", "Boksburg", "Afrique du sud"));
        cities.put("Durban", new City(371, "durban", "Durban", "ديربان", "Durban", "Durban", "Afrique du sud"));
        cities.put("Germiston", new City(369, "germiston", "Germiston", "جيرميستون", "Germiston", "Germiston", "Afrique du sud"));
        cities.put("Johannesburg", new City(374, "johannesburg", "Johannesburg", "جوهانسبرغ", "Johannesburg", "Johannesburg", "Afrique du sud"));
        cities.put("Le Cap", new City(372, "le-cap", "Cape Town", "كيب تاون", "Le Cap", "Ciudad del Cabo", "Afrique du sud"));
        cities.put("Pretoria", new City(373, "pretoria", "Pretoria", "بريتوريا", "Pretoria", "Pretoria", "Afrique du sud"));

        // ── Algérie (30 villes) ──
        cities.put("Alger", new City(2, "alger", "Algiers", "الجزائر", "Alger", "Argel", "Algérie"));
        cities.put("Annaba", new City(280, "annaba", "Annaba", "عنابة", "Annaba", "Annaba", "Algérie"));
        cities.put("Batna", new City(277, "batna", "Batna", "باتنة", "Batna", "Batna", "Algérie"));
        cities.put("Bir El Djir", new City(295, "bir-el-djir", "Bir El Djir", "بير الجير", "Bir El Djir", "Bir El Djir", "Algérie"));
        cities.put("Biskra", new City(282, "biskra", "Biskra", "بسكرة", "Biskra", "Biskra", "Algérie"));
        cities.put("Blida", new City(291, "blida", "Blida", "البليدة", "Blida", "Blida", "Algérie"));
        cities.put("Bordj Bou Arreridj", new City(288, "bordj-bou-arreridj", "Bordj Bou Arreridj", "برج بوعريريج", "Bordj Bou Arreridj", "Bordj Bou Arreridj", "Algérie"));
        cities.put("Béchar", new City(289, "bechar", "Bechar", "بشار", "Béchar", "Béchar", "Algérie"));
        cities.put("Béjaïa", new City(287, "bejaia", "Bejaia", "بجاية", "Béjaïa", "Béjaïa", "Algérie"));
        cities.put("Chlef", new City(286, "chlef", "Chlef", "الشلف", "Chlef", "Chlef", "Algérie"));
        cities.put("Constantine", new City(276, "constantine", "Constantine", "قسنطينة", "Constantine", "Constantine", "Algérie"));
        cities.put("Djelfa", new City(278, "djelfa", "Djelfa", "الجلفة", "Djelfa", "Djelfa", "Algérie"));
        cities.put("El Eulma", new City(294, "el-eulma", "El Eulma", "العلمة", "El Eulma", "El Eulma", "Algérie"));
        cities.put("El Khroub", new City(285, "el-khroub", "El Khroub", "الخروب", "El Khroub", "El Khroub", "Algérie"));
        cities.put("El Oued", new City(302, "el-oued", "El Oued", "الوادي", "El Oued", "El Oued", "Algérie"));
        cities.put("Jijel", new City(301, "jijel", "Jijel", "جيجل", "Jijel", "Jijel", "Algérie"));
        cities.put("Laghouat", new City(297, "laghouat", "Laghouat", "الأغواط", "Laghouat", "Laghouat", "Algérie"));
        cities.put("M'Sila", new City(292, "m-sila", "M'Sila", "المسيلة", "M'Sila", "M'Sila", "Algérie"));
        cities.put("Mostaganem", new City(296, "mostaganem", "Mostaganem", "مستغانم", "Mostaganem", "Mostaganem", "Algérie"));
        cities.put("Médéa", new City(299, "medea", "Medea", "المدية", "Médéa", "Médéa", "Algérie"));
        cities.put("Oran", new City(275, "oran", "Oran", "وهران", "Oran", "Oran", "Algérie"));
        cities.put("Ouargla", new City(303, "ouargla", "Ouargla", "ورقلة", "Ouargla", "Ouargla", "Algérie"));
        cities.put("Sidi bel Abbès", new City(281, "sidi-bel-abbes", "Sidi Bel Abbes", "سيدي بلعباس", "Sidi bel Abbès", "Sidi bel Abbès", "Algérie"));
        cities.put("Skikda", new City(290, "skikda", "Skikda", "سكيكدة", "Skikda", "Skikda", "Algérie"));
        cities.put("Souk Ahras", new City(293, "souk-ahras", "Souk Ahras", "سوق أهراس", "Souk Ahras", "Souk Ahras", "Algérie"));
        cities.put("Sétif", new City(279, "setif", "Setif", "سطيف", "Sétif", "Sétif", "Algérie"));
        cities.put("Tiaret", new City(283, "tiaret", "Tiaret", "تيارت", "Tiaret", "Tiaret", "Algérie"));
        cities.put("Tizi Ouzou", new City(300, "tizi-ouzou", "Tizi Ouzou", "تيزي وزو", "Tizi Ouzou", "Tizi Ouzou", "Algérie"));
        cities.put("Tlemcen", new City(298, "tlemcen", "Tlemcen", "تلمسان", "Tlemcen", "Tlemcen", "Algérie"));
        cities.put("Tébessa", new City(284, "tebessa", "Tebessa", "تبسة", "Tébessa", "Tébessa", "Algérie"));

        // ── Allemagne (20 villes) ──
        cities.put("Berlin", new City(35, "berlin", "Berlin", "برلين", "Berlin", "Berlin", "Allemagne"));
        cities.put("Bielefeld", new City(273, "bielefeld", "Bielefeld", "بيليفيلد", "Bielefeld", "Bielefeld", "Allemagne"));
        cities.put("Bochum", new City(271, "bochum", "Bochum", "بوخوم", "Bochum", "Bochum", "Allemagne"));
        cities.put("Bonn", new City(38, "bonn", "Bonn", "بون", "Bonn", "Bonn", "Allemagne"));
        cities.put("Brême", new City(265, "breme", "Bremen", "بريمن", "Brême", "Bremen", "Allemagne"));
        cities.put("Cologne", new City(40, "cologne", "Cologne", "كولونيا", "Cologne", "Colonia", "Allemagne"));
        cities.put("Dortmund", new City(263, "dortmund", "Dortmund", "دورتموند", "Dortmund", "Dortmund", "Allemagne"));
        cities.put("Dresde", new City(266, "dresde", "Dresden", "دريسدن", "Dresde", "Dresde", "Allemagne"));
        cities.put("Duisbourg", new City(270, "duisbourg", "Duisburg", "دويسبورغ", "Duisbourg", "Duisbourg", "Allemagne"));
        cities.put("Düsseldorf", new City(41, "dusseldorf", "Dusseldorf", "دوسلدورف", "Düsseldorf", "Düsseldorf", "Allemagne"));
        cities.put("Essen", new City(264, "essen", "Essen", "إيسن", "Essen", "Essen", "Allemagne"));
        cities.put("Francfort", new City(39, "francfort", "Frankfurt", "فرانكفورت", "Francfort", "Fráncfort", "Allemagne"));
        cities.put("Hambourg", new City(37, "hambourg", "Hamburg", "هامبورغ", "Hambourg", "Hambourg", "Allemagne"));
        cities.put("Hanovre", new City(268, "hanovre", "Hanover", "هانوفر", "Hanovre", "Hannover", "Allemagne"));
        cities.put("Leipzig", new City(267, "leipzig", "Leipzig", "لايبزيغ", "Leipzig", "Leipzig", "Allemagne"));
        cities.put("Mannheim", new City(274, "mannheim", "Mannheim", "مانهايم", "Mannheim", "Mannheim", "Allemagne"));
        cities.put("Munich", new City(36, "munich", "Munich", "ميونخ", "Munich", "Múnich", "Allemagne"));
        cities.put("Nuremberg", new City(269, "nuremberg", "Nuremberg", "نورنبرغ", "Nuremberg", "Núremberg", "Allemagne"));
        cities.put("Stuttgart", new City(262, "stuttgart", "Stuttgart", "شتوتغارت", "Stuttgart", "Stuttgart", "Allemagne"));
        cities.put("Wuppertal", new City(272, "wuppertal", "Wuppertal", "فوبرتال", "Wuppertal", "Wuppertal", "Allemagne"));

        // ── Arabie-Saoudite (4 villes) ──
        cities.put("Djeddah", new City(328, "djeddah", "Jeddah", "جدة", "Djeddah", "Yeda", "Arabie-Saoudite"));
        cities.put("La Mecque", new City(13, "la-mecque", "Mecca", "مكة المكرمة", "La Mecque", "La Meca", "Arabie-Saoudite"));
        cities.put("Médine", new City(12, "medine", "Medina", "المدينة المنورة", "Médine", "Medina", "Arabie-Saoudite"));
        cities.put("Riyad", new City(327, "riyad", "Riyadh", "الرياض", "Riyad", "Riad", "Arabie-Saoudite"));

        // ── Australie (10 villes) ──
        cities.put("Adélaïde", new City(330, "adelaide", "Adelaide", "أديلايد", "Adélaïde", "Adelaida", "Australie"));
        cities.put("Brisbane", new City(331, "brisbane", "Brisbane", "بريسبان", "Brisbane", "Brisbane", "Australie"));
        cities.put("Canberra", new City(334, "canberra", "Canberra", "كانبرا", "Canberra", "Canberra", "Australie"));
        cities.put("Geelong", new City(333, "geelong", "Geelong", "جيلونغ", "Geelong", "Geelong", "Australie"));
        cities.put("Hobart", new City(332, "hobart", "Hobart", "هوبارت", "Hobart", "Hobart", "Australie"));
        cities.put("Melbourne", new City(337, "melbourne", "Melbourne", "ملبورن", "Melbourne", "Melbourne", "Australie"));
        cities.put("Newcastle", new City(335, "newcastle", "Newcastle", "نيوكاسل", "Newcastle", "Newcastle", "Australie"));
        cities.put("Perth", new City(336, "perth", "Perth", "بيرث", "Perth", "Perth", "Australie"));
        cities.put("Sydney", new City(338, "sydney", "Sydney", "سيدني", "Sydney", "Sídney", "Australie"));
        cities.put("Wollongong", new City(329, "wollongong", "Wollongong", "ولونغونغ", "Wollongong", "Wollongong", "Australie"));

        // ── Autriche (3 villes) ──
        cities.put("Linz", new City(339, "linz", "Linz", "لينتس", "Linz", "Linz", "Autriche"));
        cities.put("Vienne", new City(340, "vienne", "Vienna", "فيينا", "Vienne", "Vienne", "Autriche"));
        cities.put("Villach", new City(341, "villach", "Villach", "فيلاخ", "Villach", "Villach", "Autriche"));

        // ── Belgique (11 villes) ──
        cities.put("Anvers", new City(56, "anvers", "Antwerp", "أنتويرب", "Anvers", "Amberes", "Belgique"));
        cities.put("Bruxelles", new City(55, "bruxelles", "Brussels", "بروكسل", "Bruxelles", "Bruselas", "Belgique"));
        cities.put("Charleroi", new City(57, "charleroi", "Charleroi", "شارلروا", "Charleroi", "Charleroi", "Belgique"));
        cities.put("Gand", new City(63, "gand", "Ghent", "غنت", "Gand", "Gante", "Belgique"));
        cities.put("Hasselt", new City(58, "hasselt", "Hasselt", "هاسلت", "Hasselt", "Hasselt", "Belgique"));
        cities.put("Liège", new City(61, "liege", "Liège", "لييج", "Liège", "Lieja", "Belgique"));
        cities.put("Mons", new City(64, "mons", "Mons", "مونس", "Mons", "Mons", "Belgique"));
        cities.put("Namur", new City(59, "namur", "Namur", "نامور", "Namur", "Namur", "Belgique"));
        cities.put("Renaix", new City(65, "renaix", "Renaix", "رونسه", "Renaix", "Renaix", "Belgique"));
        cities.put("Tournai", new City(62, "tournai", "Tournai", "تورناي", "Tournai", "Tournai", "Belgique"));
        cities.put("Verviers", new City(60, "verviers", "Verviers", "فيرفييه", "Verviers", "Verviers", "Belgique"));

        // ── Cameroun (1 villes) ──
        cities.put("Yaoundé", new City(44, "yaounde", "Yaounde", "ياوندي", "Yaoundé", "Yaundé", "Cameroun"));

        // ── Canada (5 villes) ──
        cities.put("Montréal", new City(51, "montreal", "Montreal", "مونتريال", "Montréal", "Montreal", "Canada"));
        cities.put("Ottawa", new City(52, "ottawa", "Ottawa", "أوتاوا", "Ottawa", "Ottawa", "Canada"));
        cities.put("Québec", new City(54, "quebec", "Quebec", "كيبيك", "Québec", "Quebec", "Canada"));
        cities.put("Toronto", new City(53, "toronto", "Toronto", "تورونتو", "Toronto", "Toronto", "Canada"));
        cities.put("Vancouver", new City(260, "vancouver", "Vancouver", "فانكوفر", "Vancouver", "Vancouver", "Canada"));

        // ── Côte d'ivoire (1 villes) ──
        cities.put("Abidjan", new City(43, "abidjan", "Abidjan", "أبيدجان", "Abidjan", "Abiyán", "Côte d'ivoire"));

        // ── Danemark (1 villes) ──
        cities.put("Copenhague", new City(342, "copenhague", "Copenhagen", "كوبنهاغن", "Copenhague", "Copenhague", "Danemark"));

        // ── Egypte (1 villes) ──
        cities.put("Le Caire", new City(42, "le-caire", "Cairo", "القاهرة", "Le Caire", "El Cairo", "Egypte"));

        // ── Emirats arabes unis (2 villes) ──
        cities.put("Abou Dhabi", new City(9, "abou-dhabi", "Abu Dhabi", "أبوظبي", "Abou Dhabi", "Abu Dabi", "Emirats arabes unis"));
        cities.put("Dubaï", new City(8, "dubai", "Dubai", "دبي", "Dubaï", "Dubái", "Emirats arabes unis"));

        // ── Espagne (54 villes) ──
        cities.put("Albacete", new City(240, "albacete", "Albacete", "البسيط", "Albacete", "Albacete", "Espagne"));
        cities.put("Alcalá de Henares", new City(230, "alcala-de-henares", "Alcala de Henares", "ألكالا دي إيناريس", "Alcalá de Henares", "Alcalá de Henares", "Espagne"));
        cities.put("Alcorcón", new City(239, "alcorcon", "Alcorcon", "ألكوركون", "Alcorcón", "Alcorcón", "Espagne"));
        cities.put("Algeciras", new City(199, "algeciras", "Algeciras", "الجزيرة الخضراء", "Algeciras", "Algeciras", "Espagne"));
        cities.put("Alicante", new City(210, "alicante", "Alicante", "أليكانتي", "Alicante", "Alicante", "Espagne"));
        cities.put("Almería", new City(233, "almeria", "Almeria", "ألميرية", "Almería", "Almería", "Espagne"));
        cities.put("Badajoz", new City(246, "badajoz", "Badajoz", "بطليوس", "Badajoz", "Badajoz", "Espagne"));
        cities.put("Badalona", new City(224, "badalona", "Badalona", "بادالونا", "Badalona", "Badalona", "Espagne"));
        cities.put("Barcelona", new City(202, "barcelona", "Barcelona", "برشلونة", "Barcelona", "Barcelona", "Espagne"));
        cities.put("Bilbao", new City(206, "bilbao", "Bilbao", "بلباو", "Bilbao", "Bilbao", "Espagne"));
        cities.put("Burgos", new City(238, "burgos", "Burgos", "برغش", "Burgos", "Burgos", "Espagne"));
        cities.put("Cartagena", new City(225, "cartagena", "Cartagena", "قرطاجنة", "Cartagena", "Cartagena", "Espagne"));
        cities.put("Castellón de la Plana", new City(237, "castellon-de-la-plana", "Castellon de la Plana", "كاستيون دي لا بلانا", "Castellón de la Plana", "Castellón de la Plana", "Espagne"));
        cities.put("Cádiz", new City(197, "cadiz", "Cadiz", "قادس", "Cádiz", "Cádiz", "Espagne"));
        cities.put("Córdoba", new City(211, "cordoba", "Cordoba", "قرطبة", "Córdoba", "Córdoba", "Espagne"));
        cities.put("Elche", new City(221, "elche", "Elche", "إلتشي", "Elche", "Elche", "Espagne"));
        cities.put("Fuenlabrada", new City(232, "fuenlabrada", "Fuenlabrada", "فوينلابرادا", "Fuenlabrada", "Fuenlabrada", "Espagne"));
        cities.put("Getafe", new City(241, "getafe", "Getafe", "خيتافي", "Getafe", "Getafe", "Espagne"));
        cities.put("Gijón", new City(216, "gijon", "Gijon", "خيخون", "Gijón", "Gijón", "Espagne"));
        cities.put("Granada", new City(219, "granada", "Granada", "غرناطة", "Granada", "Granada", "Espagne"));
        cities.put("Huelva", new City(245, "huelva", "Huelva", "ولبة", "Huelva", "Huelva", "Espagne"));
        cities.put("Jaén", new City(198, "jaen", "Jaen", "خاين", "Jaén", "Jaén", "Espagne"));
        cities.put("Jerez de la Frontera", new City(228, "jerez-de-la-frontera", "Jerez de la Frontera", "شريش", "Jerez de la Frontera", "Jerez de la Frontera", "Espagne"));
        cities.put("L'hospitalet De Llobregat", new City(217, "l-hospitalet-de-llobregat", "L'Hospitalet de Llobregat", "هوسبيتالت دي يوبريغات", "L'hospitalet De Llobregat", "L'hospitalet De Llobregat", "Espagne"));
        cities.put("La Coruña", new City(218, "la-coruna", "A Coruña", "لا كورونيا", "La Coruña", "La Coruña", "Espagne"));
        cities.put("Las Palmas De Gran Canaria", new City(213, "las-palmas-de-gran-canaria", "Las Palmas de Gran Canaria", "لاس بالماس دي غران كناريا", "Las Palmas De Gran Canaria", "Las Palmas De Gran Canaria", "Espagne"));
        cities.put("Leganés", new City(235, "leganes", "Leganes", "ليغانيس", "Leganés", "Leganés", "Espagne"));
        cities.put("León", new City(248, "leon", "Leon", "ليون", "León", "León", "Espagne"));
        cities.put("Logroño", new City(243, "logrono", "Logrono", "لوغرونيو", "Logroño", "Logroño", "Espagne"));
        cities.put("Lérida", new City(195, "lerida", "Lleida", "لاردة", "Lérida", "Lérida", "Espagne"));
        cities.put("Madrid", new City(203, "madrid", "Madrid", "مدريد", "Madrid", "Madrid", "Espagne"));
        cities.put("Marbella", new City(196, "marbella", "Marbella", "مربلة", "Marbella", "Marbella", "Espagne"));
        cities.put("Murcia", new City(209, "murcia", "Murcia", "مرسية", "Murcia", "Murcia", "Espagne"));
        cities.put("Málaga", new City(207, "malaga", "Malaga", "مالقة", "Málaga", "Málaga", "Espagne"));
        cities.put("Móstoles", new City(226, "mostoles", "Mostoles", "موستوليس", "Móstoles", "Móstoles", "Espagne"));
        cities.put("Oviedo", new City(223, "oviedo", "Oviedo", "أوفييدو", "Oviedo", "Oviedo", "Espagne"));
        cities.put("Palma de Mallorca", new City(212, "palma-de-mallorca", "Palma de Mallorca", "بالما دي مايوركا", "Palma de Mallorca", "Palma de Mallorca", "Espagne"));
        cities.put("Pamplona", new City(231, "pamplona", "Pamplona", "بنبلونة", "Pamplona", "Pamplona", "Espagne"));
        cities.put("Reus", new City(201, "reus", "Reus", "ريوس", "Reus", "Reus", "Espagne"));
        cities.put("Sabadell", new City(229, "sabadell", "Sabadell", "ساباديل", "Sabadell", "Sabadell", "Espagne"));
        cities.put("Salamanca", new City(242, "salamanca", "Salamanca", "سلمنكا", "Salamanca", "Salamanca", "Espagne"));
        cities.put("San Cristóbal de La Laguna", new City(244, "san-cristobal-de-la-laguna", "San Cristóbal de La Laguna", "سان كريستوبال دي لا لاغونا", "San Cristóbal de La Laguna", "San Cristóbal de La Laguna", "Espagne"));
        cities.put("San Sebastián", new City(234, "san-sebastian", "San Sebastian", "سان سيباستيان", "San Sebastián", "San Sebastián", "Espagne"));
        cities.put("Santa Cruz De Tenerife", new City(222, "santa-cruz-de-tenerife", "Santa Cruz de Tenerife", "سانتا كروز دي تينيريفي", "Santa Cruz De Tenerife", "Santa Cruz De Tenerife", "Espagne"));
        cities.put("Santander", new City(236, "santander", "Santander", "سانتاندير", "Santander", "Santander", "Espagne"));
        cities.put("Sevilla", new City(205, "sevilla", "Seville", "إشبيلية", "Sevilla", "Sevilla", "Espagne"));
        cities.put("Tarifa", new City(200, "tarifa", "Tarifa", "طريفة", "Tarifa", "Tarifa", "Espagne"));
        cities.put("Tarragona", new City(247, "tarragona", "Tarragona", "طركونة", "Tarragona", "Tarragona", "Espagne"));
        cities.put("Terrassa", new City(227, "terrassa", "Terrassa", "تاراسا", "Terrassa", "Terrassa", "Espagne"));
        cities.put("Valencia", new City(204, "valencia", "Valencia", "بلنسية", "Valencia", "Valencia", "Espagne"));
        cities.put("Valladolid", new City(214, "valladolid", "Valladolid", "بلد الوليد", "Valladolid", "Valladolid", "Espagne"));
        cities.put("Vigo", new City(215, "vigo", "Vigo", "فيغو", "Vigo", "Vigo", "Espagne"));
        cities.put("Vitoria", new City(220, "vitoria", "Vitoria", "فيتوريا", "Vitoria", "Vitoria", "Espagne"));
        cities.put("Zaragoza", new City(208, "zaragoza", "Zaragoza", "سرقسطة", "Zaragoza", "Zaragoza", "Espagne"));

        // ── Etats-Unis (13 villes) ──
        cities.put("Anchorage", new City(376, "anchorage", "Anchorage", "أنكوريج", "Anchorage", "Anchorage", "Etats-Unis"));
        cities.put("Atlanta", new City(380, "atlanta", "Atlanta", "أتلانتا", "Atlanta", "Atlanta", "Etats-Unis"));
        cities.put("Chicago", new City(48, "chicago", "Chicago", "شيكاغو", "Chicago", "Chicago", "Etats-Unis"));
        cities.put("Dallas", new City(379, "dallas", "Dallas", "دالاس", "Dallas", "Dallas", "Etats-Unis"));
        cities.put("Denver", new City(378, "denver", "Denver", "دنفر", "Denver", "Denver", "Etats-Unis"));
        cities.put("Détroit", new City(375, "detroit", "Detroit", "ديترويت", "Détroit", "Détroit", "Etats-Unis"));
        cities.put("Honolulu", new City(381, "honolulu", "Honolulu", "هونولولو", "Honolulu", "Honolulu", "Etats-Unis"));
        cities.put("Houston", new City(377, "houston", "Houston", "هيوستن", "Houston", "Houston", "Etats-Unis"));
        cities.put("Los Angeles", new City(49, "los-angeles", "Los Angeles", "لوس أنجلوس", "Los Angeles", "Los Angeles", "Etats-Unis"));
        cities.put("Miami", new City(47, "miami", "Miami", "ميامي", "Miami", "Miami", "Etats-Unis"));
        cities.put("New York", new City(45, "new-york", "New York", "نيويورك", "New York", "Nueva York", "Etats-Unis"));
        cities.put("Seattle", new City(50, "seattle", "Seattle", "سياتل", "Seattle", "Seattle", "Etats-Unis"));
        cities.put("Washington", new City(46, "washington", "Washington", "واشنطن", "Washington", "Washington", "Etats-Unis"));

        // ── Finlande (4 villes) ──
        cities.put("Espoo", new City(344, "espoo", "Espoo", "إسبو", "Espoo", "Espoo", "Finlande"));
        cities.put("Helsinki", new City(343, "helsinki", "Helsinki", "هلسنكي", "Helsinki", "Helsinki", "Finlande"));
        cities.put("Turku", new City(345, "turku", "Turku", "توركو", "Turku", "Turku", "Finlande"));
        cities.put("Vantaa", new City(346, "vantaa", "Vantaa", "فانتا", "Vantaa", "Vantaa", "Finlande"));

        // ── France (86 villes) ──
        cities.put("Albi", new City(141, "albi", "Albi", "ألبي", "Albi", "Albi", "France"));
        cities.put("Amiens", new City(148, "amiens", "Amiens", "أميان", "Amiens", "Amiens", "France"));
        cities.put("Angers", new City(146, "angers", "Angers", "أنجيه", "Angers", "Angers", "France"));
        cities.put("Angoulême", new City(181, "angouleme", "Angoulême", "أنغوليم", "Angoulême", "Angoulême", "France"));
        cities.put("Annecy", new City(186, "annecy", "Annecy", "آنسي", "Annecy", "Annecy", "France"));
        cities.put("Arras", new City(165, "arras", "Arras", "أراس", "Arras", "Arras", "France"));
        cities.put("Auxerre", new City(119, "auxerre", "Auxerre", "أوكسير", "Auxerre", "Auxerre", "France"));
        cities.put("Avignon", new City(191, "avignon", "Avignon", "أفينيون", "Avignon", "Avignon", "France"));
        cities.put("Bar-le-Duc", new City(134, "bar-le-duc", "Bar-le-Duc", "بار لو دوك", "Bar-le-Duc", "Bar-le-Duc", "France"));
        cities.put("Bayeux", new City(132, "bayeux", "Bayeux", "بايو", "Bayeux", "Bayeux", "France"));
        cities.put("Beauvais", new City(129, "beauvais", "Beauvais", "بوفيه", "Beauvais", "Beauvais", "France"));
        cities.put("Belfort", new City(111, "belfort", "Belfort", "بلفور", "Belfort", "Belfort", "France"));
        cities.put("Besançon", new City(149, "besancon", "Besançon", "بيزانسون", "Besançon", "Besançon", "France"));
        cities.put("Bordeaux", new City(150, "bordeaux", "Bordeaux", "بوردو", "Bordeaux", "Burdeos", "France"));
        cities.put("Bourges", new City(147, "bourges", "Bourges", "بورج", "Bourges", "Bourges", "France"));
        cities.put("Caen", new City(131, "caen", "Caen", "كان", "Caen", "Caen", "France"));
        cities.put("Carcassonne", new City(143, "carcassonne", "Carcassonne", "قرقشونة", "Carcassonne", "Carcassonne", "France"));
        cities.put("Chalon sur Marne", new City(122, "chalon-sur-marne", "Chalon sur Marne", "شالون سور مارن", "Chalon sur Marne", "Chalon sur Marne", "France"));
        cities.put("Chalon sur Saône", new City(185, "chalon-sur-saone", "Chalon sur Saône", "شالون سور سون", "Chalon sur Saône", "Chalon sur Saône", "France"));
        cities.put("Chambéry", new City(187, "chambery", "Chambéry", "شامبيري", "Chambéry", "Chambéry", "France"));
        cities.put("Charleville-Mézières", new City(170, "charleville-mezieres", "Charleville-Mézières", "شارلفيل ميزيير", "Charleville-Mézières", "Charleville-Mézières", "France"));
        cities.put("Chartres", new City(124, "chartres", "Chartres", "شارتر", "Chartres", "Chartres", "France"));
        cities.put("Chaumont", new City(115, "chaumont", "Chaumont", "شومون", "Chaumont", "Chaumont", "France"));
        cities.put("Cherbourg", new City(169, "cherbourg", "Cherbourg", "شيربورغ", "Cherbourg", "Cherbourg", "France"));
        cities.put("Cholet", new City(145, "cholet", "Cholet", "شوليه", "Cholet", "Cholet", "France"));
        cities.put("Château-Chinon", new City(174, "chateau-chinon", "Château-Chinon", "شاتو شينون", "Château-Chinon", "Château-Chinon", "France"));
        cities.put("Châtellerault", new City(127, "chatellerault", "Châtellerault", "شاتيلرو", "Châtellerault", "Châtellerault", "France"));
        cities.put("Clermont-Ferrand", new City(151, "clermont-ferrand", "Clermont-Ferrand", "كليرمون فيران", "Clermont-Ferrand", "Clermont-Ferrand", "France"));
        cities.put("Dieppe", new City(168, "dieppe", "Dieppe", "دييب", "Dieppe", "Dieppe", "France"));
        cities.put("Dijon", new City(172, "dijon", "Dijon", "ديجون", "Dijon", "Dijon", "France"));
        cities.put("Douai", new City(166, "douai", "Douai", "دويه", "Douai", "Douai", "France"));
        cities.put("Draguignan", new City(189, "draguignan", "Draguignan", "دراغينيان", "Draguignan", "Draguignan", "France"));
        cities.put("Epinal", new City(113, "epinal", "Epinal", "إبينال", "Epinal", "Epinal", "France"));
        cities.put("Foix", new City(138, "foix", "Foix", "فوا", "Foix", "Foix", "France"));
        cities.put("Forbach", new City(110, "forbach", "Forbach", "فورباخ", "Forbach", "Forbach", "France"));
        cities.put("La Rochelle", new City(182, "la-rochelle", "La Rochelle", "لاروشيل", "La Rochelle", "La Rochelle", "France"));
        cities.put("Langres", new City(114, "langres", "Langres", "لانغر", "Langres", "Langres", "France"));
        cities.put("Laval", new City(116, "laval", "Laval", "لافال", "Laval", "Laval", "France"));
        cities.put("Le Havre", new City(130, "le-havre", "Le Havre", "لوهافر", "Le Havre", "Le Havre", "France"));
        cities.put("Le Mans", new City(118, "le-mans", "Le Mans", "لومان", "Le Mans", "Le Mans", "France"));
        cities.put("Le Puy", new City(176, "le-puy", "Le Puy", "لوبوي", "Le Puy", "Le Puy", "France"));
        cities.put("Lens", new City(164, "lens", "Lens", "لانس", "Lens", "Lens", "France"));
        cities.put("Lille", new City(152, "lille", "Lille", "ليل", "Lille", "Lila", "France"));
        cities.put("Limoges", new City(180, "limoges", "Limoges", "ليموج", "Limoges", "Limoges", "France"));
        cities.put("Lyon", new City(153, "lyon", "Lyon", "ليون", "Lyon", "Lyon", "France"));
        cities.put("Macon", new City(126, "macon", "Macon", "ماكون", "Macon", "Macon", "France"));
        cities.put("Marseille", new City(154, "marseille", "Marseille", "مرسيليا", "Marseille", "Marsella", "France"));
        cities.put("Meaux", new City(121, "meaux", "Meaux", "مو", "Meaux", "Meaux", "France"));
        cities.put("Melun", new City(120, "melun", "Melun", "مولان", "Melun", "Melun", "France"));
        cities.put("Metz", new City(155, "metz", "Metz", "ميتز", "Metz", "Metz", "France"));
        cities.put("Montluçon", new City(128, "montlucon", "Montluçon", "مونلوسون", "Montluçon", "Montluçon", "France"));
        cities.put("Montpellier", new City(140, "montpellier", "Montpellier", "مونبلييه", "Montpellier", "Montpellier", "France"));
        cities.put("Mulhouse", new City(156, "mulhouse", "Mulhouse", "ميلوز", "Mulhouse", "Mulhouse", "France"));
        cities.put("Nancy", new City(133, "nancy", "Nancy", "نانسي", "Nancy", "Nancy", "France"));
        cities.put("Nantes", new City(144, "nantes", "Nantes", "نانت", "Nantes", "Nantes", "France"));
        cities.put("Nevers", new City(173, "nevers", "Nevers", "نيفر", "Nevers", "Nevers", "France"));
        cities.put("Nice", new City(188, "nice", "Nice", "نيس", "Nice", "Niza", "France"));
        cities.put("Niort", new City(125, "niort", "Niort", "نيور", "Niort", "Niort", "France"));
        cities.put("Nogent sur Seine", new City(123, "nogent-sur-seine", "Nogent sur Seine", "نوجان سور سين", "Nogent sur Seine", "Nogent sur Seine", "France"));
        cities.put("Nîmes", new City(192, "nimes", "Nîmes", "نيم", "Nîmes", "Nîmes", "France"));
        cities.put("Orléans", new City(157, "orleans", "Orléans", "أورليان", "Orléans", "Orléans", "France"));
        cities.put("Paris", new City(158, "paris", "Paris", "باريس", "Paris", "París", "France"));
        cities.put("Pau", new City(139, "pau", "Pau", "بو", "Pau", "Pau", "France"));
        cities.put("Perpignan", new City(142, "perpignan", "Perpignan", "بربينيان", "Perpignan", "Perpignan", "France"));
        cities.put("Poitiers", new City(159, "poitiers", "Poitiers", "بواتييه", "Poitiers", "Poitiers", "France"));
        cities.put("Pontarlier", new City(175, "pontarlier", "Pontarlier", "بونتارلييه", "Pontarlier", "Pontarlier", "France"));
        cities.put("Péronne", new City(171, "peronne", "Péronne", "بيرون", "Péronne", "Péronne", "France"));
        cities.put("Reims", new City(194, "reims", "Reims", "رانس", "Reims", "Reims", "France"));
        cities.put("Rennes", new City(117, "rennes", "Rennes", "رين", "Rennes", "Rennes", "France"));
        cities.put("Roanne", new City(184, "roanne", "Roanne", "روآن", "Roanne", "Roanne", "France"));
        cities.put("Rodez", new City(177, "rodez", "Rodez", "روديز", "Rodez", "Rodez", "France"));
        cities.put("Rouen", new City(160, "rouen", "Rouen", "روان", "Rouen", "Ruán", "France"));
        cities.put("Saint Etienne", new City(183, "saint-etienne", "Saint Etienne", "سانت إتيان", "Saint Etienne", "Saint Etienne", "France"));
        cities.put("Saint-Dizier", new City(135, "saint-dizier", "Saint-Dizier", "سان ديزييه", "Saint-Dizier", "Saint-Dizier", "France"));
        cities.put("Saint-Dié", new City(112, "saint-die", "Saint-Dié", "سان دييه", "Saint-Dié", "Saint-Dié", "France"));
        cities.put("Strasbourg", new City(161, "strasbourg", "Strasbourg", "ستراسبورغ", "Strasbourg", "Estrasburgo", "France"));
        cities.put("Thionville", new City(109, "thionville", "Thionville", "تيونفيل", "Thionville", "Thionville", "France"));
        cities.put("Toul", new City(137, "toul", "Toul", "تول", "Toul", "Toul", "France"));
        cities.put("Toulon", new City(190, "toulon", "Toulon", "تولون", "Toulon", "Toulon", "France"));
        cities.put("Toulouse", new City(162, "toulouse", "Toulouse", "تولوز", "Toulouse", "Tolosa", "France"));
        cities.put("Tours", new City(163, "tours", "Tours", "تور", "Tours", "Tours", "France"));
        cities.put("Troyes", new City(136, "troyes", "Troyes", "تروا", "Troyes", "Troyes", "France"));
        cities.put("Valence", new City(178, "valence", "Valence", "فالانس", "Valence", "Valence", "France"));
        cities.put("Valenciennes", new City(167, "valenciennes", "Valenciennes", "فالانسيان", "Valenciennes", "Valenciennes", "France"));
        cities.put("Verdun", new City(193, "verdun", "Verdun", "فردان", "Verdun", "Verdun", "France"));
        cities.put("Vichy", new City(179, "vichy", "Vichy", "فيشي", "Vichy", "Vichy", "France"));

        // ── Ghana (6 villes) ──
        cities.put("Accra", new City(34, "accra", "Accra", "أكرا", "Accra", "Accra", "Ghana"));
        cities.put("Kumasi", new City(348, "kumasi", "Kumasi", "كوماسي", "Kumasi", "Kumasi", "Ghana"));
        cities.put("Sekondi", new City(350, "sekondi", "Sekondi", "سيكوندي", "Sekondi", "Sekondi", "Ghana"));
        cities.put("Takoradi", new City(347, "takoradi", "Takoradi", "تاكورادي", "Takoradi", "Takoradi", "Ghana"));
        cities.put("Tamale", new City(349, "tamale", "Tamale", "تامالي", "Tamale", "Tamale", "Ghana"));
        cities.put("Tema", new City(351, "tema", "Tema", "تيما", "Tema", "Tema", "Ghana"));

        // ── Irak (1 villes) ──
        cities.put("Bagdad", new City(33, "bagdad", "Baghdad", "بغداد", "Bagdad", "Bagdad", "Irak"));

        // ── Irlande (1 villes) ──
        cities.put("Dublin", new City(352, "dublin", "Dublin", "دبلن", "Dublin", "Dublín", "Irlande"));

        // ── Italie (13 villes) ──
        cities.put("Bari", new City(251, "bari", "Bari", "باري", "Bari", "Bari", "Italie"));
        cities.put("Bologna", new City(26, "bologna", "Bologna", "بولونيا", "Bologna", "Bolonia", "Italie"));
        cities.put("Cagliari", new City(253, "cagliari", "Cagliari", "كالياري", "Cagliari", "Cagliari", "Italie"));
        cities.put("Catania", new City(252, "catania", "Catania", "قطانية", "Catania", "Catania", "Italie"));
        cities.put("Firenze", new City(25, "firenze", "Florence", "فلورنسا", "Firenze", "Florencia", "Italie"));
        cities.put("Genoa", new City(31, "genoa", "Genoa", "جنوة", "Genoa", "Génova", "Italie"));
        cities.put("Milan", new City(28, "milan", "Milan", "ميلانو", "Milan", "Milán", "Italie"));
        cities.put("Napoli", new City(30, "napoli", "Naples", "نابولي", "Napoli", "Nápoles", "Italie"));
        cities.put("Palermo", new City(24, "palermo", "Palermo", "باليرمو", "Palermo", "Palermo", "Italie"));
        cities.put("Roma", new City(29, "roma", "Rome", "روما", "Roma", "Roma", "Italie"));
        cities.put("Turin", new City(27, "turin", "Turin", "تورينو", "Turin", "Turín", "Italie"));
        cities.put("Venezia", new City(32, "venezia", "Venice", "البندقية", "Venezia", "Venecia", "Italie"));
        cities.put("Verona", new City(249, "verona", "Verona", "فيرونا", "Verona", "Verona", "Italie"));

        // ── Japon (10 villes) ──
        cities.put("Fukuoka", new City(356, "fukuoka", "Fukuoka", "فوكوكا", "Fukuoka", "Fukuoka", "Japon"));
        cities.put("Hiroshima", new City(361, "hiroshima", "Hiroshima", "هيروشيما", "Hiroshima", "Hiroshima", "Japon"));
        cities.put("Kawasaki", new City(359, "kawasaki", "Kawasaki", "كاواساكي", "Kawasaki", "Kawasaki", "Japon"));
        cities.put("Kitakyushu", new City(353, "kitakyushu", "Kitakyushu", "كيتاكيوشو", "Kitakyushu", "Kitakyushu", "Japon"));
        cities.put("Kobe", new City(358, "kobe", "Kobe", "كوبه", "Kobe", "Kobe", "Japon"));
        cities.put("Kyoto", new City(360, "kyoto", "Kyoto", "كيوتو", "Kyoto", "Kyoto", "Japon"));
        cities.put("Nagoya", new City(354, "nagoya", "Nagoya", "ناغويا", "Nagoya", "Nagoya", "Japon"));
        cities.put("Osaka", new City(362, "osaka", "Osaka", "أوساكا", "Osaka", "Osaka", "Japon"));
        cities.put("Sapporo", new City(355, "sapporo", "Sapporo", "سابورو", "Sapporo", "Sapporo", "Japon"));
        cities.put("Tokyo", new City(357, "tokyo", "Tokyo", "طوكيو", "Tokyo", "Tokyo", "Japon"));

        // ── Liban (1 villes) ──
        cities.put("Beyrouth", new City(23, "beyrouth", "Beirut", "بيروت", "Beyrouth", "Beirut", "Liban"));

        // ── Libye (1 villes) ──
        cities.put("Tripoli", new City(261, "tripoli", "Tripoli", "طرابلس", "Tripoli", "Trípoli", "Libye"));

        // ── Mali (1 villes) ──
        cities.put("Bamako", new City(22, "bamako", "Bamako", "باماكو", "Bamako", "Bamako", "Mali"));

        // ── Mauritanie (1 villes) ──
        cities.put("Nouakchott", new City(21, "nouakchott", "Nouakchott", "نواكشوط", "Nouakchott", "Nuakchot", "Mauritanie"));

        // ── Nigéria (7 villes) ──
        cities.put("Ibadan", new City(367, "ibadan", "Ibadan", "إبادان", "Ibadan", "Ibadan", "Nigéria"));
        cities.put("Ilesha", new City(366, "ilesha", "Ilesha", "إيليشا", "Ilesha", "Ilesha", "Nigéria"));
        cities.put("Ilorin", new City(364, "ilorin", "Ilorin", "إلورين", "Ilorin", "Ilorin", "Nigéria"));
        cities.put("Kano", new City(365, "kano", "Kano", "كانو", "Kano", "Kano", "Nigéria"));
        cities.put("Lagos", new City(15, "lagos", "Lagos", "لاغوس", "Lagos", "Lagos", "Nigéria"));
        cities.put("Onitsha", new City(363, "onitsha", "Onitsha", "أونيتشا", "Onitsha", "Onitsha", "Nigéria"));
        cities.put("Zaria", new City(368, "zaria", "Zaria", "زاريا", "Zaria", "Zaria", "Nigéria"));

        // ── Norvège (1 villes) ──
        cities.put("Oslo", new City(386, "oslo", "Oslo", "أوسلو", "Oslo", "Oslo", "Norvège"));

        // ── Nouvelle-Zélande (4 villes) ──
        cities.put("Auckland", new City(385, "auckland", "Auckland", "أوكلاند", "Auckland", "Auckland", "Nouvelle-Zélande"));
        cities.put("Dunedin", new City(383, "dunedin", "Dunedin", "دنيدن", "Dunedin", "Dunedin", "Nouvelle-Zélande"));
        cities.put("Hamilton", new City(382, "hamilton", "Hamilton", "هاميلتون", "Hamilton", "Hamilton", "Nouvelle-Zélande"));
        cities.put("Wellington", new City(384, "wellington", "Wellington", "ولينغتون", "Wellington", "Wellington", "Nouvelle-Zélande"));

        // ── Pays-Bas (21 villes) ──
        cities.put("Almere", new City(413, "almere", "Almere", "ألمير", "Almere", "Almere", "Pays-Bas"));
        cities.put("Amersfoort", new City(422, "amersfoort", "Amersfoort", "أمرسفورت", "Amersfoort", "Amersfoort", "Pays-Bas"));
        cities.put("Amsterdam", new City(16, "amsterdam", "Amsterdam", "أمستردام", "Amsterdam", "Amsterdam", "Pays-Bas"));
        cities.put("Apeldoorn", new City(420, "apeldoorn", "Apeldoorn", "أبلدورن", "Apeldoorn", "Apeldoorn", "Pays-Bas"));
        cities.put("Arnhem", new City(417, "arnhem", "Arnhem", "أرنهيم", "Arnhem", "Arnhem", "Pays-Bas"));
        cities.put("Bois-le-Duc", new City(419, "bois-le-duc", "'s-Hertogenbosch", "سيرتوخنبوس", "Bois-le-Duc", "Bois-le-Duc", "Pays-Bas"));
        cities.put("Bréda", new City(414, "breda", "Breda", "بريدا", "Bréda", "Bréda", "Pays-Bas"));
        cities.put("Eindhoven", new City(20, "eindhoven", "Eindhoven", "آيندهوفن", "Eindhoven", "Eindhoven", "Pays-Bas"));
        cities.put("Enschede", new City(421, "enschede", "Enschede", "أنسخديه", "Enschede", "Enschede", "Pays-Bas"));
        cities.put("Groningue", new City(412, "groningue", "Groningen", "خرونينغن", "Groningue", "Groningue", "Pays-Bas"));
        cities.put("Haarlem", new City(418, "haarlem", "Haarlem", "هارلم", "Haarlem", "Haarlem", "Pays-Bas"));
        cities.put("Haarlemmermeer", new City(424, "haarlemmermeer", "Haarlemmermeer", "هارلمرمير", "Haarlemmermeer", "Haarlemmermeer", "Pays-Bas"));
        cities.put("La Haye", new City(18, "la-haye", "The Hague", "لاهاي", "La Haye", "La Haye", "Pays-Bas"));
        cities.put("Maastricht", new City(416, "maastricht", "Maastricht", "ماستريخت", "Maastricht", "Maastricht", "Pays-Bas"));
        cities.put("Nimègue", new City(415, "nimegue", "Nijmegen", "نايميخن", "Nimègue", "Nimègue", "Pays-Bas"));
        cities.put("Rotterdam", new City(17, "rotterdam", "Rotterdam", "روتردام", "Rotterdam", "Rotterdam", "Pays-Bas"));
        cities.put("Tilburg", new City(258, "tilburg", "Tilburg", "تيلبورغ", "Tilburg", "Tilburg", "Pays-Bas"));
        cities.put("Utrecht", new City(19, "utrecht", "Utrecht", "أوتريخت", "Utrecht", "Utrecht", "Pays-Bas"));
        cities.put("Zaanstad", new City(423, "zaanstad", "Zaanstad", "زانستاد", "Zaanstad", "Zaanstad", "Pays-Bas"));
        cities.put("Zoetermeer", new City(426, "zoetermeer", "Zoetermeer", "زوترمير", "Zoetermeer", "Zoetermeer", "Pays-Bas"));
        cities.put("Zwolle", new City(425, "zwolle", "Zwolle", "زفوله", "Zwolle", "Zwolle", "Pays-Bas"));

        // ── Qatar (1 villes) ──
        cities.put("Doha", new City(14, "doha", "Doha", "الدوحة", "Doha", "Doha", "Qatar"));

        // ── Royaume-Uni (9 villes) ──
        cities.put("Belfast", new City(396, "belfast", "Belfast", "بلفاست", "Belfast", "Belfast", "Royaume-Uni"));
        cities.put("Birmingham", new City(395, "birmingham", "Birmingham", "برمنغهام", "Birmingham", "Birmingham", "Royaume-Uni"));
        cities.put("Bristol", new City(393, "bristol", "Bristol", "بريستول", "Bristol", "Bristol", "Royaume-Uni"));
        cities.put("Cardiff", new City(400, "cardiff", "Cardiff", "كارديف", "Cardiff", "Cardiff", "Royaume-Uni"));
        cities.put("Glasgow", new City(399, "glasgow", "Glasgow", "غلاسكو", "Glasgow", "Glasgow", "Royaume-Uni"));
        cities.put("Liverpool", new City(397, "liverpool", "Liverpool", "ليفربول", "Liverpool", "Liverpool", "Royaume-Uni"));
        cities.put("London", new City(7, "london", "London", "لندن", "London", "Londres", "Royaume-Uni"));
        cities.put("Manchester", new City(398, "manchester", "Manchester", "مانشستر", "Manchester", "Manchester", "Royaume-Uni"));
        cities.put("Sheffield", new City(394, "sheffield", "Sheffield", "شيفيلد", "Sheffield", "Sheffield", "Royaume-Uni"));

        // ── Suisse (15 villes) ──
        cities.put("Berne", new City(6, "berne", "Bern", "برن", "Berne", "Berna", "Suisse"));
        cities.put("Bienne", new City(409, "bienne", "Bienne", "بيال", "Bienne", "Bienne", "Suisse"));
        cities.put("Bâle", new City(401, "bale", "Basel", "بازل", "Bâle", "Basilea", "Suisse"));
        cities.put("Fribourg", new City(403, "fribourg", "Fribourg", "فريبورغ", "Fribourg", "Friburgo", "Suisse"));
        cities.put("Genève", new City(5, "geneve", "Geneva", "جنيف", "Genève", "Ginebra", "Suisse"));
        cities.put("La Chaux-de-Fonds", new City(411, "la-chaux-de-fonds", "La Chaux-de-Fonds", "لاشو دو فون", "La Chaux-de-Fonds", "La Chaux-de-Fonds", "Suisse"));
        cities.put("Lausanne", new City(3, "lausanne", "Lausanne", "لوزان", "Lausanne", "Lausana", "Suisse"));
        cities.put("Lucerne", new City(404, "lucerne", "Lucerne", "لوسيرن", "Lucerne", "Lucerna", "Suisse"));
        cities.put("Lugano", new City(408, "lugano", "Lugano", "لوغانو", "Lugano", "Lugano", "Suisse"));
        cities.put("Montreux", new City(405, "montreux", "Montreux", "مونترو", "Montreux", "Montreux", "Suisse"));
        cities.put("Neuchatel", new City(402, "neuchatel", "Neuchâtel", "نوشاتيل", "Neuchatel", "Neuchâtel", "Suisse"));
        cities.put("Saint-Gall", new City(407, "saint-gall", "St. Gallen", "سان غالن", "Saint-Gall", "San Galo", "Suisse"));
        cities.put("Thoune", new City(410, "thoune", "Thoune", "تون", "Thoune", "Thun", "Suisse"));
        cities.put("Winterthour", new City(406, "winterthour", "Winterthur", "فينترتور", "Winterthour", "Winterthur", "Suisse"));
        cities.put("Zurich", new City(4, "zurich", "Zurich", "زيورخ", "Zurich", "Zúrich", "Suisse"));

        // ── Suède (5 villes) ──
        cities.put("Borås", new City(390, "boras", "Boras", "بوروس", "Borås", "Borås", "Suède"));
        cities.put("Malmö", new City(391, "malmo", "Malmo", "مالمو", "Malmö", "Malmö", "Suède"));
        cities.put("Stockholm", new City(389, "stockholm", "Stockholm", "ستوكهولم", "Stockholm", "Stockholm", "Suède"));
        cities.put("Uppsala", new City(392, "uppsala", "Uppsala", "أوبسالا", "Uppsala", "Uppsala", "Suède"));
        cities.put("Örebro", new City(387, "orebro", "Orebro", "أوريبرو", "Örebro", "Örebro", "Suède"));

        // ── Syrie (1 villes) ──
        cities.put("Damas", new City(10, "damas", "Damascus", "دمشق", "Damas", "Damasco", "Syrie"));

        // ── Sénégal (1 villes) ──
        cities.put("Dakar", new City(11, "dakar", "Dakar", "داكار", "Dakar", "Dakar", "Sénégal"));

        // ── Tunisie (24 villes) ──
        cities.put("Ariana", new City(304, "ariana", "Ariana", "أريانة", "Ariana", "Ariana", "Tunisie"));
        cities.put("Ben Arous", new City(306, "ben-arous", "Ben Arous", "بن عروس", "Ben Arous", "Ben Arous", "Tunisie"));
        cities.put("Bizerte", new City(307, "bizerte", "Bizerte", "بنزرت", "Bizerte", "Bizerte", "Tunisie"));
        cities.put("Béja", new City(305, "beja", "Béja", "باجة", "Béja", "Béja", "Tunisie"));
        cities.put("Gabès", new City(308, "gabes", "Gabès", "قابس", "Gabès", "Gabès", "Tunisie"));
        cities.put("Gafsa", new City(309, "gafsa", "Gafsa", "قفصة", "Gafsa", "Gafsa", "Tunisie"));
        cities.put("Jendouba", new City(310, "jendouba", "Jendouba", "جندوبة", "Jendouba", "Jendouba", "Tunisie"));
        cities.put("Kairouan", new City(311, "kairouan", "Kairouan", "القيروان", "Kairouan", "Kairouan", "Tunisie"));
        cities.put("Kasserine", new City(312, "kasserine", "Kasserine", "القصرين", "Kasserine", "Kasserine", "Tunisie"));
        cities.put("Kébili", new City(313, "kebili", "Kébili", "قبلي", "Kébili", "Kébili", "Tunisie"));
        cities.put("La Manouba", new City(316, "la-manouba", "La Manouba", "منوبة", "La Manouba", "La Manouba", "Tunisie"));
        cities.put("Le Kef", new City(314, "le-kef", "Le Kef", "الكاف", "Le Kef", "Le Kef", "Tunisie"));
        cities.put("Mahdia", new City(315, "mahdia", "Mahdia", "المهدية", "Mahdia", "Mahdia", "Tunisie"));
        cities.put("Monastir", new City(318, "monastir", "Monastir", "المنستير", "Monastir", "Monastir", "Tunisie"));
        cities.put("Médenine", new City(317, "medenine", "Médenine", "مدنين", "Médenine", "Médenine", "Tunisie"));
        cities.put("Nabeul", new City(319, "nabeul", "Nabeul", "نابل", "Nabeul", "Nabeul", "Tunisie"));
        cities.put("Sfax", new City(320, "sfax", "Sfax", "صفاقس", "Sfax", "Sfax", "Tunisie"));
        cities.put("Sidi Bouzid", new City(321, "sidi-bouzid", "Sidi Bouzid", "سيدي بوزيد", "Sidi Bouzid", "Sidi Bouzid", "Tunisie"));
        cities.put("Siliana", new City(322, "siliana", "Siliana", "سليانة", "Siliana", "Siliana", "Tunisie"));
        cities.put("Sousse", new City(323, "sousse", "Sousse", "سوسة", "Sousse", "Sousse", "Tunisie"));
        cities.put("Tataouine", new City(324, "tataouine", "Tataouine", "تطاوين", "Tataouine", "Tataouine", "Tunisie"));
        cities.put("Tozeur", new City(325, "tozeur", "Tozeur", "توزر", "Tozeur", "Tozeur", "Tunisie"));
        cities.put("Tunis", new City(1, "tunis", "Tunis", "تونس", "Tunis", "Túnez", "Tunisie"));
        cities.put("Zaghouan", new City(326, "zaghouan", "Zaghouan", "زغوان", "Zaghouan", "Zaghouan", "Tunisie"));

    }

    public static List<City> getAllCities() {
        return new ArrayList<>(cities.values());
    }

    public static City getCity(String key) {
        return cities.get(key);
    }

    public static City getCityById(int id) {
        for (City c : cities.values()) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    /** Distinct country names in insertion order (Maroc first). */
    public static List<String> getCountries() {
        List<String> out = new ArrayList<>();
        for (City c : cities.values()) {
            if (!out.contains(c.getCountry())) out.add(c.getCountry());
        }
        return out;
    }

    public static List<City> getCitiesByCountry(String country) {
        List<City> out = new ArrayList<>();
        for (City c : cities.values()) {
            if (c.getCountry().equals(country)) out.add(c);
        }
        return out;
    }

    public static List<City> searchCities(String query, String language) {
        List<City> results = new ArrayList<>();
        String q = normalize(query);
        if (q.isEmpty()) {
            results.addAll(cities.values());
            return results;
        }
        for (City city : cities.values()) {
            if (normalize(city.getName(language)).contains(q)
                    || normalize(city.getNameEn()).contains(q)
                    || normalize(city.getNameFr()).contains(q)
                    || normalize(city.getNameEs()).contains(q)
                    || normalize(city.getNameAr()).contains(q)
                    || TranslationManager.countryMatches(city.getCountry(), q)) {
                results.add(city);
            }
        }
        return results;
    }

    /**
     * French country names matching the query in ANY app language
     * (insertion order; caller sorts for display).
     */
    public static List<String> searchCountries(String query) {
        String q = normalize(query);
        List<String> out = new ArrayList<>();
        for (String c : getCountries()) {
            if (TranslationManager.countryMatches(c, q)) out.add(c);
        }
        return out;
    }

    public static City getCityByName(String name) {
        if (name == null) return cities.get("Tanger");
        City direct = cities.get(name);
        if (direct != null) return direct;
        // exact match on any language variant
        for (City c : cities.values()) {
            if (c.getNameEn().equals(name) || c.getNameFr().equals(name)
                    || c.getNameEs().equals(name)
                    || c.getNameAr().equals(name) || c.getSlug().equals(name)) return c;
        }
        // accent/case-insensitive fallback (handles old ASCII keys: Fes->Fès, Meknes->Meknès, ...)
        String n = normalize(name);
        for (City c : cities.values()) {
            if (normalize(c.getNameEn()).equals(n) || normalize(c.getNameFr()).equals(n)
                    || normalize(c.getSlug().replace('-', ' ')).equals(n)) return c;
        }
        return cities.get("Tanger"); // default
    }

    /** Lowercase + strip accents for tolerant search. */
    private static String normalize(String s) {
        if (s == null) return "";
        String n = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        n = n.replaceAll("\\p{M}", "");
        return n.toLowerCase(java.util.Locale.ROOT);
    }
}
