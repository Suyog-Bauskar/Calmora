package com.suyogbauskar.calmora;

import com.google.firebase.auth.FirebaseAuth;

import com.suyogbauskar.calmora.utils.ProgressDialog;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private final ProgressDialog progressDialog = new ProgressDialog();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        handleEmailAndPasswordSignUp();
    }

    private void handleEmailAndPasswordSignUp() {
        String email = "b@gmail.com";
        String password = "111111";
        String confirmPassword = "111111";

        if (email.isEmpty()) {
            Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Password and confirm password do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show(SignUpActivity.this);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            progressDialog.hide();
            if (task.isSuccessful()) {
                Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SignUpActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}