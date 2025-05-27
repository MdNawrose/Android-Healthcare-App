package com.example.mediapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DoctorActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Doctor> doctorList = new ArrayList<>();
    private DoctorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Pass context & doctor list to DoctorAdapter
        adapter = new DoctorAdapter(this, doctorList);
        recyclerView.setAdapter(adapter);

        loadDoctors();
    }

    private void loadDoctors() {
        FirebaseFirestore.getInstance().collection("doctor")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    doctorList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Doctor doctor = doc.toObject(Doctor.class);
                        doctor.setId(doc.getId()); // 🔥 Assign Firestore document ID
                        doctorList.add(doctor);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

}

