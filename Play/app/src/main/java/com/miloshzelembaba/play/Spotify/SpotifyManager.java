package com.miloshzelembaba.play.Spotify;

import android.app.Activity;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.SpotifyPlayer;

/**
 * Created by miloshzelembaba on 2018-03-25.
 */

public class SpotifyManager implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback {
    SpotifyUpdateListener mSpotifyUpdateListener;
    Activity baseActivity;


    // You bet your left nut you'll be passing in a SpotifyUpdateListener
    public SpotifyManager(Activity baseActivity) {
        this.baseActivity = baseActivity;
        mSpotifyUpdateListener = (SpotifyUpdateListener) baseActivity;
    }

    public void attemptSpotifyLogin(){
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(SpotifyInfo.CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                SpotifyInfo.REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(baseActivity, SpotifyInfo.REQUEST_CODE, request);
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("MainActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            case kSpPlaybackNotifyAudioDeliveryDone:
                mSpotifyUpdateListener.onSongFinishedPlaying();
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
        mSpotifyUpdateListener.onLoggedIn();
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
