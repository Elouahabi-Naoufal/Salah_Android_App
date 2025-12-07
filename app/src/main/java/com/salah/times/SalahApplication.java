package com.salah.times;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.io.File;

public class SalahApplication extends Application {
    private static final String TAG = "SalahApplication";
    private static final String PREFS_NAME = "app_config";
    private static final String KEY_FIRST_RUN = "first_run";
    private static final String KEY_APP_VERSION = "app_version";
    
    private static SalahApplication instance;
    private TranslationManager translationManager;
    private AppLifecycleManager lifecycleManager;
    
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        
        Log.d(TAG, "Salah Times Application Starting");
        
        initializeApplication();
        setupConfiguration();
        initializeTranslations();
        createConfigDirectories();
        setupLifecycleManager();
    }
    
    private void initializeApplication() {
        // Initialize managers
        SharedPrefsManager.init(this);
        TranslationManager.init(this);
        StorageManager.init(this);
        SettingsManager.init(this);
        
        // Run migration
        MigrationHelper.migrateIfNeeded(this);
        
        // Set application metadata
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (prefs.getBoolean(KEY_FIRST_RUN, true)) {
            Log.d(TAG, "First run detected, initializing defaults");
            setupFirstRun();
        }
        
        // Update version info
        prefs.edit().putString(KEY_APP_VERSION, "2.0").apply();
    }
    
    private void setupFirstRun() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
            .putBoolean(KEY_FIRST_RUN, false)
            .putLong("install_time", System.currentTimeMillis())
            .apply();
        
        // Initialize default settings in database
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        db.saveSetting("default_city", "Casablanca");
        db.saveSetting("language", "ar");
        db.saveSetting("notifications_enabled", "true");
        db.saveSetting("theme", "auto");
        db.saveSetting("auto_update", "true");
        db.saveSetting("adan_enabled", "true");
        
        // Set default iqama delays
        db.setIqamaDelay("fajr", 10);
        db.setIqamaDelay("dhuhr", 10);
        db.setIqamaDelay("asr", 10);
        db.setIqamaDelay("maghrib", 5);
        db.setIqamaDelay("isha", 10);
        
        Log.d(TAG, "First run setup completed");
    }
    
    private void setupConfiguration() {
        // Load saved language preference
        String savedLanguage = SettingsManager.getLanguage();
        TranslationManager.setLanguage(savedLanguage);
        
        Log.d(TAG, "Configuration loaded - Language: " + savedLanguage + 
                   ", City: " + SettingsManager.getDefaultCity());
    }
    
    private void initializeTranslations() {
        // Initialize translation system
        String currentLanguage = TranslationManager.getCurrentLanguage();
        Log.d(TAG, "Translation system initialized for language: " + currentLanguage);
    }
    
    private void createConfigDirectories() {
        try {
            // Create necessary directories for configuration files
            File configDir = new File(getFilesDir(), "config");
            if (!configDir.exists()) {
                configDir.mkdirs();
                Log.d(TAG, "Created config directory: " + configDir.getAbsolutePath());
            }
            
            File cacheDir = new File(getCacheDir(), "prayer_cache");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
                Log.d(TAG, "Created cache directory: " + cacheDir.getAbsolutePath());
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error creating directories", e);
        }
    }
    
    public static SalahApplication getInstance() {
        return instance;
    }
    

    
    public boolean isFirstRun() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_FIRST_RUN, true);
    }
    
    public String getAppVersion() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_APP_VERSION, "1.0");
    }
    
    public long getInstallTime() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getLong("install_time", System.currentTimeMillis());
    }
    
    @Override
    public void onTerminate() {
        Log.d(TAG, "Salah Times Application Terminating");
        super.onTerminate();
    }
    
    private void setupLifecycleManager() {
        lifecycleManager = new AppLifecycleManager();
        registerActivityLifecycleCallbacks(lifecycleManager);
        Log.d(TAG, "Lifecycle manager registered");
    }
    
    public AppLifecycleManager getLifecycleManager() {
        return lifecycleManager;
    }
    
    @Override
    public void onLowMemory() {
        Log.w(TAG, "Low memory warning received");
        super.onLowMemory();
    }
}