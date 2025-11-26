package com.salah.times;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class GeometryManager {
    private static final String PREFS_NAME = "geometry_prefs";
    private static final String KEY_WINDOW_WIDTH = "window_width";
    private static final String KEY_WINDOW_HEIGHT = "window_height";
    private static final String KEY_WINDOW_X = "window_x";
    private static final String KEY_WINDOW_Y = "window_y";
    
    private Context context;
    private SharedPreferences prefs;
    
    public GeometryManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    public void saveWindowGeometry(int width, int height, int x, int y) {
        prefs.edit()
            .putInt(KEY_WINDOW_WIDTH, width)
            .putInt(KEY_WINDOW_HEIGHT, height)
            .putInt(KEY_WINDOW_X, x)
            .putInt(KEY_WINDOW_Y, y)
            .apply();
    }
    
    public WindowGeometry getWindowGeometry() {
        DisplayMetrics displayMetrics = getDisplayMetrics();
        
        // Default values (centered)
        int defaultWidth = Math.min(480, displayMetrics.widthPixels - 100);
        int defaultHeight = Math.min(720, displayMetrics.heightPixels - 100);
        int defaultX = (displayMetrics.widthPixels - defaultWidth) / 2;
        int defaultY = (displayMetrics.heightPixels - defaultHeight) / 2;
        
        int width = prefs.getInt(KEY_WINDOW_WIDTH, defaultWidth);
        int height = prefs.getInt(KEY_WINDOW_HEIGHT, defaultHeight);
        int x = prefs.getInt(KEY_WINDOW_X, defaultX);
        int y = prefs.getInt(KEY_WINDOW_Y, defaultY);
        
        // Validate coordinates are within screen bounds
        if (x < 0 || x > displayMetrics.widthPixels - width) {
            x = defaultX;
        }
        if (y < 0 || y > displayMetrics.heightPixels - height) {
            y = defaultY;
        }
        
        return new WindowGeometry(width, height, x, y);
    }
    
    public boolean hasStoredGeometry() {
        return prefs.contains(KEY_WINDOW_WIDTH);
    }
    
    public void clearGeometry() {
        prefs.edit()
            .remove(KEY_WINDOW_WIDTH)
            .remove(KEY_WINDOW_HEIGHT)
            .remove(KEY_WINDOW_X)
            .remove(KEY_WINDOW_Y)
            .apply();
    }
    
    private DisplayMetrics getDisplayMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }
    
    public static class WindowGeometry {
        public final int width;
        public final int height;
        public final int x;
        public final int y;
        
        public WindowGeometry(int width, int height, int x, int y) {
            this.width = width;
            this.height = height;
            this.x = x;
            this.y = y;
        }
        
        public boolean isValid() {
            return width > 0 && height > 0 && x >= 0 && y >= 0;
        }
        
        @Override
        public String toString() {
            return String.format("WindowGeometry{width=%d, height=%d, x=%d, y=%d}", width, height, x, y);
        }
    }
}