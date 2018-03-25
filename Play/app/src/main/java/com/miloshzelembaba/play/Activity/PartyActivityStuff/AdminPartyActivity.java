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
import com.miloshzelembaba.play.Spotify.SpotifyManager;
import com.miloshzelembaba.play.Spotify.SpotifyUpdateListener;
import com.miloshzelembaba.play.Utils.StringUtil;
import com.miloshzelembaba.play.api.Services.AddSongToPartyService;
import com.miloshzelembaba.play.api.Services.GetPartyDetailsService;
import com.miloshzelembaba.play.api.Services.IncrementSongVoteCountService;
import com.miloshzelembaba.play.api.Services.LeavePartyService;
import com.miloshzelembaba.play.api.Services.RemoveSongFromPartyService;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import org.json.JSONException;
import org.json.JSONObject;

import static android.view.View.VISIBLE;

public class AdminPartyActivity extends AppCompatActivity implements OnPartyUpdated, PartyMethods, SpotifyUpdateListener {
    public static final String EXTRA_PARTY_ID = "ExtraPartyId";
    public static final String EXTRA_USER = "ExtraUser";

    // Services
    GetPartyDetailsService getPartyDetailsService;
    AddSongToPartyService addSongToPartyService;
    IncrementSongVoteCountService incrementSongVoteCountService;
    LeavePartyService leavePartyService;
    RemoveSongFromPartyService removeSongFromPartyService;

    // Spotify
    private Player mPlayer;
    private SpotifyManager mSpotifyManager;

    // Local
    private Party mParty;
    private User user;

    // Views
    private ListView mSongsListView;
    private PartySongsAdapter mPartySongsAdapter;
    private LinearLayout mMusicControls;
    private TextView mPlaybackControl;
    private boolean mIsPlaying;
    private Song currentlyPlayingSong;
    private FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NetworkInfo.getInstance().addPartyUpdateListener(this);
        mSpotifyManager = new SpotifyManager(this);
        mSpotifyManager.attemptSpotifyLogin();

        initServices();
        initViews();
        mIsPlaying = false;


        try {
            user = new User(new JSONObject(getIntent().getStringExtra(EXTRA_USER)));
        } catch (JSONException e){
            user = null;
        }

        String partyId = getIntent().getStringExtra(EXTRA_PARTY_ID);
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
    }

    private void initServices() {
        getPartyDetailsService = new GetPartyDetailsService();
        addSongToPartyService = new AddSongToPartyService();
        incrementSongVoteCountService = new IncrementSongVoteCountService();
        leavePartyService = new LeavePartyService();
        removeSongFromPartyService = new RemoveSongFromPartyService();
    }

    private void initViews() {
        setContentView(R.layout.admin_activity_party);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSongsListView = (ListView) findViewById(R.id.party_songs);
        mPlaybackControl = (TextView) findViewById(R.id.music_controls_play);
        fab = (FloatingActionButton) findViewById(R.id.fab);
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
        if (mParty.getSongs() == null || mParty.getSongs().size() == 0) {
            currentlyPlayingSong = null;
            mIsPlaying = false;
            mPlaybackControl.setText(getString(R.string.play_song));
            return;
        }

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
        if (mParty.getSongs() == null || mParty.getSongs().size() == 0) {
            currentlyPlayingSong = null;
            mIsPlaying = false;
            mPlaybackControl.setText(getString(R.string.play_song));
            return;
        }

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
        setTitle(mParty.getName() + " " + StringUtil.padZeros(mParty.getId()));
    }

    private void addSongToParty(Song song) {
        Toast.makeText(this, "Added " + song.getSongName(), Toast.LENGTH_SHORT).show();

        addSongToPartyService.requestService(user, mParty, song, null);
    }

    @Override
    public void incrementSongCount(Song song) {
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
                        mPlayer.addConnectionStateCallback(mSpotifyManager);
                        mPlayer.addNotificationCallback(mSpotifyManager);
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
    public void onSongFinishedPlaying() {
        playNextSong();
    }

    @Override
    public void onLoggedIn() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(AdminPartyActivity.this, SongSearchActivity.class), SongSearchActivity.SONG_SEARCH_RESULT);
            }
        });
    }

    @Override
    protected void onDestroy() {
        leavePartyService.requestService(user, null);
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

}
