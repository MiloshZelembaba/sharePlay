package com.miloshzelembaba.play.Spotify;

import android.app.Activity;
import android.util.Log;

import com.miloshzelembaba.play.Activity.StartActivity.InitialActivity;
import com.miloshzelembaba.play.Models.User;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.UserPrivate;
import kaaes.spotify.webapi.android.models.UserPublic;

/**
 * Created by miloshzelembaba on 2018-03-25.
 */

public class SpotifyManager implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback {
    public static final String CLIENT_ID = "75cc7c4b4c6d49388044414a5ba6aaa6";
    public static final String REDIRECT_URI = "what://localhost:8888/callback";
    public static final int REQUEST_CODE = 1337;
    public static String ACCESS_TOKEN;

    static SpotifyUpdateListener mSpotifyUpdateListener;
    static Activity baseActivity;
    static SpotifyApi mSpotifyApi;
    static SpotifyPlayer mSpotifyPlayer;
    static SpotifyManager instance;

    // Spotify Objects
    UserPrivate mPrivateUser;
    UserPublic mPublicUser;


    private SpotifyManager() {}
    public static SpotifyManager getInstance() {
        if (baseActivity == null) { // this means we haven't logged in so you don't get one
            return null;
        }

        if (instance == null){
            instance = new SpotifyManager();
        }

        return instance;
    }

    public void setSpotifyUpdateListener(SpotifyUpdateListener spotifyUpdateListener) {
        mSpotifyUpdateListener = spotifyUpdateListener;
    }

    public static void setAccessToken(String a){
        ACCESS_TOKEN = a;
    }

    public void logout() {
        // might not be possible for some stupid reason, stupid ass spotifyAPI
    }

    public void relogin() {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI).setShowDialog(true);
        AuthenticationRequest request = builder.setScopes(new String[]{"user-read-private", "streaming", "user-read-email"}).build();
        AuthenticationClient.openLoginActivity(baseActivity, REQUEST_CODE, request);
    }

    public static void attemptSpotifyLogin(Activity activity){
        baseActivity = activity;
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming", "user-read-email"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(baseActivity, REQUEST_CODE, request);
    }

    public void createSpotifyApi() {
        mSpotifyApi = new SpotifyApi();
        mSpotifyApi.setAccessToken(ACCESS_TOKEN);
    }

    public void pauseSong() {
        if (mSpotifyPlayer != null) {
            mSpotifyPlayer.pause(null);
        }
    }

    public String getProduct() {
        return getPrivateUser().product;
    }

    private UserPrivate getPrivateUser() {
        if (mPrivateUser == null) {
            mPrivateUser = mSpotifyApi.getService().getMe();
        }

        return mPrivateUser;
    }

    private UserPublic getPublicUser(String id) {
        if (mPublicUser == null) {
            mPublicUser = mSpotifyApi.getService().getUser(id);
        }

        return  mPublicUser;
    }

    public void getUserDetails(final InitialActivity.SpotifyResultCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Map<String, String> userDetails = new HashMap<>();
                userDetails.put(User.EMAIL, getPrivateUser().email);
                userDetails.put(User.DISPLAY_NAME, getPrivateUser().display_name);

                callback.onSuccess(userDetails);
            }
        };
        Thread getEmailThread = new Thread(runnable);
        getEmailThread.start();

    }

    public void setPlayer(SpotifyPlayer player) {
        mSpotifyPlayer = player;
    }

    public SpotifyPlayer getPlayer() {
        return mSpotifyPlayer;
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("MainActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            case kSpPlaybackNotifyAudioDeliveryDone:
                if (mSpotifyUpdateListener != null) {
                    mSpotifyUpdateListener.onSongFinishedPlaying();
                }
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("MainActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error error) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }
}
