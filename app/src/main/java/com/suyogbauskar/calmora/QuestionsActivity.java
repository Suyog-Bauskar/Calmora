package com.suyogbauskar.calmora;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.suyogbauskar.calmora.utils.ProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionsActivity extends AppCompatActivity {
    private AppCompatButton nextbtn;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private List<RadioGroup> radioGroups = new ArrayList<>();
    private ProgressDialog progressDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questions);
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
        
        // Find and collect all RadioGroups
        findAllRadioGroups();
        
        nextbtn = findViewById(R.id.nextBtn);
        nextbtn.setOnClickListener(view -> handleNextButtonClick());
    }

    private void findAllRadioGroups() {
        // Get the ScrollView 
        ScrollView scrollView = findViewById(R.id.scrollView);
        if (scrollView == null) {
            // Get all ScrollViews in the layout if ID is not set
            ViewGroup rootView = (ViewGroup) findViewById(R.id.main);
            for (int i = 0; i < rootView.getChildCount(); i++) {
                View child = rootView.getChildAt(i);
                if (child instanceof ScrollView) {
                    scrollView = (ScrollView) child;
                    break;
                }
            }
        }
        
        if (scrollView != null) {
            // Get the LinearLayout inside ScrollView (first child)
            ViewGroup linearLayout = (ViewGroup) scrollView.getChildAt(0);
            
            // Find all RadioGroups by traversing the view hierarchy
            findRadioGroupsRecursively(linearLayout);
        }
    }
    
    private void findRadioGroupsRecursively(ViewGroup parent) {
        // Check all children of the parent view
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            
            if (child instanceof RadioGroup) {
                // Found a RadioGroup
                radioGroups.add((RadioGroup) child);
            } else if (child instanceof ViewGroup) {
                // Recursively check children of this ViewGroup
                findRadioGroupsRecursively((ViewGroup) child);
            }
        }
    }

    private void handleNextButtonClick() {
        if (validateAllQuestionsAnswered()) {
            saveResponsesToFirestore();
        } else {
            Toast.makeText(this, "Please answer all questions", Toast.LENGTH_SHORT).show();
        }
    }
    
    private boolean validateAllQuestionsAnswered() {
        if (radioGroups.size() < 15) {
            Toast.makeText(this, "Error finding all questions. Please try again.", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        for (RadioGroup radioGroup : radioGroups) {
            if (radioGroup.getCheckedRadioButtonId() == -1) {
                // No option selected for this question
                return false;
            }
        }
        return true;
    }
    
    private void saveResponsesToFirestore() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "You must be logged in to continue", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show loading dialog
        progressDialog.show(this);
        
        String userId = auth.getCurrentUser().getUid();
        Map<String, String> answers = new HashMap<>();
        
        // Collect all answers
        for (int i = 0; i < radioGroups.size(); i++) {
            RadioGroup radioGroup = radioGroups.get(i);
            int selectedId = radioGroup.getCheckedRadioButtonId();
            
            // Get the selected radio button's text
            RadioButton selectedRadioButton = findViewById(selectedId);
            String answer = selectedRadioButton.getText().toString();
            
            // Store question number and answer
            answers.put("question_" + (i + 1), answer);
        }
        
        // Save to Firestore
        db.collection("Users")
            .document(userId)
            .collection("questions")
            .document("responses")
            .set(answers)
            .addOnSuccessListener(aVoid -> {
                // Hide loading dialog
                progressDialog.hide();
                startActivity(new Intent(QuestionsActivity.this, HomeActivity.class));
                finish(); // Close this activity
            })
            .addOnFailureListener(e -> {
                // Hide loading dialog
                progressDialog.hide();
                Toast.makeText(QuestionsActivity.this, "Failed to save responses: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
            });
    }
}