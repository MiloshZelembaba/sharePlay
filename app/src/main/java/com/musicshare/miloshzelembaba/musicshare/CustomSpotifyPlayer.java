package com.musicshare.miloshzelembaba.musicshare;

/**
 * Created by miloshzelembaba on 2017-02-04.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

public class CustomSpotifyPlayer extends Activity implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback {
    // TODO: Replace with your client ID
    public static final String CLIENT_ID = "75cc7c4b4c6d49388044414a5ba6aaa6";
    // TODO: Replace with your redirect URI
    public static final String REDIRECT_URI = "http://localhost:8888/callback";
    static private Context context;
    static public AuthenticationResponse auth;
    private LoginWindowInfo loginWindowInfo;

    static private Player mPlayer;
    // Request code that will be used to verify if the result comes from correct activity, can be any integer
    private static final int REQUEST_CODE = 1337;

    public CustomSpotifyPlayer(Context context){
        this.context = context;
        //this.loginWindowInfo = loginWindowInfo;
    }

    public CustomSpotifyPlayer(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"}); // TODO the scope here can change for more privliges: https://developer.spotify.com/web-api/using-scopes/
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    static public void playSong(Song song){
        mPlayer.playUri(null, "spotify:track:5EX8gks8V2wDZanRGAy8pm", 0, 0);
        //mPlayer.playUri(null, song.getURI(), 0, 0);
    }

    @Override
    public void onLoginFailed(Error var1){

    }

    static public Intent getMyIntent(){
        Intent intent = new Intent(context, CustomSpotifyPlayer.class);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            auth = response;
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(CustomSpotifyPlayer.this);
                        mPlayer.addNotificationCallback(CustomSpotifyPlayer.this);
                        onLoggedIn();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("MainActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
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
        //mPlayer.playUri(null, "spotify:track:2TpxZ7JUBn3uw46aR7qd6V", 0, 0);
        mPlayer.playUri(null, "spotify:track:5EX8gks8V2wDZanRGAy8pm", 0, 0);
        MainPageActivity.logIn();
        //finish();
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
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
