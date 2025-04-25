package com.suyogbauskar.calmora.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.suyogbauskar.calmora.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LeaderBoardFragment extends Fragment {

    private BarChart barChart;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private LinearLayout user1Layout, user2Layout, user3Layout;
    private TextView user1Name, user2Name, user3Name, user1Time, user2Time, user3Time;
    private Map<Long, Float> dailyUsageData = new HashMap<>();

    public LeaderBoardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leader_board, container, false);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize UI components
        barChart = view.findViewById(R.id.barChart);
        
        // Find leaderboard UI components
        user1Layout = view.findViewById(R.id.user1Layout);
        user2Layout = view.findViewById(R.id.user2Layout);
        user3Layout = view.findViewById(R.id.user3Layout);
        
        if (user1Layout == null || user2Layout == null || user3Layout == null) {
            Log.e("LeaderBoardFragment", "Failed to find user layouts!");
            logViewHierarchy(view);
            Toast.makeText(getContext(), "Error initializing leaderboard UI", Toast.LENGTH_SHORT).show();
            return view;
        }
        
        user1Name = view.findViewById(R.id.user1Name);
        user2Name = view.findViewById(R.id.user2Name);
        user3Name = view.findViewById(R.id.user3Name);
        
        user1Time = view.findViewById(R.id.user1Time);
        user2Time = view.findViewById(R.id.user2Time);
        user3Time = view.findViewById(R.id.user3Time);
        
        // Check if TextViews were found directly
        if (user1Name == null || user2Name == null || user3Name == null || 
            user1Time == null || user2Time == null || user3Time == null) {
            
            // Try to find them inside the layouts
            if (user1Layout != null) user1Name = user1Layout.findViewById(R.id.user1Name);
            if (user1Layout != null) user1Time = user1Layout.findViewById(R.id.user1Time);
            
            if (user2Layout != null) user2Name = user2Layout.findViewById(R.id.user2Name);
            if (user2Layout != null) user2Time = user2Layout.findViewById(R.id.user2Time);
            
            if (user3Layout != null) user3Name = user3Layout.findViewById(R.id.user3Name);
            if (user3Layout != null) user3Time = user3Layout.findViewById(R.id.user3Time);
            
            Log.d("LeaderBoardFragment", "Tried to find TextViews inside layouts: " +
                   "user1Name=" + user1Name + ", user1Time=" + user1Time);
        }

        // Load user's weekly data
        loadWeeklyData();
        
        // Load top users
        loadTopUsers();

        return view;
    }

    private void loadWeeklyData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();
        db.collection("Users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        extractDailyUsageData(documentSnapshot);
                        setupBarChart();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load weekly data", Toast.LENGTH_SHORT).show();
                });
    }

    private void extractDailyUsageData(DocumentSnapshot document) {
        dailyUsageData.clear();
        
        // Initialize all days of the week with zero values
        for (int dayOfWeek = 1; dayOfWeek <= 7; dayOfWeek++) {
            dailyUsageData.put((long) dayOfWeek, 0f);
        }
        
        // Get the data from document
        Map<String, Object> data = document.getData();
        if (data != null) {
            // Extract day-specific data
            for (int dayOfWeek = 1; dayOfWeek <= 7; dayOfWeek++) {
                String dayKey = "day_" + dayOfWeek;
                if (data.containsKey(dayKey)) {
                    Object value = data.get(dayKey);
                    Float usageValue = 0f;
                    
                    if (value instanceof Long) {
                        usageValue = ((Long) value).floatValue();
                    } else if (value instanceof Double) {
                        usageValue = ((Double) value).floatValue();
                    } else if (value instanceof Integer) {
                        usageValue = ((Integer) value).floatValue();
                    } else if (value != null) {
                        try {
                            usageValue = Float.parseFloat(value.toString());
                        } catch (NumberFormatException e) {
                            // Keep default value of 0
                        }
                    }
                    
                    dailyUsageData.put((long) dayOfWeek, usageValue);
                }
            }
        }
    }

    private void setupBarChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        final String[] days = {"S", "M", "T", "W", "T", "F", "S"};
        
        // Create entries for each day of the week
        for (int i = 0; i < 7; i++) {
            // Convert chart position to day of week (1-7)
            int dayOfWeek = i + 1;
            float value = dailyUsageData.getOrDefault((long) dayOfWeek, 0f);
            entries.add(new BarEntry(i, value));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Weekly Progress");
        dataSet.setColor(Color.parseColor("#4CAF50"));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        // Customize X-Axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < days.length) {
                    return days[index];
                } else {
                    return "";
                }
            }
        });

        barChart.invalidate(); // Refresh chart
    }

    private void loadTopUsers() {
        // Get current week identifier
        Calendar calendar = Calendar.getInstance();
        int weekNumber = calendar.get(Calendar.WEEK_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);
        String currentWeekId = "week_" + year + "_" + weekNumber;
        
        // Query for users with current week data first
        db.collection("Users")
                .whereEqualTo("currentWeekId", currentWeekId)
                .orderBy("currentWeekUsage", Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Process results as before
                        List<UserData> topUsers = extractUserData(queryDocumentSnapshots);
                        updateTopUsersUI(topUsers);
                    } else {
                        // If no users found with current week ID, fall back to all users
                        loadTopUsersWithoutWeekFilter();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load top users: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    loadTopUsersWithoutWeekFilter(); // Try fallback on failure
                });
    }
    
    private void loadTopUsersWithoutWeekFilter() {
        // Fallback query without week filter
        db.collection("Users")
                .orderBy("currentWeekUsage", Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<UserData> topUsers = extractUserData(queryDocumentSnapshots);
                    
                    if (topUsers.isEmpty()) {
                        // If still no results, create placeholder data
                        createPlaceholderData();
                    } else {
                        updateTopUsersUI(topUsers);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load any users: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    createPlaceholderData();
                });
    }
    
    private List<UserData> extractUserData(QuerySnapshot queryDocumentSnapshots) {
        List<UserData> topUsers = new ArrayList<>();
        
        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
            String userId = documentSnapshot.getId();
            
            // Try multiple fields to get a proper user name
            String displayName = null;
            
            // First check displayName field
            if (documentSnapshot.contains("displayName")) {
                displayName = documentSnapshot.getString("displayName");
            }
            
            // Then check name field
            if ((displayName == null || displayName.isEmpty()) && documentSnapshot.contains("name")) {
                displayName = documentSnapshot.getString("name");
            }
            
            // Then check fullName field
            if ((displayName == null || displayName.isEmpty()) && documentSnapshot.contains("fullName")) {
                displayName = documentSnapshot.getString("fullName");
            }
            
            // Then try email as fallback
            if (displayName == null || displayName.isEmpty()) {
                displayName = documentSnapshot.getString("email");
                if (displayName != null && displayName.contains("@")) {
                    // Extract username from email
                    displayName = displayName.substring(0, displayName.indexOf('@'));
                }
            }
            
            // Final fallback using username field
            if ((displayName == null || displayName.isEmpty()) && documentSnapshot.contains("username")) {
                displayName = documentSnapshot.getString("username");
            }
            
            // If we still have no name, try user ID
            if (displayName == null || displayName.isEmpty()) {
                // Try to get a reasonable name from the userId if it's an email format
                if (userId.contains("@")) {
                    displayName = userId.substring(0, userId.indexOf('@'));
                } else {
                    // Last resort placeholder
                    displayName = "Player " + (topUsers.size() + 1);
                }
            }
            
            // Get current week usage with fallback to 0
            Object weekUsageObj = documentSnapshot.get("currentWeekUsage");
            long weekUsage = 0;
            if (weekUsageObj instanceof Long) {
                weekUsage = (Long) weekUsageObj;
            } else if (weekUsageObj instanceof Double) {
                weekUsage = ((Double) weekUsageObj).longValue();
            } else if (weekUsageObj instanceof Integer) {
                weekUsage = ((Integer) weekUsageObj).longValue();
            } else if (weekUsageObj != null) {
                try {
                    weekUsage = Long.parseLong(weekUsageObj.toString());
                } catch (NumberFormatException e) {
                    // Use default value of 0
                }
            }
            
            // Ensure we have data for the current user in Firestore
            ensureUserWeeklyFields(userId);
            
            // Log user data for debugging
            Log.d("LeaderBoardFragment", "User data: id=" + userId + ", name=" + displayName + ", usage=" + weekUsage);
            logDocumentFields(documentSnapshot);
            
            UserData userData = new UserData(userId, displayName, weekUsage);
            topUsers.add(userData);
        }
        
        return topUsers;
    }
    
    private void createPlaceholderData() {
        // Create placeholder data if no users found
        List<UserData> placeholders = new ArrayList<>();
        
        // Current user as first placeholder if available
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String name = currentUser.getDisplayName();
            if (name == null || name.isEmpty()) {
                String email = currentUser.getEmail();
                if (email != null && email.contains("@")) {
                    name = email.substring(0, email.indexOf('@'));
                } else {
                    name = "You";
                }
            }
            
            placeholders.add(new UserData(currentUser.getUid(), name, 0));
            
            // Ensure we have data for the current user in Firestore
            ensureUserWeeklyFields(currentUser.getUid());
        }
        
        // Add other placeholder positions if needed
        while (placeholders.size() < 3) {
            placeholders.add(new UserData("placeholder_" + placeholders.size(), 
                    "Future Star " + (placeholders.size() + 1), 0));
        }
        
        updateTopUsersUI(placeholders);
    }
    
    /**
     * Ensures user has the required fields for weekly tracking
     */
    private void ensureUserWeeklyFields(String userId) {
        // Get current week identifier
        Calendar calendar = Calendar.getInstance();
        int weekNumber = calendar.get(Calendar.WEEK_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);
        String currentWeekId = "week_" + year + "_" + weekNumber;
        
        // Create a map with default values for weekly fields
        Map<String, Object> defaultFields = new HashMap<>();
        defaultFields.put("currentWeekId", currentWeekId);
        
        // Only set these fields if they don't exist
        for (int i = 1; i <= 7; i++) {
            String dayKey = "day_" + i;
            defaultFields.put(dayKey, FieldValue.increment(0));
        }
        
        defaultFields.put("currentWeekUsage", FieldValue.increment(0));
        defaultFields.put("totalAppUsage", FieldValue.increment(0));
        
        // Update the document, creating fields if they don't exist
        db.collection("Users").document(userId)
                .set(defaultFields, SetOptions.merge())
                .addOnFailureListener(e -> {
                    // Just log the error, don't show to user
                    Log.e("LeaderBoardFragment", "Failed to ensure weekly fields: " + e.getMessage());
                });
    }
    
    private void updateTopUsersUI(List<UserData> topUsers) {
        // Make sure UI components are properly initialized
        if (getView() == null || user1Layout == null || user2Layout == null || user3Layout == null) {
            return;
        }
        
        // Always ensure we have 3 items
        while (topUsers.size() < 3) {
            topUsers.add(new UserData("empty", "---", 0));
        }
        
        if (topUsers.size() >= 1) {
            UserData user1 = topUsers.get(0);
            user1Name.setText(user1.displayName);
            user1Time.setText(formatUsageTime(user1.averageUsage));
        }

        if (topUsers.size() >= 2) {
            UserData user2 = topUsers.get(1);
            user2Name.setText(user2.displayName);
            user2Time.setText(formatUsageTime(user2.averageUsage));
        }

        if (topUsers.size() >= 3) {
            UserData user3 = topUsers.get(2);
            user3Name.setText(user3.displayName);
            user3Time.setText(formatUsageTime(user3.averageUsage));
        }
    }

    private String formatUsageTime(long minutes) {
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;
        
        return hours + "h " + remainingMinutes + "m";
    }

    private long getStartOfDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    // Simple data class to hold user information
    private static class UserData {
        String userId;
        String displayName;
        long averageUsage;

        public UserData(String userId, String displayName, long averageUsage) {
            this.userId = userId;
            this.displayName = displayName;
            this.averageUsage = averageUsage;
        }
    }

    /**
     * Debug helper to log all fields in a document
     */
    private void logDocumentFields(DocumentSnapshot document) {
        if (document != null && document.exists()) {
            Map<String, Object> data = document.getData();
            if (data != null) {
                StringBuilder fieldsInfo = new StringBuilder();
                fieldsInfo.append("Document fields for ").append(document.getId()).append(":\n");
                
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    String fieldName = entry.getKey();
                    Object fieldValue = entry.getValue();
                    String valueString = (fieldValue == null) ? "null" : fieldValue.toString();
                    fieldsInfo.append("  ").append(fieldName).append(" = ").append(valueString).append("\n");
                }
                
                Log.d("LeaderBoardFragment", fieldsInfo.toString());
            } else {
                Log.d("LeaderBoardFragment", "Document " + document.getId() + " has no data");
            }
        } else {
            Log.d("LeaderBoardFragment", "Document is null or doesn't exist");
        }
    }

    /**
     * Debug method to log the view hierarchy
     */
    private void logViewHierarchy(View view) {
        if (view == null) {
            Log.e("LeaderBoardFragment", "View is null!");
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("View hierarchy:\n");
        logViewDetails(view, sb, 0);
        Log.d("LeaderBoardFragment", sb.toString());
    }
    
    private void logViewDetails(View view, StringBuilder sb, int depth) {
        // Add indentation
        for (int i = 0; i < depth; i++) {
            sb.append("  ");
        }
        
        // Log view details
        sb.append(view.getClass().getSimpleName());
        if (view.getId() != View.NO_ID) {
            try {
                sb.append(" id=").append(view.getResources().getResourceEntryName(view.getId()));
            } catch (Exception e) {
                sb.append(" id=").append(view.getId());
            }
        }
        sb.append("\n");
        
        // Recurse for ViewGroups
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                logViewDetails(group.getChildAt(i), sb, depth + 1);
            }
        }
    }
}
