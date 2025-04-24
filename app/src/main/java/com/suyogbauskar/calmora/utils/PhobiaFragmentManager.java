package com.suyogbauskar.calmora.utils;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.suyogbauskar.calmora.utils.ProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to manage phobia-specific fragments and related data
 */
public class PhobiaFragmentManager {

    // Phobia types
    public static final String ACROPHOBIA = "Acrophobia";
    public static final String CLAUSTROPHOBIA = "Claustrophobia";
    public static final String UNKNOWN_PHOBIA = "Unknown";

    // Fragment indices for each phobia type
    public static final int[] ACROPHOBIA_FRAGMENTS = {0, 1, 2, 3, 8, 9, 10, 11, 12}; // Dizziness, Shortness of Breath, Trembling, Anxiety + 5 new height images
    public static final int[] CLAUSTROPHOBIA_FRAGMENTS = {4, 5, 6, 7}; // Mental Distortion, Need to Escape, Suffocation, Nausea

    /**
     * Get the array of fragment indices for a specific phobia type
     * @param phobiaType The type of phobia
     * @return Array of fragment indices
     */
    public static int[] getFragmentIndicesForPhobia(String phobiaType) {
        if (phobiaType.contains("Acrophobia")) {
            return ACROPHOBIA_FRAGMENTS;
        } else if (phobiaType.contains("Claustrophobia")) {
            return CLAUSTROPHOBIA_FRAGMENTS;
        } else {
            // Default to all fragments if phobia type is unknown
            return new int[]{0, 1, 2, 3, 4, 5, 6, 7};
        }
    }

    /**
     * Interface for callback when phobia type is determined
     */
    public interface PhobiaTypeCallback {
        void onPhobiaTypeDetermined(String phobiaType);
    }

    /**
     * Get the user's phobia type from Firestore
     * @param context Context for showing progress dialog
     * @param callback Callback to handle the determined phobia type
     */
    public static void getUserPhobiaType(Context context, PhobiaTypeCallback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ProgressDialog progressDialog = new ProgressDialog();

        if (auth.getCurrentUser() == null) {
            callback.onPhobiaTypeDetermined(UNKNOWN_PHOBIA);
            return;
        }

        // Show loading dialog
        progressDialog.show(context);

        String userId = auth.getCurrentUser().getUid();

        // Get the user's responses from Firestore
        db.collection("Users")
                .document(userId)
                .collection("questions")
                .document("responses")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Hide loading dialog
                    progressDialog.hide();
                    
                    if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                        Map<String, Object> data = documentSnapshot.getData();
                        Map<String, String> responses = new HashMap<>();
                        
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            responses.put(entry.getKey(), entry.getValue().toString());
                        }
                        
                        // Determine phobia type from responses
                        String phobiaType = determinePhobiaType(responses);
                        callback.onPhobiaTypeDetermined(phobiaType);
                    } else {
                        callback.onPhobiaTypeDetermined(UNKNOWN_PHOBIA);
                    }
                })
                .addOnFailureListener(e -> {
                    // Hide loading dialog
                    progressDialog.hide();
                    callback.onPhobiaTypeDetermined(UNKNOWN_PHOBIA);
                });
    }

    /**
     * Determine the phobia type from questionnaire responses
     * @param responses Map of question responses
     * @return The determined phobia type
     */
    private static String determinePhobiaType(Map<String, String> responses) {
        // Get the response for question 1 which identifies the phobia type
        String phobiaResponse = responses.get("question_1");
        
        if (phobiaResponse == null) {
            return UNKNOWN_PHOBIA;
        }
        
        if (phobiaResponse.contains("Heights")) {
            return ACROPHOBIA;
        } else if (phobiaResponse.contains("Enclosed spaces")) {
            return CLAUSTROPHOBIA;
        } else {
            return UNKNOWN_PHOBIA;
        }
    }
} 