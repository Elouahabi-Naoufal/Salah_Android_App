package com.salah.times;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class AdhanNotificationsActivity extends AppCompatActivity {
    
    private CheckBox cbMonday, cbTuesday, cbWednesday, cbThursday, cbFriday, cbSaturday, cbSunday;
    private CheckBox cbFajr, cbDhuhr, cbAsr, cbMaghrib, cbIsha;
    private TextView timeFajr, timeDhuhr, timeAsr, timeMaghrib, timeIsha;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adhan_notifications);
        
        initViews();
        loadPrayerTimes();
        loadSettings();
        setupTimeClickListeners();
        
        findViewById(R.id.save_button).setOnClickListener(v -> saveSettings());
    }
    
    private void initViews() {
        cbMonday = findViewById(R.id.cb_monday);
        cbTuesday = findViewById(R.id.cb_tuesday);
        cbWednesday = findViewById(R.id.cb_wednesday);
        cbThursday = findViewById(R.id.cb_thursday);
        cbFriday = findViewById(R.id.cb_friday);
        cbSaturday = findViewById(R.id.cb_saturday);
        cbSunday = findViewById(R.id.cb_sunday);
        
        cbFajr = findViewById(R.id.cb_fajr);
        cbDhuhr = findViewById(R.id.cb_dhuhr);
        cbAsr = findViewById(R.id.cb_asr);
        cbMaghrib = findViewById(R.id.cb_maghrib);
        cbIsha = findViewById(R.id.cb_isha);
        
        timeFajr = findViewById(R.id.time_fajr);
        timeDhuhr = findViewById(R.id.time_dhuhr);
        timeAsr = findViewById(R.id.time_asr);
        timeMaghrib = findViewById(R.id.time_maghrib);
        timeIsha = findViewById(R.id.time_isha);
    }
    
    private void loadPrayerTimes() {
        City defaultCity = CitiesData.getCityByName(SettingsManager.getDefaultCity());
        if (defaultCity != null) {
            PrayerTimeWorker worker = new PrayerTimeWorker(this);
            worker.loadPrayerTimes(defaultCity, new PrayerTimeWorker.PrayerTimeCallback() {
                @Override
                public void onSuccess(PrayerTimes prayerTimes) {
                    runOnUiThread(() -> updatePrayerTimesDisplay(prayerTimes));
                }
                
                @Override
                public void onError(String error) {
                    // Use fallback times if loading fails
                }
                
                @Override
                public void onCachedData(PrayerTimes prayerTimes, int daysRemaining) {
                    runOnUiThread(() -> updatePrayerTimesDisplay(prayerTimes));
                }
            });
        }
    }
    
    private void updatePrayerTimesDisplay(PrayerTimes prayerTimes) {
        // Load custom times or use actual prayer times
        timeFajr.setText(SharedPrefsManager.getString("adhan_time_fajr", prayerTimes.getFajr()));
        timeDhuhr.setText(SharedPrefsManager.getString("adhan_time_dhuhr", prayerTimes.getDhuhr()));
        timeAsr.setText(SharedPrefsManager.getString("adhan_time_asr", prayerTimes.getAsr()));
        timeMaghrib.setText(SharedPrefsManager.getString("adhan_time_maghrib", prayerTimes.getMaghrib()));
        timeIsha.setText(SharedPrefsManager.getString("adhan_time_isha", prayerTimes.getIsha()));
    }
    
    private void setupTimeClickListeners() {
        timeFajr.setOnClickListener(v -> showTimePicker("fajr", timeFajr));
        timeDhuhr.setOnClickListener(v -> showTimePicker("dhuhr", timeDhuhr));
        timeAsr.setOnClickListener(v -> showTimePicker("asr", timeAsr));
        timeMaghrib.setOnClickListener(v -> showTimePicker("maghrib", timeMaghrib));
        timeIsha.setOnClickListener(v -> showTimePicker("isha", timeIsha));
    }
    
    private void showTimePicker(String prayer, TextView timeView) {
        String currentTime = timeView.getText().toString();
        String[] timeParts = currentTime.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
            (view, selectedHour, selectedMinute) -> {
                String newTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                timeView.setText(newTime);
            }, hour, minute, true);
        
        timePickerDialog.show();
    }
    
    private void loadSettings() {
        cbMonday.setChecked(SharedPrefsManager.getBoolean("adhan_monday", true));
        cbTuesday.setChecked(SharedPrefsManager.getBoolean("adhan_tuesday", true));
        cbWednesday.setChecked(SharedPrefsManager.getBoolean("adhan_wednesday", true));
        cbThursday.setChecked(SharedPrefsManager.getBoolean("adhan_thursday", true));
        cbFriday.setChecked(SharedPrefsManager.getBoolean("adhan_friday", true));
        cbSaturday.setChecked(SharedPrefsManager.getBoolean("adhan_saturday", true));
        cbSunday.setChecked(SharedPrefsManager.getBoolean("adhan_sunday", true));
        
        cbFajr.setChecked(SharedPrefsManager.getBoolean("adhan_fajr", true));
        cbDhuhr.setChecked(SharedPrefsManager.getBoolean("adhan_dhuhr", true));
        cbAsr.setChecked(SharedPrefsManager.getBoolean("adhan_asr", true));
        cbMaghrib.setChecked(SharedPrefsManager.getBoolean("adhan_maghrib", true));
        cbIsha.setChecked(SharedPrefsManager.getBoolean("adhan_isha", true));
    }
    
    private void saveSettings() {
        SharedPrefsManager.putBoolean("adhan_monday", cbMonday.isChecked());
        SharedPrefsManager.putBoolean("adhan_tuesday", cbTuesday.isChecked());
        SharedPrefsManager.putBoolean("adhan_wednesday", cbWednesday.isChecked());
        SharedPrefsManager.putBoolean("adhan_thursday", cbThursday.isChecked());
        SharedPrefsManager.putBoolean("adhan_friday", cbFriday.isChecked());
        SharedPrefsManager.putBoolean("adhan_saturday", cbSaturday.isChecked());
        SharedPrefsManager.putBoolean("adhan_sunday", cbSunday.isChecked());
        
        SharedPrefsManager.putBoolean("adhan_fajr", cbFajr.isChecked());
        SharedPrefsManager.putBoolean("adhan_dhuhr", cbDhuhr.isChecked());
        SharedPrefsManager.putBoolean("adhan_asr", cbAsr.isChecked());
        SharedPrefsManager.putBoolean("adhan_maghrib", cbMaghrib.isChecked());
        SharedPrefsManager.putBoolean("adhan_isha", cbIsha.isChecked());
        
        // Save custom prayer times
        SharedPrefsManager.putString("adhan_time_fajr", timeFajr.getText().toString());
        SharedPrefsManager.putString("adhan_time_dhuhr", timeDhuhr.getText().toString());
        SharedPrefsManager.putString("adhan_time_asr", timeAsr.getText().toString());
        SharedPrefsManager.putString("adhan_time_maghrib", timeMaghrib.getText().toString());
        SharedPrefsManager.putString("adhan_time_isha", timeIsha.getText().toString());
        
        scheduleAdhanAlarms();
        
        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
        finish();
    }
    
    private void scheduleAdhanAlarms() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        
        // Cancel existing alarms
        for (int i = 0; i < 35; i++) { // 7 days * 5 prayers
            Intent intent = new Intent(this, AdhanAlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            alarmManager.cancel(pendingIntent);
        }
        
        // Schedule new alarms based on current prayer times
        City defaultCity = CitiesData.getCityByName(SettingsManager.getDefaultCity());
        if (defaultCity != null) {
            PrayerTimeWorker worker = new PrayerTimeWorker(this);
            worker.loadPrayerTimes(defaultCity, new PrayerTimeWorker.PrayerTimeCallback() {
                @Override
                public void onSuccess(PrayerTimes prayerTimes) {
                    scheduleAlarmsForPrayerTimes(prayerTimes);
                }
                
                @Override
                public void onError(String error) {}
                
                @Override
                public void onCachedData(PrayerTimes prayerTimes, int daysRemaining) {
                    scheduleAlarmsForPrayerTimes(prayerTimes);
                }
            });
        }
    }
    
    private void scheduleAlarmsForPrayerTimes(PrayerTimes prayerTimes) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        String[] prayers = {"fajr", "dhuhr", "asr", "maghrib", "isha"};
        
        // Use custom times from UI instead of fetched prayer times
        String[] customTimes = {
            timeFajr.getText().toString(),
            timeDhuhr.getText().toString(), 
            timeAsr.getText().toString(),
            timeMaghrib.getText().toString(),
            timeIsha.getText().toString()
        };
        
        for (int day = 1; day <= 7; day++) { // Calendar.SUNDAY = 1
            if (!isDayEnabled(day)) continue;
            
            for (int p = 0; p < prayers.length; p++) {
                if (!isPrayerEnabled(prayers[p])) continue;
                
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_WEEK, day);
                
                String[] timeParts = customTimes[p].split(":");
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
                calendar.set(Calendar.SECOND, 0);
                
                if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                    calendar.add(Calendar.WEEK_OF_YEAR, 1);
                }
                
                Intent intent = new Intent(this, AdhanAlarmReceiver.class);
                intent.putExtra("prayer", prayers[p]);
                
                int requestCode = day * 10 + p;
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
            }
        }
    }
    
    private boolean isDayEnabled(int day) {
        switch (day) {
            case Calendar.MONDAY: return cbMonday.isChecked();
            case Calendar.TUESDAY: return cbTuesday.isChecked();
            case Calendar.WEDNESDAY: return cbWednesday.isChecked();
            case Calendar.THURSDAY: return cbThursday.isChecked();
            case Calendar.FRIDAY: return cbFriday.isChecked();
            case Calendar.SATURDAY: return cbSaturday.isChecked();
            case Calendar.SUNDAY: return cbSunday.isChecked();
            default: return false;
        }
    }
    
    private boolean isPrayerEnabled(String prayer) {
        switch (prayer) {
            case "fajr": return cbFajr.isChecked();
            case "dhuhr": return cbDhuhr.isChecked();
            case "asr": return cbAsr.isChecked();
            case "maghrib": return cbMaghrib.isChecked();
            case "isha": return cbIsha.isChecked();
            default: return false;
        }
    }
}