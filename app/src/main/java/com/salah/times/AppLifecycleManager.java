package com.salah.times;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AppLifecycleManager implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "AppLifecycle";
    private int activityCount = 0;
    private boolean isAppInForeground = false;
    
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d(TAG, "Activity created: " + activity.getClass().getSimpleName());
    }
    
    @Override
    public void onActivityStarted(Activity activity) {
        activityCount++;
        if (!isAppInForeground) {
            isAppInForeground = true;
            onAppForegrounded(activity);
        }
        Log.d(TAG, "Activity started: " + activity.getClass().getSimpleName());
    }
    
    @Override
    public void onActivityResumed(Activity activity) {
        Log.d(TAG, "Activity resumed: " + activity.getClass().getSimpleName());
    }
    
    @Override
    public void onActivityPaused(Activity activity) {
        Log.d(TAG, "Activity paused: " + activity.getClass().getSimpleName());
    }
    
    @Override
    public void onActivityStopped(Activity activity) {
        activityCount--;
        if (activityCount == 0) {
            isAppInForeground = false;
            onAppBackgrounded(activity);
        }
        Log.d(TAG, "Activity stopped: " + activity.getClass().getSimpleName());
    }
    
    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.d(TAG, "Activity save instance state: " + activity.getClass().getSimpleName());
    }
    
    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d(TAG, "Activity destroyed: " + activity.getClass().getSimpleName());
    }
    
    private void onAppForegrounded(Activity activity) {
        Log.d(TAG, "App moved to foreground");
        // Start notification service if not running
        startNotificationServiceIfNeeded(activity);
    }
    
    private void onAppBackgrounded(Activity activity) {
        Log.d(TAG, "App moved to background");
        // Keep notification service running for persistent display
    }
    
    private void startNotificationServiceIfNeeded(Context context) {
        try {
            Intent serviceIntent = new Intent(context, PrayerNotificationService.class);
            context.startForegroundService(serviceIntent);
        } catch (Exception e) {
            Log.e(TAG, "Error starting notification service", e);
        }
    }
    
    public boolean isAppInForeground() {
        return isAppInForeground;
    }
}