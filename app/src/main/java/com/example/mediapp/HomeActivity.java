package com.example.mediapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    Button pharmacyButton, topDoctorsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        pharmacyButton = findViewById(R.id.pharmacyButton);
        topDoctorsButton = findViewById(R.id.topDoctorsButton);

        pharmacyButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, PharmacyActivity.class);
            startActivity(intent);
        });

        topDoctorsButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, DoctorActivity.class);
            startActivity(intent);
        });
    }
}
