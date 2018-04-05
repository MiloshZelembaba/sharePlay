package com.miloshzelembaba.play.Spotify;

import com.spotify.sdk.android.authentication.AuthenticationResponse;

/**
 * Created by miloshzelembaba on 2018-03-12.
 */

abstract public class SpotifyInfo {
    // TODO: Replace with your client ID
    public static final String CLIENT_ID = "75cc7c4b4c6d49388044414a5ba6aaa6";
    // TODO: Replace with your redirect URI
    public static final String REDIRECT_URI = "http://localhost:8888/callback";
    static public AuthenticationResponse auth;
    // Request code that will be used to verify if the result comes from correct activity, can be any integer
    public static final int REQUEST_CODE = 1337;
    public static String ACCESS_TOKEN;

    public static void setAccessToken(String a){
        ACCESS_TOKEN = a;
    }
}