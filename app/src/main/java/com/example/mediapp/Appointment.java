package com.example.mediapp;

public class Appointment {
    private String userId;
    private String userName;
    private String userPhone;
    private String userLocation;
    private String doctorId;
    private String doctorName;
    private String dateTime;

    // Required for Firestore deserialization
    public Appointment() {}

    public Appointment(String userId, String userName, String userPhone, String userLocation,
                       String doctorId, String doctorName, String dateTime) {
        this.userId = userId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userLocation = userLocation;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.dateTime = dateTime;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getUserPhone() { return userPhone; }
    public String getUserLocation() { return userLocation; }
    public String getDoctorId() { return doctorId; }
    public String getDoctorName() { return doctorName; }
    public String getDateTime() { return dateTime; }

    // Setters
    public void setUserId(String userId) { this.userId = userId; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }
    public void setUserLocation(String userLocation) { this.userLocation = userLocation; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }
}
