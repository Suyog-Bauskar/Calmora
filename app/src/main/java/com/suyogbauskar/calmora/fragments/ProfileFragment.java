package com.suyogbauskar.calmora.fragments;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.suyogbauskar.calmora.POJOs.User;
import com.suyogbauskar.calmora.R;
import com.suyogbauskar.calmora.utils.ProgressDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // UI elements
    private EditText nameEditText;
    private RadioGroup ageGroup, genderGroup;
    private TextView selectedTimeText;
    private Button selectTimeButton;
    private CheckBox[] dayCheckboxes = new CheckBox[7];
    
    // Firebase
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    
    // Preferences
    private SharedPreferences preferences;
    private final String PREFS_NAME = "CalmoraPrefs";
    private final String NOTIFICATION_TIME = "notification_time";
    private final String DAYS_PREFIX = "therapy_day_";
    
    private final ProgressDialog progressDialog = new ProgressDialog();

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        
        // Initialize SharedPreferences
        preferences = requireActivity().getSharedPreferences(PREFS_NAME, 0);
        
        // Initialize UI elements
        initializeViews(view);
        
        // Load user data
        loadUserData();
        
        // Set up listeners
        setupListeners();
    }
    
    private void initializeViews(View view) {
        nameEditText = view.findViewById(R.id.profileNameEditText);
        ageGroup = view.findViewById(R.id.profileAgeGroup);
        genderGroup = view.findViewById(R.id.profileGenderGroup);
        selectedTimeText = view.findViewById(R.id.profileSelectedTimeText);
        selectTimeButton = view.findViewById(R.id.profileSelectTimeButton);
        
        // Initialize checkboxes
        dayCheckboxes[0] = view.findViewById(R.id.checkBoxMonday);
        dayCheckboxes[1] = view.findViewById(R.id.checkBoxTuesday);
        dayCheckboxes[2] = view.findViewById(R.id.checkBoxWednesday);
        dayCheckboxes[3] = view.findViewById(R.id.checkBoxThursday);
        dayCheckboxes[4] = view.findViewById(R.id.checkBoxFriday);
        dayCheckboxes[5] = view.findViewById(R.id.checkBoxSaturday);
        dayCheckboxes[6] = view.findViewById(R.id.checkBoxSunday);
        
        // Save button
        Button saveButton = view.findViewById(R.id.profileSaveBtn);
        saveButton.setOnClickListener(v -> saveUserProfile());
    }
    
    private void loadUserData() {
        if (currentUser == null) return;
        
        progressDialog.show(requireActivity());
        
        // Load user data from Firestore
        db.collection("Users").document(currentUser.getUid()).get()
            .addOnCompleteListener(task -> {
                progressDialog.hide();
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Set name
                        String name = document.getString("name");
                        if (name != null) {
                            nameEditText.setText(name);
                        }
                        
                        // Set age group
                        Long ageGroupValue = document.getLong("ageGroup");
                        if (ageGroupValue != null) {
                            int group = ageGroupValue.intValue();
                            switch (group) {
                                case 1:
                                    ageGroup.check(R.id.profileGroup1);
                                    break;
                                case 2:
                                    ageGroup.check(R.id.profileGroup2);
                                    break;
                                case 3:
                                    ageGroup.check(R.id.profileGroup3);
                                    break;
                                case 4:
                                    ageGroup.check(R.id.profileGroup4);
                                    break;
                            }
                        }
                        
                        // Set gender
                        String gender = document.getString("gender");
                        if (gender != null) {
                            if (gender.equalsIgnoreCase("male")) {
                                genderGroup.check(R.id.profileMale);
                            } else if (gender.equalsIgnoreCase("female")) {
                                genderGroup.check(R.id.profileFemale);
                            } else if (gender.equalsIgnoreCase("others")) {
                                genderGroup.check(R.id.profileOthers);
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            });
        
        // Load notification time preference
        String savedTime = preferences.getString(NOTIFICATION_TIME, "");
        if (!savedTime.isEmpty()) {
            selectedTimeText.setText(savedTime);
        }
        
        // Load therapy days preferences
        for (int i = 0; i < dayCheckboxes.length; i++) {
            boolean isChecked = preferences.getBoolean(DAYS_PREFIX + i, false);
            dayCheckboxes[i].setChecked(isChecked);
        }
    }
    
    private void setupListeners() {
        // Time picker listener
        selectTimeButton.setOnClickListener(v -> showTimePickerDialog());
    }
    
    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        
        TimePickerDialog timePickerDialog = new TimePickerDialog(
            requireContext(),
            (view, hourOfDay, selectedMinute) -> {
                // Format time as HH:MM AM/PM
                String amPm = hourOfDay >= 12 ? "PM" : "AM";
                int hour12Format = hourOfDay > 12 ? hourOfDay - 12 : (hourOfDay == 0 ? 12 : hourOfDay);
                String timeString = String.format("%02d:%02d %s", hour12Format, selectedMinute, amPm);
                
                // Display selected time
                selectedTimeText.setText(timeString);
            },
            hour,
            minute,
            false // 12-hour format
        );
        
        timePickerDialog.show();
    }
    
    private void saveUserProfile() {
        if (currentUser == null) return;
        
        // Validate inputs
        String name = nameEditText.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }
        
        int selectedAgeGroupId = ageGroup.getCheckedRadioButtonId();
        if (selectedAgeGroupId == -1) {
            Toast.makeText(requireContext(), "Please select an age group", Toast.LENGTH_SHORT).show();
            return;
        }
        
        int selectedGenderId = genderGroup.getCheckedRadioButtonId();
        if (selectedGenderId == -1) {
            Toast.makeText(requireContext(), "Please select your gender", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Get age group value
        int ageGroupValue = 0;
        RadioButton selectedAgeButton = requireView().findViewById(selectedAgeGroupId);
        String ageText = selectedAgeButton.getText().toString();
        if (ageText.equals("06 - 12")) {
            ageGroupValue = 1;
        } else if (ageText.equals("13 - 19")) {
            ageGroupValue = 2;
        } else if (ageText.equals("20 - 35")) {
            ageGroupValue = 3;
        } else if (ageText.equals("36+")) {
            ageGroupValue = 4;
        }
        
        // Get gender value
        RadioButton selectedGenderButton = requireView().findViewById(selectedGenderId);
        String gender = selectedGenderButton.getText().toString().toLowerCase();
        
        // Create map of fields to update
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("gender", gender);
        updates.put("ageGroup", ageGroupValue);
        
        progressDialog.show(requireActivity());
        
        // First check if the document exists
        db.collection("Users").document(currentUser.getUid()).get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // Document exists, use update to preserve existing fields
                    db.collection("Users").document(currentUser.getUid())
                        .update(updates)
                        .addOnCompleteListener(task -> handleProfileUpdateResult(task));
                } else {
                    // Document doesn't exist, create it with set and add default values for leaderboard
                    addLeaderboardFields(updates);
                    db.collection("Users").document(currentUser.getUid())
                        .set(updates)
                        .addOnCompleteListener(task -> handleProfileUpdateResult(task));
                }
            })
            .addOnFailureListener(e -> {
                // Error checking document, try to update anyway
                progressDialog.hide();
                Toast.makeText(requireContext(), "Error checking profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
    
    /**
     * Adds default leaderboard fields to a new user document
     */
    private void addLeaderboardFields(Map<String, Object> userData) {
        // Add current week identifier
        Calendar calendar = Calendar.getInstance();
        int weekNumber = calendar.get(Calendar.WEEK_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);
        String weekKey = "week_" + year + "_" + weekNumber;
        userData.put("currentWeekId", weekKey);
        
        // Initialize daily usage fields
        for (int i = 1; i <= 7; i++) {
            userData.put("day_" + i, 0);
        }
        
        // Initialize total fields
        userData.put("currentWeekUsage", 0);
        userData.put("totalAppUsage", 0);
    }
    
    /**
     * Handles the result of profile update operation
     */
    private void handleProfileUpdateResult(com.google.android.gms.tasks.Task<?> task) {
        progressDialog.hide();
        if (task.isSuccessful()) {
            // Save notification time to SharedPreferences
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(NOTIFICATION_TIME, selectedTimeText.getText().toString());
            
            // Save therapy days to SharedPreferences
            for (int i = 0; i < dayCheckboxes.length; i++) {
                editor.putBoolean(DAYS_PREFIX + i, dayCheckboxes[i].isChecked());
            }
            
            editor.apply();
            
            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Failed to update profile: " + 
                          (task.getException() != null ? task.getException().getMessage() : "Unknown error"), 
                          Toast.LENGTH_SHORT).show();
        }
    }
}