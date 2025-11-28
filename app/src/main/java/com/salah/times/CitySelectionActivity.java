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
import java.util.List;

public class CitySelectionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CityAdapter adapter;
    private EditText searchEditText;
    private List<City> allCities;
    private List<City> filteredCities;
    private TextView welcomeTitle, welcomeSubtitle;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ThemeManager.applyTheme();
        
        setContentView(R.layout.activity_city_selection_ultra_modern);
        
        initViews();
        setupRecyclerView();
        setupSearch();
        loadCities();
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
        
        filteredCities = new ArrayList<>();
        adapter = new CityAdapter(filteredCities, this::onCitySelected);
        recyclerView.setAdapter(adapter);
    }
    
    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCities(s.toString());
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void loadCities() {
        allCities = CitiesData.getAllCities();
        filteredCities.clear();
        filteredCities.addAll(allCities);
        adapter.notifyDataSetChanged();
    }
    
    private void filterCities(String query) {
        filteredCities.clear();
        
        if (query.isEmpty()) {
            filteredCities.addAll(allCities);
        } else {
            String currentLang = TranslationManager.getCurrentLanguage();
            List<City> searchResults = CitiesData.searchCities(query, currentLang);
            filteredCities.addAll(searchResults);
        }
        
        adapter.notifyDataSetChanged();
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
    
    public static class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {
        private List<City> cities;
        private OnCitySelectedListener listener;
        private String selectedCityName;
        
        public interface OnCitySelectedListener {
            void onCitySelected(City city);
        }
        
        public CityAdapter(List<City> cities, OnCitySelectedListener listener) {
            this.cities = cities;
            this.listener = listener;
            this.selectedCityName = SettingsManager.getDefaultCity();
        }
        
        @Override
        public CityViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_city_ultra_modern, parent, false);
            return new CityViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(CityViewHolder holder, int position) {
            City city = cities.get(position);
            String currentLang = TranslationManager.getCurrentLanguage();
            
            holder.cityName.setText(city.getName(currentLang));
            holder.cityRegion.setVisibility(View.GONE);
            
            boolean isSelected = city.getNameEn().equals(selectedCityName);
            holder.selectionIndicator.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            
            holder.itemView.setOnClickListener(v -> {
                selectedCityName = city.getNameEn();
                notifyDataSetChanged();
                listener.onCitySelected(city);
            });
        }
        
        @Override
        public int getItemCount() {
            return cities.size();
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