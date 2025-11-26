package com.salah.times;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    
    private TextView clockText;
    private TextView dateText;
    private TextView hijriText;
    private TextView nextPrayerText;
    private TextView countdownText;
    private RecyclerView prayerGrid;
    private Handler handler = new Handler();
    private Runnable updateTimeRunnable;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        setupPrayerGrid();
        startClockUpdate();
        loadPrayerTimes();
    }
    
    private void initViews() {
        clockText = findViewById(R.id.clock_text);
        dateText = findViewById(R.id.date_text);
        hijriText = findViewById(R.id.hijri_text);
        nextPrayerText = findViewById(R.id.next_prayer_text);
        countdownText = findViewById(R.id.countdown_text);
        prayerGrid = findViewById(R.id.prayer_grid);
    }
    
    private void setupPrayerGrid() {
        prayerGrid.setLayoutManager(new GridLayoutManager(this, 2));
        // Will add adapter later
    }
    
    private void startClockUpdate() {
        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                updateClock();
                handler.postDelayed(this, 1000); // Update every second
            }
        };
        handler.post(updateTimeRunnable);
    }
    
    private void updateClock() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        
        Date now = new Date();
        clockText.setText(timeFormat.format(now));
        dateText.setText(dateFormat.format(now));
    }
    
    private void loadPrayerTimes() {
        SharedPrefsManager prefsManager = new SharedPrefsManager(this);
        City defaultCity = CitiesData.getCityByName(prefsManager.getDefaultCity());
        if (defaultCity != null) {
            PrayerTimesService.fetchPrayerTimes(defaultCity)
                .thenAccept(prayerTimes -> runOnUiThread(() -> updatePrayerTimesUI(prayerTimes)))
                .exceptionally(throwable -> {
                    runOnUiThread(() -> showError(throwable.getMessage()));
                    return null;
                });
        }
    }
    
    private void updatePrayerTimesUI(PrayerTimes prayerTimes) {
        // Update UI with prayer times
    }
    
    private void showError(String error) {
        // Show error message
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && updateTimeRunnable != null) {
            handler.removeCallbacks(updateTimeRunnable);
        }
    }
}