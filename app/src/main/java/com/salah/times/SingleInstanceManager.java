package com.salah.times;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.util.List;

public class SingleInstanceManager {
    private static final String TAG = "SingleInstance";
    
    public static boolean isAppRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = activityManager.getRunningAppProcesses();
        
        if (runningProcesses != null) {
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.processName.equals(context.getPackageName())) {
                    return processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
                }
            }
        }
        return false;
    }
    
    public static void bringToForeground(Context context) {
        try {
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
            Log.d(TAG, "Bringing existing instance to foreground");
        } catch (Exception e) {
            Log.e(TAG, "Error bringing app to foreground", e);
        }
    }
    
    public static void handleSingleInstance(Activity activity) {
        // Android handles single instance through launch modes in manifest
        // This method can be used for additional single instance logic
        Log.d(TAG, "Single instance check completed for: " + activity.getClass().getSimpleName());
    }
}