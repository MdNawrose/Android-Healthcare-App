package com.example.mediapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    private EditText editTextName, editTextLocation, editTextAge, editTextMobile, editTextEmail, editTextPassword;
    private Button buttonRegister;
    private TextView textLoginRedirect;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Link UI elements
        editTextName = findViewById(R.id.editTextName);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextAge = findViewById(R.id.editTextAge);
        editTextMobile = findViewById(R.id.editTextMobile);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        textLoginRedirect = findViewById(R.id.textLoginRedirect);

        // Handle register button click
        buttonRegister.setOnClickListener(v -> registerUser());

        // Redirect to login screen
        textLoginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = editTextName.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String ageString = editTextAge.getText().toString().trim();
        String mobile = editTextMobile.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Name is required.");
            return;
        }
        if (TextUtils.isEmpty(location)) {
            editTextLocation.setError("Location is required.");
            return;
        }
        if (TextUtils.isEmpty(ageString)) {
            editTextAge.setError("Age is required.");
            return;
        }
        if (TextUtils.isEmpty(mobile)) {
            editTextMobile.setError("Mobile number is required.");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required.");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required.");
            return;
        }
        if (password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters.");
            return;
        }

        // Convert age to integer
        int age;
        try {
            age = Integer.parseInt(ageString);
        } catch (NumberFormatException e) {
            editTextAge.setError("Invalid age format.");
            return;
        }

        // Register user via Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String userId = authResult.getUser().getUid();

                    // Save user data to Firestore
                    saveUserToFirestore(userId, name, location, age, mobile, email);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Registration Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void saveUserToFirestore(String userId, String name, String location, int age, String mobile, String email) {
        User user = new User(userId, name, location, age, mobile, email);

        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class)); // Redirect to main screen
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save user data!", Toast.LENGTH_SHORT).show());
    }
}
