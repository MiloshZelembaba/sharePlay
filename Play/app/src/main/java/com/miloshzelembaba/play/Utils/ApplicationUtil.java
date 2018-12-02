package com.miloshzelembaba.play.Utils;

import android.content.Context;

import com.miloshzelembaba.play.Models.User;

/**
 * Created by miloshzelembaba on 2018-04-03.
 */

public class ApplicationUtil {
    private static ApplicationUtil instance;
    private static User user;
    private static String appMode = "local"; // local or dev server
    private static String localServerAddress = "";
    private static Context context;

    private ApplicationUtil(){}

    public static ApplicationUtil getInstance() {
        if (instance == null) {
            instance = new ApplicationUtil();
        }

        return instance;
    }

    public void setLocalServerAddress(String address) {
        localServerAddress = address;
    }

    public String getLocalServerAddress() {
        return localServerAddress;
    }

    public String getAppMode() {
        return appMode;
    }

    public void setContext(Context c) {
        context = c;
    }

    public Context getContext() {
        return context;
    }

    public void switchAppMode() {
        if (appMode.equals("dev_server")) {
            appMode = "local";
        } else {
            appMode = "dev_server";
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User u) {
        user = u;
    }


}
