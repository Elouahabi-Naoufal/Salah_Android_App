package com.salah.times;

public class City {
    private int id;
    private String slug;
    private String nameEn;
    private String nameAr;
    private String nameFr;
    private String nameEs;
    private String country;

    public City(int id, String slug, String nameEn, String nameAr, String nameFr, String nameEs, String country) {
        this.id = id;
        this.slug = slug;
        this.nameEn = nameEn;
        this.nameAr = nameAr;
        this.nameFr = nameFr;
        this.nameEs = nameEs;
        this.country = country;
    }

    /** Backward-compatible constructor (defaults es to English, country to "Maroc"). */
    public City(int id, String slug, String nameEn, String nameAr, String nameFr, String country) {
        this(id, slug, nameEn, nameAr, nameFr, nameEn, country);
    }

    /** Backward-compatible constructor (defaults country to "Maroc"). */
    public City(int id, String slug, String nameEn, String nameAr, String nameFr) {
        this(id, slug, nameEn, nameAr, nameFr, nameEn, "Maroc");
    }

    public int getId() { return id; }
    public String getSlug() { return slug; }
    /** Table name: slug with hyphens replaced by underscores */
    public String getTableName() { return slug.replace('-', '_'); }
    public String getNameEn() { return nameEn; }
    public String getNameAr() { return nameAr; }
    public String getNameFr() { return nameFr; }
    public String getNameEs() { return nameEs; }
    public String getCountry() { return country; }

    public String getName(String language) {
        switch (language) {
            case "ar": return nameAr;
            case "fr": return nameFr;
            case "es": return nameEs;
            default: return nameEn;
        }
    }
}