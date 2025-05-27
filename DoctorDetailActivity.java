package com.example.mediapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DoctorDetailActivity extends AppCompatActivity {

    private ImageView doctorImage;
    private TextView doctorName, doctorSpecialty, doctorRating, doctorDistance, doctorDescription, selectedDateTime;
    private Button bookButton;
    private Doctor doctor;
    private Calendar appointmentCalendar = Calendar.getInstance();
    private FirebaseFirestore db;
    private String doctorId, doctorNameStr, userId;
    private Map<String, Object> appointment = new HashMap<>();

    private Uri pdfUri;

    private final ActivityResultLauncher<Intent> createPdfLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    pdfUri = result.getData().getData();
                    writePdfToUri(pdfUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_detail);

        doctor = (Doctor) getIntent().getSerializableExtra("doctor");
        if (doctor == null) {
            Toast.makeText(this, "Error: Doctor details not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        db = FirebaseFirestore.getInstance();
        doctorId = getIntent().getStringExtra("doctorId");
        doctorNameStr = getIntent().getStringExtra("doctorName");
        userId = FirebaseAuth.getInstance().getUid();

        doctorImage = findViewById(R.id.doctorImage);
        doctorName = findViewById(R.id.doctorName);
        doctorSpecialty = findViewById(R.id.doctorSpecialty);
        doctorRating = findViewById(R.id.doctorRating);
        doctorDistance = findViewById(R.id.doctorDistance);
        doctorDescription = findViewById(R.id.doctorDescription);
        selectedDateTime = findViewById(R.id.selectedDateTime);
        bookButton = findViewById(R.id.bookAppointmentButton);

        doctorName.setText(doctor.getName());
        doctorSpecialty.setText(doctor.getSpecialty());
        doctorRating.setText("\u2605 " + doctor.getRating());
        doctorDistance.setText(doctor.getDistance());
        doctorDescription.setText(doctor.getDescription());

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getAssets().open(doctor.getImage()));
            doctorImage.setImageBitmap(bitmap);
        } catch (IOException e) {
            doctorImage.setImageResource(R.drawable.placeholder_image);
        }

        selectedDateTime.setOnClickListener(v -> showDatePicker());
        bookButton.setOnClickListener(v -> checkAndBookAppointment());
    }

    private void showDatePicker() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            appointmentCalendar.set(Calendar.YEAR, year);
            appointmentCalendar.set(Calendar.MONTH, month);
            appointmentCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            showTimePicker();
        },
                appointmentCalendar.get(Calendar.YEAR),
                appointmentCalendar.get(Calendar.MONTH),
                appointmentCalendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void showTimePicker() {
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            appointmentCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            appointmentCalendar.set(Calendar.MINUTE, minute);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            selectedDateTime.setText(format.format(appointmentCalendar.getTime()));
        },
                appointmentCalendar.get(Calendar.HOUR_OF_DAY),
                appointmentCalendar.get(Calendar.MINUTE),
                false
        ).show();
    }

    private void checkAndBookAppointment() {
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(appointmentCalendar.getTime());

        db.collection("appointment")
                .whereEqualTo("doctorId", doctorId)
                .whereEqualTo("dateTime", formattedDate)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        retrieveUserDetailsAndBook(userId, doctorId, doctorNameStr, formattedDate);
                    } else {
                        Toast.makeText(this, "This time slot is already booked. Please choose another.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error checking availability", Toast.LENGTH_SHORT).show());
    }

    private void retrieveUserDetailsAndBook(String userId, String doctorId, String doctorName, String dateTime) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userName = documentSnapshot.getString("name");
                        String userPhone = documentSnapshot.getString("mobile");
                        String userLocation = documentSnapshot.getString("location");

                        saveAppointmentToFirestore(userId, userName, userPhone, userLocation, doctorId, doctorName, dateTime);
                    } else {
                        Toast.makeText(this, "Error: User details not found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error retrieving user data", Toast.LENGTH_SHORT).show());
    }

    private void saveAppointmentToFirestore(String userId, String userName, String userPhone, String userLocation,
                                            String doctorId, String doctorName, String dateTime) {

        appointment.clear();
        appointment.put("userId", userId);
        appointment.put("userName", userName);
        appointment.put("userPhone", userPhone);
        appointment.put("userLocation", userLocation);
        appointment.put("doctorId", doctorId);
        appointment.put("doctorName", doctorName);
        appointment.put("dateTime", dateTime);

        db.collection("appointment")
                .add(appointment)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Appointment Booked!", Toast.LENGTH_SHORT).show();
                    promptUserToSavePdf();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Booking Failed", Toast.LENGTH_SHORT).show());
    }

    private void promptUserToSavePdf() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "appointment.pdf");
        createPdfLauncher.launch(intent);
    }

    private void writePdfToUri(Uri uri) {
        if (uri == null || appointment.isEmpty()) return;

        PdfDocument pdf = new PdfDocument();
        PdfDocument.PageInfo info = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = pdf.startPage(info);

        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setTextSize(12);

        page.getCanvas().drawText("Appointment Confirmation", 10, 30, paint);
        page.getCanvas().drawText("Doctor Name: " + appointment.get("doctorName"), 10, 60, paint);
        page.getCanvas().drawText("Date & Time: " + appointment.get("dateTime"), 10, 90, paint);
        page.getCanvas().drawText("User Name: " + appointment.get("userName"), 10, 120, paint);
        page.getCanvas().drawText("User Phone: " + appointment.get("userPhone"), 10, 150, paint);
        page.getCanvas().drawText("User Location: " + appointment.get("userLocation"), 10, 180, paint);

        pdf.finishPage(page);

        try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
            if (outputStream != null) {
                pdf.writeTo(outputStream);
                Toast.makeText(this, "PDF saved successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Unable to open output stream.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Failed to write PDF.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            pdf.close();
        }
    }
}
