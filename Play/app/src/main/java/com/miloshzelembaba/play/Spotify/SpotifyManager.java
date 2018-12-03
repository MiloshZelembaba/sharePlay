package com.miloshzelembaba.play.Spotify;

import android.app.Activity;
import android.util.Log;

import com.miloshzelembaba.play.Activity.StartActivity.InitialActivity;
import com.miloshzelembaba.play.Models.Song;
import com.miloshzelembaba.play.Models.User;
import com.miloshzelembaba.play.Utils.ApplicationUtil;
import com.miloshzelembaba.play.Utils.SharedPreferenceUtil;
import com.miloshzelembaba.play.api.Services.AuthenticationServices.AttemptLoginService;
import com.miloshzelembaba.play.api.Services.AuthenticationServices.GetRefreshedAccessTokenService;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.SpotifyPlayer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.SavedTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.UserPrivate;
import kaaes.spotify.webapi.android.models.UserPublic;
import retrofit.RetrofitError;

public class SpotifyManager implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback {
    public static final int REQUEST_CODE = 1337;
    static public String CLIENT_ID;

    static SpotifyUpdateListener mSpotifyUpdateListener;
    static SpotifyApi mSpotifyApi;
    static SpotifyPlayer mSpotifyPlayer;
    static SpotifyManager instance;

    // Spotify Objects
    UserPrivate mPrivateUser;
    UserPublic mPublicUser;

    // Services
    private AttemptLoginService attemptLoginService = new AttemptLoginService();
    private GetRefreshedAccessTokenService getRefreshedAccessTokenService = new GetRefreshedAccessTokenService();

    private SpotifyManager() {}

    public static SpotifyManager getInstance() {
        if (instance == null){
            instance = new SpotifyManager();
        }

        return instance;
    }

    public void setSpotifyUpdateListener(SpotifyUpdateListener spotifyUpdateListener) {
        mSpotifyUpdateListener = spotifyUpdateListener;
    }

    public void logout(Activity activity) {
        SharedPreferenceUtil.getInstance(activity).setUser(null);
    }

    public void authorize(final Activity activity, final InitialActivity.AuthResult callback) {
        User user = SharedPreferenceUtil.getInstance(activity).getUser();
        String email = user == null ? "" : user.getEmail();
        String product = user == null ? "" : user.mProduct;
        attemptLoginService.requestService(email, product, new AttemptLoginService.AttemptLoginServiceCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if (response.has("access_token")) {
                    String accessToken = response.optString("access_token");
                    CLIENT_ID = response.optString("client_id");
                    int timeUntilExpire = response.optInt("expires_in");
                    if (response.has("user")) {
                        try {
                            User user = new User(response.optJSONObject("user"));
                            ApplicationUtil.getInstance().setUser(user);
                        } catch (Exception e) {}
                    }
                    createSpotifyApi(accessToken, timeUntilExpire);
                    callback.onSuccess(accessToken);
                } else {
                    String clientId = response.optString("client_id");
                    CLIENT_ID = clientId;
                    String redirectUri = response.optString("redirect_uri");
                    fetchAuthCode(activity, clientId, redirectUri);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                callback.onFailure();
            }
        });
    }

    private void fetchAuthCode(final Activity activity, String clientId, String redirectUri) {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(clientId, AuthenticationResponse.Type.CODE, redirectUri);
        builder.setScopes(new String[]{"user-read-private", "streaming", "user-read-email","user-library-read"});
        builder.setShowDialog(true);
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(activity, REQUEST_CODE, request);
    }

    public void createSpotifyApi(String accessToken, int timeUntilExpire) {
        mSpotifyApi = new SpotifyApi();
        mSpotifyApi.setAccessToken(accessToken);

        if (timeUntilExpire != 0) {
            createThreadExecutorForTokenRefresh(timeUntilExpire);
        }
    }

    private void createThreadExecutorForTokenRefresh(int timeUnitlExpire) {
        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                if (getRefreshedAccessTokenService == null){
                    getRefreshedAccessTokenService = new GetRefreshedAccessTokenService();
                }
                User user = ApplicationUtil.getInstance().getUser();
                if (user != null && !user.isTemporaryUser()) {
                    getRefreshedAccessTokenService.requestService(user, new GetRefreshedAccessTokenService.GetRefreshedAccessTokenServiceCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            createSpotifyApi(response.optString("access_token"), 0);
                        }

                        @Override
                        public void onFailure(String errorMessage) {

                        }
                    });
                }
            }
        }, timeUnitlExpire, TimeUnit.SECONDS);
    }

    public void pauseSong() {
        if (mSpotifyPlayer != null) {
            mSpotifyPlayer.pause(null);
        }
    }

    public SpotifyApi getApi() {
        return mSpotifyApi;
    }

    public ArrayList<Song> getUserLibrary() {
        Pager<SavedTrack> userLibraryPager = new Pager<>();
        try {
            userLibraryPager = mSpotifyApi.getService().getMySavedTracks();
        } catch (RetrofitError r) {
            System.out.println(r.toString());
        }
        ArrayList<Song> songs = new ArrayList<>();

        for (SavedTrack savedTrack: userLibraryPager.items) {
            Track track = savedTrack.track;
            String allArtists = track.artists.get(0).name;
            for (int i = 1; i < track.artists.size(); ++i) { // notice that it starts on i=1
                allArtists += " & " + track.artists.get(i).name;
            }
            Song song = new Song(track.uri, track.name, allArtists, track.album.images);
            songs.add(song);
        }

        return songs;
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
                    Log.d("Service debugging", "mSpotifyUpdateListener not null");
                    mSpotifyUpdateListener.onSongFinishedPlaying();
                } else {
                    Log.d("Service debugging", "mSpotifyUpdateListener is null");
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
