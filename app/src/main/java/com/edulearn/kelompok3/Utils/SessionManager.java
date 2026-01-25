package com.edulearn.kelompok3.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.edulearn.kelompok3.Model.User;
import com.google.gson.Gson;

/**
 * SessionManager untuk menyimpan data user login
 * Ini akan mengatasi masalah logout otomatis
 */

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_DATA = "userData";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_ROLE = "userRole";
    private static final String KEY_USER_PHOTO = "userPhoto";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    private Gson gson;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        gson = new Gson();
    }

    /**
     * Simpan data user setelah login berhasil
     */
    public void createLoginSession(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, user.getUserId());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_ROLE, user.getRole());
        editor.putString(KEY_USER_PHOTO, user.getPhotoUrl());

        // Simpan juga dalam bentuk JSON untuk backup
        String userJson = gson.toJson(user);
        editor.putString(KEY_USER_DATA, userJson);

        editor.apply();
    }

    /**
     * Update data user (misalnya setelah edit profile)
     */
    public void updateUserSession(User user) {
        createLoginSession(user);
    }

    /**
     * Cek apakah user sudah login
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Ambil User ID
     */
    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }

    /**
     * Ambil User Name
     */
    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "User");
    }

    /**
     * Ambil User Email
     */
    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, null);
    }

    /**
     * Ambil User Role
     */
    public String getUserRole() {
        return pref.getString(KEY_USER_ROLE, "student");
    }

    /**
     * Ambil User Photo URL
     */
    public String getUserPhoto() {
        return pref.getString(KEY_USER_PHOTO, null);
    }

    /**
     * Ambil seluruh data User object
     */
    public User getUserDetails() {
        String userJson = pref.getString(KEY_USER_DATA, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }

        // Fallback jika JSON tidak ada, buat dari data terpisah
        User user = new User();
        user.setUserId(getUserId());
        user.setName(getUserName());
        user.setEmail(getUserEmail());
        user.setRole(getUserRole());
        user.setPhotoUrl(getUserPhoto());
        return user;
    }

    /**
     * Logout user
     */
    public void logoutUser() {
        editor.clear();
        editor.apply();
    }

    /**
     * Update nama user
     */
    public void updateUserName(String name) {
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }

    /**
     * Update photo user
     */
    public void updateUserPhoto(String photoUrl) {
        editor.putString(KEY_USER_PHOTO, photoUrl);
        editor.apply();
    }
}
