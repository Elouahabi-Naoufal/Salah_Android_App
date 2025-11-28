package com.salah.times;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;

public class StoragePermissionActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 100;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_permission);
        
        TextView messageText = findViewById(R.id.permission_message);
        Button grantButton = findViewById(R.id.grant_permission_button);
        
        messageText.setText("📱 مطلوب إذن التخزين\n\n" +
                "تطبيق مواقيت الصلاة يحتاج الوصول للتخزين من أجل:\n" +
                "• 📶 مواقيت الصلاة بدون إنترنت (30+ يوم)\n" +
                "• ⚙️ نسخ احتياطي للإعدادات\n" +
                "• 🔔 تفضيلات الإشعارات\n\n" +
                "يعمل بدون إنترنت بعد الإعداد!");
        
        grantButton.setOnClickListener(v -> requestStoragePermission());
        
        if (hasStoragePermission()) {
            proceedToMainApp();
        }
    }
    
    private boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            return ContextCompat.checkSelfPermission(this, 
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }
    
    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 
                STORAGE_PERMISSION_CODE);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (hasStoragePermission()) {
            proceedToMainApp();
        }
    }
    
    private void proceedToMainApp() {
        createDirectoryStructure();
        
        SharedPrefsManager prefsManager = new SharedPrefsManager(this);
        String savedCity = prefsManager.getDefaultCity();
        
        // Check if user has selected a city
        if (savedCity.isEmpty()) {
            // First time - show city selection
            startActivity(new Intent(this, CitySelectionActivity.class));
        } else {
            // User has selected a city - go to main app
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
    
    private void createDirectoryStructure() {
        File baseDir = new File(Environment.getExternalStorageDirectory(), "SalahTimes");
        File citiesDir = new File(baseDir, "cities");
        File configDir = new File(baseDir, "config");
        
        citiesDir.mkdirs();
        configDir.mkdirs();
    }
}