package com.salah.times;

import android.content.Intent;
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

public class CitySelectionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GroupedCityAdapter adapter;
    private EditText searchEditText;
    /** Mixed list: String (country header) or City (row), countries sorted A-Z. */
    private final List<Object> displayItems = new ArrayList<>();
    private TextView welcomeTitle, welcomeSubtitle;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ThemeManager.applyTheme();
        
        setContentView(R.layout.activity_city_selection_ultra_modern);
        
        initViews();
        setupRecyclerView();
        setupSearch();
        rebuildItems("");
    }
    
    private void initViews() {
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        welcomeTitle = findViewById(R.id.welcomeTitle);
        welcomeSubtitle = findViewById(R.id.welcomeSubtitle);
        searchEditText = findViewById(R.id.searchEditText);
        recyclerView = findViewById(R.id.citiesRecyclerView);
        
        welcomeTitle.setText(TranslationManager.tr("city_selection.welcome"));
        welcomeSubtitle.setText(TranslationManager.tr("city_selection.select_city"));
        searchEditText.setHint(TranslationManager.tr("city_selection.search_city"));
    }
    
    private void setupRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        
        adapter = new GroupedCityAdapter(displayItems, this::onCitySelected);
        recyclerView.setAdapter(adapter);
    }
    
    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                rebuildItems(s.toString());
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * Rebuild grouped display items: countries strictly A-Z
     * (Maroc in alphabetical position, no priority), cities A-Z
     * within each country. Search filters across city names + country.
     */
    private void rebuildItems(String query) {
        displayItems.clear();
        String currentLang = TranslationManager.getCurrentLanguage();

        List<City> source;
        if (query == null || query.isEmpty()) {
            source = CitiesData.getAllCities();
        } else {
            source = CitiesData.searchCities(query, currentLang);
        }

        Map<String, List<City>> byCountry = new LinkedHashMap<>();
        for (City c : source) {
            if (!byCountry.containsKey(c.getCountry())) {
                byCountry.put(c.getCountry(), new ArrayList<>());
            }
            byCountry.get(c.getCountry()).add(c);
        }

        List<String> countries = new ArrayList<>(byCountry.keySet());
        Collections.sort(countries);

        for (String country : countries) {
            List<City> list = byCountry.get(country);
            Collections.sort(list, (a, b) ->
                    a.getName(currentLang).compareToIgnoreCase(b.getName(currentLang)));
            displayItems.add(country);
            displayItems.addAll(list);
        }

        if (adapter != null) adapter.notifyDataSetChanged();
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
        finish();
        return true;
    }
    
    public static class GroupedCityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int VIEW_HEADER = 0;
        private static final int VIEW_CITY = 1;

        private final List<Object> items;
        private OnCitySelectedListener listener;
        private String selectedCityName;
        
        public interface OnCitySelectedListener {
            void onCitySelected(City city);
        }
        
        public GroupedCityAdapter(List<Object> items, OnCitySelectedListener listener) {
            this.items = items;
            this.listener = listener;
            this.selectedCityName = SettingsManager.getDefaultCity();
        }

        @Override
        public int getItemViewType(int position) {
            return items.get(position) instanceof City ? VIEW_CITY : VIEW_HEADER;
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
                String country = (String) items.get(position);
                int count = countCitiesAfter(position);
                ((HeaderViewHolder) holder).countryName.setText(country + " (" + count + ")");
                return;
            }
            City city = (City) items.get(position);
            CityViewHolder cityHolder = (CityViewHolder) holder;
            String currentLang = TranslationManager.getCurrentLanguage();
            
            cityHolder.cityName.setText(city.getName(currentLang));
            cityHolder.cityRegion.setVisibility(View.GONE);
            
            boolean isSelected = CitiesData.getCityByName(selectedCityName).getNameEn()
                    .equals(city.getNameEn());
            cityHolder.selectionIndicator.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            
            cityHolder.itemView.setOnClickListener(v -> {
                selectedCityName = city.getNameEn();
                notifyDataSetChanged();
                listener.onCitySelected(city);
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
