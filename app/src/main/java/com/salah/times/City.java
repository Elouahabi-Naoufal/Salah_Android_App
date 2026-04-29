package com.salah.times;

public class City {
    private int id;
    private String slug;
    private String nameEn;
    private String nameAr;
    private String nameFr;

    public City(int id, String slug, String nameEn, String nameAr, String nameFr) {
        this.id = id;
        this.slug = slug;
        this.nameEn = nameEn;
        this.nameAr = nameAr;
        this.nameFr = nameFr;
    }

    public int getId() { return id; }
    public String getSlug() { return slug; }
    /** Table name: slug with hyphens replaced by underscores */
    public String getTableName() { return slug.replace('-', '_'); }
    public String getNameEn() { return nameEn; }
    public String getNameAr() { return nameAr; }
    public String getNameFr() { return nameFr; }

    public String getName(String language) {
        switch (language) {
            case "ar": return nameAr;
            case "fr": return nameFr;
            default: return nameEn;
        }
    }
}