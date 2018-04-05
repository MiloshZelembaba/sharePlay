package com.miloshzelembaba.play.Spotify;

import android.app.Activity;
import android.util.Log;

import com.miloshzelembaba.play.Activity.StartActivity.InitialActivity;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.SpotifyPlayer;

import kaaes.spotify.webapi.android.SpotifyApi;

/**
 * Created by miloshzelembaba on 2018-03-25.
 */

public class SpotifyManager implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback {
    static SpotifyUpdateListener mSpotifyUpdateListener;
    static Activity baseActivity;
    static SpotifyApi mSpotifyApi;
    static SpotifyManager instance;
    static SpotifyPlayer mSpotifyPlayer;


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

    public static void attemptSpotifyLogin(Activity activity){
        baseActivity = activity;
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(SpotifyInfo.CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                SpotifyInfo.REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming", "user-read-email"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(baseActivity, SpotifyInfo.REQUEST_CODE, request);
    }

    public void createSpotifyApi() {
        mSpotifyApi = new SpotifyApi();
        mSpotifyApi.setAccessToken(SpotifyInfo.ACCESS_TOKEN);
    }

    public void getEmail(final InitialActivity.SpotifyResultCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String email = mSpotifyApi.getService().getMe().email;
                callback.onSuccess(email);
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
//                mSpotifyUpdateListener.onSongFinishedPlaying();
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
//        mSpotifyUpdateListener.onLoggedIn();
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
