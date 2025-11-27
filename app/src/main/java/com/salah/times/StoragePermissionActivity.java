package com.salah.times;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StoragePermissionActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_permission);
        
        TextView explanationText = findViewById(R.id.explanation_text);
        Button grantButton = findViewById(R.id.grant_permission_button);
        
        explanationText.setText(
            "Salah Times needs storage access to:\n\n" +
            "• Save prayer times for offline use\n" +
            "• Cache data for faster loading\n" +
            "• Work without internet connection\n\n" +
            "Your data stays on your device only."
        );
        
        grantButton.setOnClickListener(v -> {
            PermissionManager.requestStoragePermissions(this);
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (PermissionManager.hasStoragePermissions(this)) {
            // Permission granted, go to main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}