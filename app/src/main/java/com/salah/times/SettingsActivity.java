package com.salah.times;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private SharedPrefsManager prefsManager;
    private IqamaManager iqamaManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        prefsManager = new SharedPrefsManager(this);
        iqamaManager = new IqamaManager(this);
        
        initViews();
        setupTabs();
    }
    
    private void initViews() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        
        getSupportActionBar().setTitle(TranslationManager.tr("settings"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
    private void setupTabs() {
        SettingsAdapter adapter = new SettingsAdapter(this, prefsManager, iqamaManager);
        viewPager.setAdapter(adapter);
        
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("General"); break;
                case 1: tab.setText("Iqama"); break;
                case 2: tab.setText("Notifications"); break;
            }
        }).attach();
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}