package com.suyogbauskar.calmora.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to track app usage time and update Firestore database
 */
public class AppUsageTracker implements Application.ActivityLifecycleCallbacks {
    
    private static AppUsageTracker instance;
    private long startTime;
    private long sessionDuration;
    private boolean isTracking = false;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    
    private AppUsageTracker() {
        startTime = 0;
        sessionDuration = 0;
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }
    
    public static AppUsageTracker getInstance() {
        if (instance == null) {
            instance = new AppUsageTracker();
        }
        return instance;
    }
    
    public void initialize(Application application) {
        application.registerActivityLifecycleCallbacks(this);
    }
    
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
    }
    
    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (!isTracking) {
            startTime = SystemClock.elapsedRealtime();
            isTracking = true;
        }
    }
    
    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if (!isTracking) {
            startTime = SystemClock.elapsedRealtime();
            isTracking = true;
        }
    }
    
    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        if (isTracking) {
            sessionDuration += (SystemClock.elapsedRealtime() - startTime);
            isTracking = false;
        }
    }
    
    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }
    
    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
    }
    
    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }
    
    private void continueWithUsageUpdate(FirebaseUser currentUser, DocumentReference userRef, long sessionDuration) {
        // Convert milliseconds to minutes
        final long usageTimeMinutes = sessionDuration / (1000 * 60);
        
        // Create a final reference to track if we need to reset session duration
        final long[] sessionToReset = {sessionDuration};
        
        // Update daily usage for the current day
        Map<String, Object> updates = new HashMap<>();
        
        // Get current day of week (1-7, Sunday is 1)
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 1=Sunday, 7=Saturday
        
        // Create key for current day of week
        String dayKey = "day_" + dayOfWeek; 
        
        // Also store current week identifier (year + week number)
        int weekNumber = calendar.get(Calendar.WEEK_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);
        String weekKey = "week_" + year + "_" + weekNumber;
        
        Log.d("AppUsageTracker", "Updating usage: " + usageTimeMinutes + " minutes for day " + dayOfWeek + " (week: " + weekKey + ")");
        
        // First get current document to verify values and ensure consistency
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> initialFields = new HashMap<>();
            initialFields.put("currentWeekId", weekKey);
            
            // Create all day fields if they don't exist
            for (int i = 1; i <= 7; i++) {
                String key = "day_" + i;
                if (!documentSnapshot.contains(key)) {
                    initialFields.put(key, 0);
                }
            }
            
            if (!documentSnapshot.contains("totalAppUsage")) {
                initialFields.put("totalAppUsage", 0);
            }
            
            // Ensure fields exist first with a merge operation
            userRef.set(initialFields, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    // Now that we've ensured fields exist, get the latest document again
                    userRef.get().addOnSuccessListener(latestDoc -> {
                        // Update the specific day
                        Map<String, Object> dayUpdate = new HashMap<>();
                        
                        // Get current day value and add to it
                        Long currentDayValue = 0L;
                        if (latestDoc.contains(dayKey)) {
                            Object value = latestDoc.get(dayKey);
                            if (value instanceof Long) {
                                currentDayValue = (Long) value;
                            } else if (value instanceof Double) {
                                currentDayValue = ((Double) value).longValue();
                            } else if (value instanceof Integer) {
                                currentDayValue = ((Integer) value).longValue();
                            } else if (value != null) {
                                try {
                                    currentDayValue = Long.parseLong(value.toString());
                                } catch (NumberFormatException e) {
                                    // Keep as 0
                                }
                            }
                        }
                        
                        // Set new day value
                        long newDayValue = currentDayValue + usageTimeMinutes;
                        dayUpdate.put(dayKey, newDayValue);
                        
                        // Calculate total week usage from all days to ensure consistency
                        long totalWeekUsage = 0;
                        for (int i = 1; i <= 7; i++) {
                            String key = "day_" + i;
                            if (i == dayOfWeek) {
                                // For current day, use the new value
                                totalWeekUsage += newDayValue;
                            } else if (latestDoc.contains(key)) {
                                // For other days, use existing values
                                Object value = latestDoc.get(key);
                                if (value instanceof Long) {
                                    totalWeekUsage += (Long) value;
                                } else if (value instanceof Double) {
                                    totalWeekUsage += ((Double) value).longValue();
                                } else if (value instanceof Integer) {
                                    totalWeekUsage += ((Integer) value).longValue();
                                } else if (value != null) {
                                    try {
                                        totalWeekUsage += Long.parseLong(value.toString());
                                    } catch (NumberFormatException e) {
                                        // Skip invalid value
                                    }
                                }
                            }
                        }
                        
                        // Set the recalculated total
                        dayUpdate.put("currentWeekUsage", totalWeekUsage);
                        
                        // Update total lifetime usage
                        Long totalAppUsage = 0L;
                        if (latestDoc.contains("totalAppUsage")) {
                            Object value = latestDoc.get("totalAppUsage");
                            if (value instanceof Long) {
                                totalAppUsage = (Long) value;
                            } else if (value instanceof Double) {
                                totalAppUsage = ((Double) value).longValue();
                            } else if (value instanceof Integer) {
                                totalAppUsage = ((Integer) value).longValue();
                            } else if (value != null) {
                                try {
                                    totalAppUsage = Long.parseLong(value.toString());
                                } catch (NumberFormatException e) {
                                    // Keep as 0
                                }
                            }
                        }
                        
                        dayUpdate.put("totalAppUsage", totalAppUsage + usageTimeMinutes);
                        
                        // Track which week this is
                        dayUpdate.put("currentWeekId", weekKey);
                        
                        Log.d("AppUsageTracker", "Updating with exact values: " + dayUpdate.toString());
                        
                        // Update with calculated values (not increments)
                        userRef.update(dayUpdate)
                            .addOnSuccessListener(aVoid2 -> {
                                // Reset session duration after successful update
                                synchronized (this) {
                                    if (sessionToReset[0] > 0) {
                                        this.sessionDuration = 0;
                                        sessionToReset[0] = 0;  // Mark as reset
                                    }
                                }
                                Log.d("AppUsageTracker", "Successfully updated usage data");
                                
                                // Log updated document for verification
                                userRef.get().addOnSuccessListener(doc -> {
                                    if (doc.exists()) {
                                        Map<String, Object> data = doc.getData();
                                        if (data != null) {
                                            Log.d("AppUsageTracker", "User document after update: " + data.toString());
                                            validateWeeklyTotal(doc);
                                        }
                                    }
                                });
                            })
                            .addOnFailureListener(e -> {
                                // Log failure
                                Log.e("AppUsageTracker", "Failed to update usage: " + e.getMessage());
                            });
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("AppUsageTracker", "Failed to initialize fields: " + e.getMessage());
                });
        }).addOnFailureListener(e -> {
            Log.e("AppUsageTracker", "Failed to get user document: " + e.getMessage());
        });
    }
    
    /**
     * Validates that the total weekly usage matches the sum of daily values
     * and logs any inconsistency
     */
    private void validateWeeklyTotal(DocumentSnapshot document) {
        if (document == null || !document.exists()) return;
        
        long calculatedTotal = 0;
        Long storedTotal = null;
        
        for (int i = 1; i <= 7; i++) {
            String dayKey = "day_" + i;
            if (document.contains(dayKey)) {
                Object value = document.get(dayKey);
                if (value instanceof Long) {
                    calculatedTotal += (Long) value;
                } else if (value instanceof Double) {
                    calculatedTotal += ((Double) value).longValue();
                } else if (value instanceof Integer) {
                    calculatedTotal += ((Integer) value).longValue();
                } else if (value != null) {
                    try {
                        calculatedTotal += Long.parseLong(value.toString());
                    } catch (NumberFormatException e) {
                        // Skip invalid value
                    }
                }
            }
        }
        
        if (document.contains("currentWeekUsage")) {
            Object value = document.get("currentWeekUsage");
            if (value instanceof Long) {
                storedTotal = (Long) value;
            } else if (value instanceof Double) {
                storedTotal = ((Double) value).longValue();
            } else if (value instanceof Integer) {
                storedTotal = ((Integer) value).longValue();
            } else if (value != null) {
                try {
                    storedTotal = Long.parseLong(value.toString());
                } catch (NumberFormatException e) {
                    // Leave as null
                }
            }
        }
        
        if (storedTotal != null && storedTotal != calculatedTotal) {
            Log.w("AppUsageTracker", "Inconsistency detected: currentWeekUsage=" + storedTotal + 
                  ", sum of daily values=" + calculatedTotal);
            
            // Fix the inconsistency
            document.getReference().update("currentWeekUsage", calculatedTotal)
                .addOnSuccessListener(aVoid -> {
                    Log.d("AppUsageTracker", "Fixed inconsistency in currentWeekUsage");
                })
                .addOnFailureListener(e -> {
                    Log.e("AppUsageTracker", "Failed to fix inconsistency: " + e.getMessage());
                });
        }
    }
    
    /**
     * Updates the Firestore database with the current session's usage time
     * Should be called when app is going to background or closing
     */
    public void updateUsageTime() {
        long currentSessionDuration;
        
        synchronized (this) {
            if (isTracking) {
                sessionDuration += (SystemClock.elapsedRealtime() - startTime);
                isTracking = false;
            }
            
            // Make a copy of the current session duration
            currentSessionDuration = sessionDuration;
            
            // Don't reset sessionDuration here - it will be reset after successful Firestore update
        }
        
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null && currentSessionDuration > 0) {
            String userId = currentUser.getUid();
            logUserInfo(currentUser);
            DocumentReference userRef = db.collection("Users").document(userId);
            
            // First check if the user document exists and has basic profile data
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (!documentSnapshot.exists()) {
                    // Create basic user profile if it doesn't exist
                    createUserProfile(currentUser, userRef);
                }
                
                continueWithUsageUpdate(currentUser, userRef, currentSessionDuration);
            }).addOnFailureListener(e -> {
                Log.e("AppUsageTracker", "Failed to check user document: " + e.getMessage());
            });
        }
    }
    
    private void logUserInfo(FirebaseUser user) {
        if (user != null) {
            StringBuilder userInfo = new StringBuilder();
            userInfo.append("User Info:\n");
            userInfo.append("  ID: ").append(user.getUid()).append("\n");
            userInfo.append("  Display Name: ").append(user.getDisplayName()).append("\n");
            userInfo.append("  Email: ").append(user.getEmail()).append("\n");
            userInfo.append("  Provider ID: ").append(user.getProviderId()).append("\n");
            
            Log.d("AppUsageTracker", userInfo.toString());
        }
    }
    
    private void createUserProfile(FirebaseUser user, DocumentReference userRef) {
        Map<String, Object> userData = new HashMap<>();
        
        // Save basic user info
        if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            userData.put("displayName", user.getDisplayName());
        }
        
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            userData.put("email", user.getEmail());
        }
        
        // Extract a username from email if available
        if (user.getEmail() != null && user.getEmail().contains("@")) {
            String username = user.getEmail().substring(0, user.getEmail().indexOf('@'));
            userData.put("username", username);
        }
        
        // Initialize with empty data
        userData.put("totalAppUsage", 0);
        userData.put("currentWeekUsage", 0);
        
        Log.d("AppUsageTracker", "Creating user profile: " + userData.toString());
        
        userRef.set(userData)
            .addOnSuccessListener(aVoid -> {
                Log.d("AppUsageTracker", "User profile created successfully");
            })
            .addOnFailureListener(e -> {
                Log.e("AppUsageTracker", "Failed to create user profile: " + e.getMessage());
            });
    }
    
    /**
     * Get the current session duration in milliseconds
     */
    public long getCurrentSessionDuration() {
        long current = sessionDuration;
        if (isTracking) {
            current += (SystemClock.elapsedRealtime() - startTime);
        }
        return current;
    }
} 