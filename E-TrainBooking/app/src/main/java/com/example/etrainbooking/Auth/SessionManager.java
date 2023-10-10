package com.example.etrainbooking.Auth;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USER_ID = "userId";
    // Add more user-specific keys as needed

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void startSession(String userId) {
        editor.putString(KEY_USER_ID, userId);

        editor.apply();
    }

    public boolean isUserLoggedIn() {
        return sharedPreferences.getString(KEY_USER_ID, null) != null;
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public void endSession() {
        editor.clear();
        editor.apply();
    }
}
