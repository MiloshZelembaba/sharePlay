package com.miloshzelembaba.play.Utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.miloshzelembaba.play.Models.User;

import org.json.JSONObject;

public class SharedPreferenceUtil {
    private static SharedPreferenceUtil instance;
    private static Context context;

    public static SharedPreferenceUtil getInstance(Context c) {
        if (instance == null) {
            context = c;
            instance = new SharedPreferenceUtil();
        }

        return instance;
    }

    private SharedPreferenceUtil() {}

    public void setUser(User user) {
        SharedPreferences prefs = context.getSharedPreferences("SharePlayPreference", Context.MODE_PRIVATE);
        try {
            String serializedUser = user.serialize().toString();
            prefs.edit().putString("user", serializedUser).apply();
        } catch (Exception e) {
            prefs.edit().putString("user", null).apply();
        }
    }

    public User getUser() {
        SharedPreferences prefs = context.getSharedPreferences("SharePlayPreference", Context.MODE_PRIVATE);
        String serializedUser = prefs.getString("user", "");
        User user = null;
        try {
            user = new User(new JSONObject(serializedUser));
        } catch (Exception e) {
            System.out.println("what?");
        }

        return user;
    }

    public boolean getServerMode() {
        SharedPreferences prefs = context.getSharedPreferences("SharePlayPreference", Context.MODE_PRIVATE);
        return prefs.getBoolean("serverMode", false);
    }

    public void toggleServerMode() {
        SharedPreferences prefs = context.getSharedPreferences("SharePlayPreference", Context.MODE_PRIVATE);
        boolean tmp = prefs.getBoolean("serverMode", false);
        prefs.edit().putBoolean("serverMode", !tmp).apply();
    }
}
