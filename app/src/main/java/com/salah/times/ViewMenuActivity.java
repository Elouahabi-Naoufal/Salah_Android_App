package com.salah.times;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ViewMenuActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This activity extends MainActivity functionality
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_monthly_calendar) {
            showMonthlyCalendar();
            return true;
        } else if (id == R.id.action_weekly_schedule) {
            showWeeklySchedule();
            return true;
        } else if (id == R.id.action_timezone_view) {
            showTimezoneView();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void showMonthlyCalendar() {
        showFeatureUnavailable("Monthly Calendar");
    }
    
    private void showWeeklySchedule() {
        showFeatureUnavailable("Weekly Schedule");
    }
    
    private void showTimezoneView() {
        showFeatureUnavailable("Multiple Timezones");
    }
    
    private void showFeatureUnavailable(String featureName) {
        Toast.makeText(this, featureName + " feature is not available yet.", Toast.LENGTH_SHORT).show();
    }
}