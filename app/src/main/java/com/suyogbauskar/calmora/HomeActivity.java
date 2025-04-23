package com.suyogbauskar.calmora;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.suyogbauskar.calmora.fragments.HomeFragment;
import com.suyogbauskar.calmora.fragments.LeaderBoardFragment;
import com.suyogbauskar.calmora.fragments.ProfileFragment;
import com.suyogbauskar.calmora.utils.PhobiaAnalyzer;
import com.suyogbauskar.calmora.utils.ProgressDialog;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class HomeActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    
    // SharedPreferences key for tracking if dialog has been shown
    private static final String PREFS_NAME = "CalmOraPrefs";
    private static final String KEY_DIALOG_SHOWN = "phobia_dialog_shown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        
        // Initialize progress dialog
        progressDialog = new ProgressDialog();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);

        Fragment homeFragment = new HomeFragment();
        Fragment profileFragment = new ProfileFragment();
        Fragment leaderBoardFragment = new LeaderBoardFragment();

        // Set default fragment
        setCurrentFragment(homeFragment);

        // Bottom navigation item selection
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                setCurrentFragment(homeFragment);
            } else if (itemId == R.id.profile) {
                setCurrentFragment(profileFragment);
            } else if (itemId == R.id.leaderBoard) {
                setCurrentFragment(leaderBoardFragment);
            }
            return true;
        });

        // Check if we should show the phobia analysis dialog
        checkAndShowPhobiaDialog();
    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit();
    }

    private void checkAndShowPhobiaDialog() {
        if (auth.getCurrentUser() == null) {
            return; // Not logged in
        }
        
        // Check if dialog has already been shown
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String userId = auth.getCurrentUser().getUid();
        String dialogShownKey = KEY_DIALOG_SHOWN + "_" + userId; // Make it unique per user
        
        if (prefs.getBoolean(dialogShownKey, false)) {
            // Dialog has already been shown to this user
            return;
        }

        // Show loading dialog
        progressDialog.show(this);
        
        // Check for questionnaire data
        db.collection("Users")
                .document(userId)
                .collection("questions")
                .document("responses")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Hide loading dialog
                    progressDialog.hide();
                    
                    if (documentSnapshot.exists()) {
                        // Convert document data to map
                        Map<String, String> responses = new HashMap<>();
                        Map<String, Object> data = documentSnapshot.getData();
                        
                        if (data != null) {
                            for (Map.Entry<String, Object> entry : data.entrySet()) {
                                responses.put(entry.getKey(), entry.getValue().toString());
                            }
                            
                            // Show the analysis dialog
                            showPhobiaAnalysisDialog(responses);
                            
                            // Mark dialog as shown for this user
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean(dialogShownKey, true);
                            editor.apply();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Hide loading dialog
                    progressDialog.hide();
                    Toast.makeText(HomeActivity.this, "Error retrieving questionnaire data", Toast.LENGTH_SHORT).show();
                });
    }

    private void showPhobiaAnalysisDialog(Map<String, String> responses) {
        // Analyze questionnaire responses
        PhobiaAnalyzer.PhobiaAnalysis analysis = PhobiaAnalyzer.analyzeResponses(responses);
        
        // Create and configure the dialog
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_phobia_result);
        dialog.setCancelable(false);
        
        // Set dialog width to match parent
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
        
        // Set text for each view
        TextView tvPhobiaType = dialog.findViewById(R.id.tvPhobiaType);
        TextView tvSeverityLevel = dialog.findViewById(R.id.tvSeverityLevel);
        TextView tvPhobiaDescription = dialog.findViewById(R.id.tvPhobiaDescription);
        TextView tvPhysicalSymptoms = dialog.findViewById(R.id.tvPhysicalSymptoms);
        TextView tvRecommendedTherapy = dialog.findViewById(R.id.tvRecommendedTherapy);
        Button btnStartTherapy = dialog.findViewById(R.id.btnStartTherapy);
        
        tvPhobiaType.setText(analysis.getPhobiaType());
        tvSeverityLevel.setText(analysis.getSeverityLevel());
        tvPhobiaDescription.setText(analysis.getPhobiaDescription());
        tvPhysicalSymptoms.setText(analysis.getPhysicalSymptoms());
        tvRecommendedTherapy.setText(analysis.getRecommendedTherapy());
        
        // Handle button click
        btnStartTherapy.setOnClickListener(v -> {
            // Close the dialog
            dialog.dismiss();
        });
        
        // Show the dialog
        dialog.show();
    }
}
