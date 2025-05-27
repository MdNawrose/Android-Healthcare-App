package com.example.mediapp;

public class User {
    private String userId;
    private String name;
    private String location;
    private int age;
    private String mobile;
    private String email;

    // 🔥 Empty constructor (Required for Firestore)
    public User() {}

    // 🔥 Constructor with fields
    public User(String userId, String name, String location, int age, String mobile, String email) {
        this.userId = userId;
        this.name = name;
        this.location = location;
        this.age = age;
        this.mobile = mobile;
        this.email = email;
    }

    // 🔥 Getters
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public int getAge() { return age; }
    public String getMobile() { return mobile; }
    public String getEmail() { return email; }

    // 🔥 Setters
    public void setUserId(String userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setLocation(String location) { this.location = location; }
    public void setAge(int age) { this.age = age; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public void setEmail(String email) { this.email = email; }
}
