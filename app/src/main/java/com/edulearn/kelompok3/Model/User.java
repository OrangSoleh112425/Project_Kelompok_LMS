package com.edulearn.kelompok3.Model;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String userId;
    private String name;
    private String email;
    private String role; // "student" atau "teacher"
    private String photoUrl;
    private long createdAt;
    private Map<String, Boolean> classIds; // Untuk Realtime Database gunakan Map

    // Constructor kosong diperlukan untuk Firebase
    public User() {
        this.classIds = new HashMap<>();
    }

    // Constructor dengan parameter
    public User(String userId, String name, String email, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.createdAt = System.currentTimeMillis();
        this.classIds = new HashMap<>();
    }

    // Getters dan Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public Map<String, Boolean> getClassIds() {
        return classIds;
    }

    public void setClassIds(Map<String, Boolean> classIds) {
        this.classIds = classIds;
    }
}
