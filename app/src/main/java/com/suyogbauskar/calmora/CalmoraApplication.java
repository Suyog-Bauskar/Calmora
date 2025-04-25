package com.suyogbauskar.calmora;

import android.app.Application;

import com.suyogbauskar.calmora.utils.AppUsageTracker;

public class CalmoraApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize app usage tracker
        AppUsageTracker.getInstance().initialize(this);
    }
    
    @Override
    public void onTerminate() {
        // Save usage data when app is terminated
        AppUsageTracker.getInstance().updateUsageTime();
        super.onTerminate();
    }
} 