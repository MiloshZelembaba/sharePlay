package com.miloshzelembaba.play.Activity.PartyActivityStuff;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.miloshzelembaba.play.Activity.SongSearch.SongSearchActivity;
import com.miloshzelembaba.play.Models.Party;
import com.miloshzelembaba.play.Models.Song;
import com.miloshzelembaba.play.Models.User;
import com.miloshzelembaba.play.Network.NetworkEventTypeCallbacks.OnPartyUpdated;
import com.miloshzelembaba.play.Network.NetworkInfo;
import com.miloshzelembaba.play.R;
import com.miloshzelembaba.play.Spotify.SpotifyInfo;
import com.miloshzelembaba.play.api.Services.AddSongToPartyService;
import com.miloshzelembaba.play.api.Services.GetPartyDetailsService;
import com.miloshzelembaba.play.api.Services.IncrementSongVoteCountService;
import com.miloshzelembaba.play.api.Services.LeavePartyService;
import com.miloshzelembaba.play.api.Services.RemoveSongFromPartyService;
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

import org.json.JSONException;
import org.json.JSONObject;

import static android.view.View.VISIBLE;

public class PartyActivity extends AppCompatActivity implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback, OnPartyUpdated {
    public static final String EXTRA_PARTY_ID = "ExtraPartyId";
    public static final String EXTRA_USER = "ExtraUser";

    // Services
    GetPartyDetailsService getPartyDetailsService;
    AddSongToPartyService addSongToPartyService;
    IncrementSongVoteCountService incrementSongVoteCountService;
    LeavePartyService leavePartyService;
    RemoveSongFromPartyService removeSongFromPartyService;

    private Player mPlayer;
    private Party mParty;
    private User user;
    private ListView mSongsListView;
    private PartySongsAdapter mPartySongsAdapter;
    private LinearLayout mMusicControls;
    private TextView mPlaybackControl;
    private boolean isHost = true;
    private boolean mIsPlaying;
    private Song currentlyPlayingSong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NetworkInfo.getInstance().addPartyUpdateListener(this);

        getPartyDetailsService = new GetPartyDetailsService();
        addSongToPartyService = new AddSongToPartyService();
        incrementSongVoteCountService = new IncrementSongVoteCountService();
        leavePartyService = new LeavePartyService();
        removeSongFromPartyService = new RemoveSongFromPartyService();

        setContentView(R.layout.activity_party);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String partyId = getIntent().getStringExtra(EXTRA_PARTY_ID);
        mSongsListView = (ListView) findViewById(R.id.party_songs);
        mPlaybackControl = (TextView) findViewById(R.id.music_controls_play);
        try {
            user = new User(new JSONObject(getIntent().getStringExtra(EXTRA_USER)));
        } catch (JSONException e){
            user = null;
        }

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(PartyActivity.this, SongSearchActivity.class), SongSearchActivity.SONG_SEARCH_RESULT);
            }
        });

        getPartyDetailsService.requestService(partyId,
                new GetPartyDetailsService.GetPartyDetailsServiceCallback() {
                    @Override
                    public void onSuccess(Party party) {
                        setParty(party);
                    }

                    @Override
                    public void onFailure(String errorMessage) {

                    }
                });

        if (isHost) {
            showHostView();
        }

        attemptSpotifyLogin();
    }

    private void showHostView(){
        mIsPlaying = false;
        mMusicControls = (LinearLayout) findViewById(R.id.music_controls_container);
        mMusicControls.setVisibility(VISIBLE);
        mPlaybackControl.setText(getString(R.string.play_song));
        mPlaybackControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsPlaying) {
                    pauseSong();
                } else {
                    playSong();
                }
            }
        });
    }

    private void pauseSong(){
        mPlayer.pause(null);
        mIsPlaying = false;
        mPlaybackControl.setText(getString(R.string.resume_song));
    }

    private void playSong(){
        if (mIsPlaying || currentlyPlayingSong != null) {
            mPlayer.resume(null);
            mIsPlaying = true;
        } else {
            currentlyPlayingSong = mParty.getSongs().get(0);
            mPlayer.playUri(null, currentlyPlayingSong.getUri(), 0, 0);
            mIsPlaying = true;
        }

        mPlaybackControl.setText(getString(R.string.pause_song));

    }

    private void playNextSong() {
        removeSongFromPartyService.requestService(currentlyPlayingSong,
                new RemoveSongFromPartyService.RemoveSongFromPartyServiceCallback() {
                    @Override
                    public void onSuccess(Party party) {
                        setParty(party);
                        currentlyPlayingSong = null;
                        mIsPlaying = false;
                        playSong();
                    }

                    @Override
                    public void onFailure(String errorMessage) {

                    }
                });
    }

    private void setParty(Party party) {
        mParty = party;
        mPartySongsAdapter = new PartySongsAdapter(this, 0, party.getSongs());
        mSongsListView.setAdapter(mPartySongsAdapter);
        setTitle(mParty.getName() + " " + padZeros(mParty.getId()));
    }

    private String padZeros(String partyId){
        while (partyId.length() < 6){
            partyId = "0" + partyId;
        }

        return partyId;
    }


    private void attemptSpotifyLogin(){
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(SpotifyInfo.CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                SpotifyInfo.REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, SpotifyInfo.REQUEST_CODE, request);
    }

    private void addSongToParty(Song song) {
        // so that the UI updated immediately for the user
//        mPartySongsAdapter.add(song);

        Toast.makeText(this, "Added " + song.getSongName(), Toast.LENGTH_SHORT).show();

        addSongToPartyService.requestService(user, mParty, song, null);
    }

    public void incrementSongCount(Song song){
        //  this is just so that the UI updates immediately for the user that added a vote
//        song.incrementVoteCount();
//        mPartySongsAdapter.notifyDataSetChanged();

        incrementSongVoteCountService.requestService(song,
                new IncrementSongVoteCountService.IncrementSongVoteCountServiceCallback() {
                    @Override
                    public void onSuccess(Party party) {
                        setParty(party); // this should be updated
                    }

                    @Override
                    public void onFailure(String errorMessage) {

                    }
                });

    }

    @Override
    public void onPartyUpdated(final Party party) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    setParty(party);
                } catch (Exception e){
                    System.out.println("uhoh");
                }
            }
        });
    }








    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("MainActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            case kSpPlaybackNotifyAudioDeliveryDone:
                playNextSong();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == SongSearchActivity.SONG_SEARCH_RESULT && intent != null){
            String serializedSong = intent.getStringExtra("song");
            JSONObject jsonSong;
            try {
                jsonSong = new JSONObject(serializedSong);
                Song song = new Song(jsonSong);
                addSongToParty(song);
            } catch (Exception e) {
                // make error popup thing
            }
        }

        // Check if result comes from the correct activity
        if (requestCode == SpotifyInfo.REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                SpotifyInfo.setAccessToken(response.getAccessToken());
                Config playerConfig = new Config(this, response.getAccessToken(), SpotifyInfo.CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(PartyActivity.this);
                        mPlayer.addNotificationCallback(PartyActivity.this);
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
        leavePartyService.requestService(user, null);
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

}
