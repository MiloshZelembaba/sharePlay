package com.musicshare.miloshzelembaba.musicshare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by miloshzelembaba on 2017-01-18.
 */

public class LoginWindowInfo {
    private boolean loggedIn = false;
    Context context;
    private String userName;
    private String password;

    public LoginWindowInfo(Context context){
        this.context = context;
    }

    public void setUsername(String name){
        userName = name;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void logIn(){
        loggedIn = true;
    }

    public void logOut(){
        loggedIn = false;
    }

    public boolean isUserLoggedIn(){
        return loggedIn;
    }

    public Intent getLoginWindowIntent(){
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }

    public String getUserName(){
        return userName;
    }
}
