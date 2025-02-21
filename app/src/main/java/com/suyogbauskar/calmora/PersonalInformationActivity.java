package com.suyogbauskar.calmora;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import com.suyogbauskar.calmora.POJOs.User;
import com.suyogbauskar.calmora.utils.ProgressDialog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PersonalInformationActivity extends AppCompatActivity {

    private EditText nameEditText;
    private RadioGroup ageGroup, genderGroup;
    private FirebaseFirestore db;
    private FirebaseUser mUser;

    private final ProgressDialog progressDialog = new ProgressDialog();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personal_information);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        nameEditText = findViewById(R.id.nameEditText);
        ageGroup = findViewById(R.id.ageGroup);
        genderGroup = findViewById(R.id.genderGroup);
        AppCompatButton saveBtn = findViewById(R.id.saveBtn);

        saveBtn.setOnClickListener(view -> handleSaveButtonClick());
    }

    private void handleSaveButtonClick() {
        int ageGroupId = ageGroup.getCheckedRadioButtonId();
        int genderGroupId = genderGroup.getCheckedRadioButtonId();
        String nameDB = nameEditText.getText().toString().trim();
        int groupDB = -1;
        String genderDB;

        if (nameDB.isEmpty()) {
            Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ageGroupId == -1) {
            Toast.makeText(this, "Select age group", Toast.LENGTH_SHORT).show();
            return;
        }
        if (genderGroupId == -1) {
            Toast.makeText(this, "Select gender", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton ageRadioButton = findViewById(ageGroupId);
        RadioButton genderRadioButton = findViewById(genderGroupId);

        if (ageRadioButton.getText().toString().equals("06 - 12")) {
            groupDB = 1;
        } else if (ageRadioButton.getText().toString().equals("13 - 19")) {
            groupDB = 2;
        } else if (ageRadioButton.getText().toString().equals("20 - 35")) {
            groupDB = 3;
        } else if (ageRadioButton.getText().toString().equals("36+")) {
            groupDB = 4;
        }

        genderDB = genderRadioButton.getText().toString().toLowerCase();
        User user = new User(nameDB, genderDB, groupDB);

        progressDialog.show(PersonalInformationActivity.this);
        db.collection("Users").document(mUser.getUid()).set(user)
                .addOnCompleteListener(task -> {
                    progressDialog.hide();
                    if (task.isSuccessful()) {
                        startActivity(new Intent(PersonalInformationActivity.this, QuestionsActivity.class));
                    } else {
                        Toast.makeText(PersonalInformationActivity.this, "Save failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}