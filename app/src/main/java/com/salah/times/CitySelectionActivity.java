package com.salah.times;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Two-step default-city picker: choose a country first, then a city inside it.
 * Search matches countries AND cities in ANY app language (not just default).
 */
public class CitySelectionActivity extends AppCompatActivity {
    private static final int MODE_COUNTRIES = 0;
    private static final int MODE_CITIES = 1;

    private RecyclerView recyclerView;
    private PickerAdapter adapter;
    private EditText searchEditText;
    private final List<Object> displayItems = new ArrayList<>();
    private TextView welcomeTitle, welcomeSubtitle;

    private int mode = MODE_COUNTRIES;
    /** French canonical country name selected in step 1. */
    private String selectedCountryFr;
    private String query = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ThemeManager.applyTheme();
        
        setContentView(R.layout.activity_city_selection_ultra_modern);
        
        initViews();
        setupRecyclerView();
        setupSearch();
        rebuildItems();
    }
    
    private void initViews() {
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        welcomeTitle = findViewById(R.id.welcomeTitle);
        welcomeSubtitle = findViewById(R.id.welcomeSubtitle);
        searchEditText = findViewById(R.id.searchEditText);
        recyclerView = findViewById(R.id.citiesRecyclerView);
        
        welcomeTitle.setText(TranslationManager.tr("city_selection.welcome"));
        searchEditText.setHint(TranslationManager.tr("city_selection.search_country_city"));
    }
    
    private void setupRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        
        adapter = new PickerAdapter(displayItems, this::onCountrySelected, this::onCitySelected);
        recyclerView.setAdapter(adapter);
    }
    
    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                query = s.toString();
                rebuildItems();
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void rebuildItems() {
        displayItems.clear();
        String currentLang = TranslationManager.getCurrentLanguage();

        if (mode == MODE_COUNTRIES) {
            welcomeSubtitle.setText(TranslationManager.tr("city_selection.select_country"));
            List<String> countries = CitiesData.searchCountries(query);
            sortCountriesByDisplayName(countries);
            if (!countries.isEmpty()) {
                for (String fr : countries) {
                    displayItems.add(new CountryEntry(fr, CitiesData.getCitiesByCountry(fr).size()));
                }
            } else {
                // Query matched no country (e.g. a city name): show matching
                // cities grouped under their country headers.
                addGroupedCities(CitiesData.searchCities(query, currentLang), currentLang);
            }
        } else {
            welcomeSubtitle.setText(TranslationManager.trCountry(selectedCountryFr));
            List<City> all = CitiesData.getCitiesByCountry(selectedCountryFr);
            List<City> shown = new ArrayList<>();
            String q = TranslationManager.normalize(query);
            for (City c : all) {
                if (q.isEmpty()
                        || TranslationManager.normalize(c.getName(currentLang)).contains(q)
                        || TranslationManager.normalize(c.getNameEn()).contains(q)
                        || TranslationManager.normalize(c.getNameFr()).contains(q)
                        || TranslationManager.normalize(c.getNameAr()).contains(q)
                        || TranslationManager.countryMatches(c.getCountry(), q)) {
                    shown.add(c);
                }
            }
            Collections.sort(shown, (a, b) ->
                    a.getName(currentLang).compareToIgnoreCase(b.getName(currentLang)));
            displayItems.addAll(shown);
        }

        adapter.notifyDataSetChanged();
    }

    /** Group cities under A-Z country headers (fallback search results). */
    private void addGroupedCities(List<City> source, String currentLang) {
        Map<String, List<City>> byCountry = new LinkedHashMap<>();
        for (City c : source) {
            if (!byCountry.containsKey(c.getCountry())) byCountry.put(c.getCountry(), new ArrayList<>());
            byCountry.get(c.getCountry()).add(c);
        }
        List<String> countries = new ArrayList<>(byCountry.keySet());
        sortCountriesByDisplayName(countries);
        for (String fr : countries) {
            List<City> list = byCountry.get(fr);
            Collections.sort(list, (a, b) ->
                    a.getName(currentLang).compareToIgnoreCase(b.getName(currentLang)));
            displayItems.add(fr);
            displayItems.addAll(list);
        }
    }

    private void sortCountriesByDisplayName(List<String> countries) {
        Collections.sort(countries, (a, b) ->
                TranslationManager.trCountry(a).compareToIgnoreCase(TranslationManager.trCountry(b)));
    }

    private void onCountrySelected(String frCountry) {
        selectedCountryFr = frCountry;
        mode = MODE_CITIES;
        rebuildItems();
    }

    private void backToCountries() {
        mode = MODE_COUNTRIES;
        selectedCountryFr = null;
        rebuildItems();
    }
    
    private void onCitySelected(City city) {
        SettingsManager.setDefaultCity(city.getNameEn());
        
        Toast.makeText(this, 
            TranslationManager.tr("missing_strings.city_changed_full", 
                city.getName(TranslationManager.getCurrentLanguage())), 
            Toast.LENGTH_SHORT).show();
        
        setResult(RESULT_OK);
        finish();
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        if (mode == MODE_CITIES) {
            backToCountries();
            return true;
        }
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mode == MODE_CITIES) {
            backToCountries();
            return;
        }
        super.onBackPressed();
    }

    /** Row wrapper for step 1 (String items are country section headers). */
    public static class CountryEntry {
        public final String frName;
        public final int cityCount;
        public CountryEntry(String frName, int cityCount) {
            this.frName = frName;
            this.cityCount = cityCount;
        }
    }
    
    public static class PickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int VIEW_COUNTRY = 0;
        private static final int VIEW_HEADER = 1;
        private static final int VIEW_CITY = 2;

        private final List<Object> items;
        private OnCountrySelectedListener countryListener;
        private OnCitySelectedListener cityListener;
        private String selectedCityName;
        
        public interface OnCountrySelectedListener {
            void onCountrySelected(String frCountry);
        }

        public interface OnCitySelectedListener {
            void onCitySelected(City city);
        }
        
        public PickerAdapter(List<Object> items,
                             OnCountrySelectedListener countryListener,
                             OnCitySelectedListener cityListener) {
            this.items = items;
            this.countryListener = countryListener;
            this.cityListener = cityListener;
            this.selectedCityName = SettingsManager.getDefaultCity();
        }

        @Override
        public int getItemViewType(int position) {
            Object o = items.get(position);
            if (o instanceof CountryEntry) return VIEW_COUNTRY;
            if (o instanceof City) return VIEW_CITY;
            return VIEW_HEADER;
        }
        
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            if (viewType == VIEW_HEADER) {
                View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_country_header, parent, false);
                return new HeaderViewHolder(view);
            }
            View view = android.view.LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_city_ultra_modern, parent, false);
            return new CityViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof HeaderViewHolder) {
                String fr = (String) items.get(position);
                int count = countCitiesAfter(position);
                ((HeaderViewHolder) holder).countryName.setText(
                        TranslationManager.trCountry(fr) + " (" + count + ")");
                return;
            }
            CityViewHolder cityHolder = (CityViewHolder) holder;
            String currentLang = TranslationManager.getCurrentLanguage();

            if (holder.getItemViewType() == VIEW_COUNTRY) {
                CountryEntry entry = (CountryEntry) items.get(position);
                cityHolder.cityName.setText(TranslationManager.trCountry(entry.frName));
                cityHolder.cityRegion.setVisibility(View.VISIBLE);
                cityHolder.cityRegion.setText(TranslationManager.tr(
                        "city_selection.cities_in_country", String.valueOf(entry.cityCount)));
                cityHolder.selectionIndicator.setVisibility(View.GONE);
                cityHolder.itemView.setOnClickListener(v ->
                        countryListener.onCountrySelected(entry.frName));
                return;
            }

            City city = (City) items.get(position);
            cityHolder.cityName.setText(city.getName(currentLang));
            cityHolder.cityRegion.setVisibility(View.GONE);
            
            boolean isSelected = CitiesData.getCityByName(selectedCityName).getNameEn()
                    .equals(city.getNameEn());
            cityHolder.selectionIndicator.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            
            cityHolder.itemView.setOnClickListener(v -> {
                selectedCityName = city.getNameEn();
                notifyDataSetChanged();
                cityListener.onCitySelected(city);
            });
        }

        /** Count consecutive City items following a header at position. */
        private int countCitiesAfter(int headerPosition) {
            int count = 0;
            for (int i = headerPosition + 1; i < items.size(); i++) {
                if (!(items.get(i) instanceof City)) break;
                count++;
            }
            return count;
        }
        
        @Override
        public int getItemCount() {
            return items.size();
        }

        static class HeaderViewHolder extends RecyclerView.ViewHolder {
            TextView countryName;

            HeaderViewHolder(View itemView) {
                super(itemView);
                countryName = itemView.findViewById(R.id.countryName);
            }
        }
        
        static class CityViewHolder extends RecyclerView.ViewHolder {
            TextView cityName, cityRegion;
            View selectionIndicator;
            
            CityViewHolder(View itemView) {
                super(itemView);
                cityName = itemView.findViewById(R.id.cityName);
                cityRegion = itemView.findViewById(R.id.cityRegion);
                selectionIndicator = itemView.findViewById(R.id.selectionIndicator);
            }
        }
    }
}
