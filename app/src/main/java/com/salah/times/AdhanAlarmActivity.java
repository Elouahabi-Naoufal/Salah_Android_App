package com.salah.times;

import android.app.KeyguardManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class AdhanAlarmActivity extends AppCompatActivity {
    private static Ringtone currentRingtone;
    private static MediaPlayer mediaPlayer;
    private String prayerName;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Show over lock screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                               WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
        
        setContentView(R.layout.activity_adhan_alarm);
        
        prayerName = getIntent().getStringExtra("prayer_name");
        if (prayerName == null) prayerName = "Prayer";
        
        TextView prayerNameText = findViewById(R.id.prayer_name);
        prayerNameText.setText(prayerName);
        
        Button stopButton = findViewById(R.id.stop_button);
        Button snoozeButton = findViewById(R.id.snooze_button);
        
        stopButton.setOnClickListener(v -> {
            stopAlarm();
            finish();
        });
        
        snoozeButton.setOnClickListener(v -> {
            stopAlarm();
            scheduleSnooze();
            finish();
        });
        
        // Start playing alarm
        playAlarm();
    }
    
    private void playAlarm() {
        stopAlarm();
        
        // Try custom adhan first
        try {
            Uri adhanUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.adhan);
            currentRingtone = RingtoneManager.getRingtone(this, adhanUri);
            if (currentRingtone != null) {
                currentRingtone.play();
                android.util.Log.d("AdhanAlarm", "Custom adhan playing");
                return;
            }
        } catch (Exception e) {
            android.util.Log.e("AdhanAlarm", "Custom adhan failed: " + e.getMessage());
        }
        
        // Fallback to system alarm
        try {
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri != null) {
                currentRingtone = RingtoneManager.getRingtone(this, alarmUri);
                if (currentRingtone != null) {
                    currentRingtone.play();
                    android.util.Log.d("AdhanAlarm", "System alarm playing");
                }
            }
        } catch (Exception e) {
            android.util.Log.e("AdhanAlarm", "System alarm failed: " + e.getMessage());
        }
    }
    
    public static void stopAlarm() {
        // Stop MediaPlayer immediately
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            } catch (Exception e) {
                android.util.Log.e("AdhanAlarm", "Error stopping MediaPlayer", e);
            }
            mediaPlayer = null;
        }
        
        // Stop Ringtone as fallback
        if (currentRingtone != null) {
            try {
                if (currentRingtone.isPlaying()) {
                    currentRingtone.stop();
                }
            } catch (Exception e) {
                android.util.Log.e("AdhanAlarm", "Error stopping Ringtone", e);
            }
            currentRingtone = null;
        }
    }
    
    private void scheduleSnooze() {
        Calendar snoozeTime = Calendar.getInstance();
        snoozeTime.add(Calendar.MINUTE, 5);
        
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;
        
        android.content.Intent intent = new android.content.Intent(this, PrayerAlarmReceiver.class);
        intent.putExtra("prayer_name", prayerName + " (Snooze)");
        intent.putExtra("is_snooze", true);
        
        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(this, 8888, intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, 
                    snoozeTime.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, 
                    snoozeTime.getTimeInMillis(), pendingIntent);
            }
            android.widget.Toast.makeText(this, "Snoozed for 5 minutes", android.widget.Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            android.util.Log.e("AdhanAlarm", "Permission denied for snooze alarm", e);
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAlarm();
    }
    
    @Override
    public void onBackPressed() {
        // Prevent back button from closing alarm
    }
}