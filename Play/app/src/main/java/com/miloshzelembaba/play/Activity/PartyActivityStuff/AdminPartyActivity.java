package com.miloshzelembaba.play.Activity.PartyActivityStuff;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.miloshzelembaba.play.Activity.PartyMembers.PartyMembersActivity;
import com.miloshzelembaba.play.Activity.SongSearch.SongSearchActivity;
import com.miloshzelembaba.play.Models.Party;
import com.miloshzelembaba.play.Models.Song;
import com.miloshzelembaba.play.Models.User;
import com.miloshzelembaba.play.Network.NetworkManager;
import com.miloshzelembaba.play.R;
import com.miloshzelembaba.play.Spotify.SpotifyManager;
import com.miloshzelembaba.play.Spotify.SpotifyUpdateListener;
import com.miloshzelembaba.play.api.Services.GetPartyDetailsService;
import com.miloshzelembaba.play.api.Services.RemoveSongFromPartyService;
import com.spotify.sdk.android.player.Player;

import org.json.JSONException;
import org.json.JSONObject;

import static android.view.View.VISIBLE;

public class AdminPartyActivity extends BaseParty implements SpotifyUpdateListener {
    public static final String EXTRA_PARTY_ID = "ExtraPartyId";
    public static final String EXTRA_USER = "ExtraUser";

    // Services
    RemoveSongFromPartyService removeSongFromPartyService;

    // Spotify
    private Player mPlayer;
    private SpotifyManager mSpotifyManager;

    // Views
    private LinearLayout mMusicControls;
    private ImageView mPlaybackControlPlay;
    private ImageView mPlaybackControlNextSong;
    private ImageView mPlaybackControlAddSong;
    private boolean mIsPlaying;
    private Song currentlyPlayingSong;
    private FloatingActionButton fab;
    private ImageView partyMembersIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NetworkManager.getInstance().addPartyUpdateListener(this);

        initServices();
        initViews();
        mIsPlaying = false;
        mSpotifyManager = SpotifyManager.getInstance();
        mSpotifyManager.setSpotifyUpdateListener(this);
        mPlayer = mSpotifyManager.getPlayer();


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

    @Override
    protected void initServices() {
        super.initServices(); // init the base services
        removeSongFromPartyService = new RemoveSongFromPartyService();
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.admin_activity_party);
        partyMembersIcon = (ImageView) findViewById(R.id.party_members);
        header = (TextView) findViewById(R.id.party_activity_header);
        mSongsListView = (ListView) findViewById(R.id.party_songs);
        mPlaybackControlPlay = (ImageView) findViewById(R.id.music_controls_play);
        mPlaybackControlNextSong = (ImageView) findViewById(R.id.music_controls_next_song);
        mPlaybackControlAddSong = (ImageView) findViewById(R.id.music_controls_add_song);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mMusicControls = (LinearLayout) findViewById(R.id.music_controls_container);
        mMusicControls.setVisibility(VISIBLE);
        mPlaybackControlAddSong.setImageResource(R.mipmap.baseline_add_black_36);
        mPlaybackControlPlay.setImageResource(R.mipmap.baseline_play_arrow_black_36);
        mPlaybackControlPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsPlaying) {
                    pauseSong();
                } else {
                    playSong();
                }
            }
        });
        mPlaybackControlNextSong.setImageResource(R.mipmap.baseline_skip_next_black_36);
        mPlaybackControlNextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextSong();
            }
        });

        partyMembersIcon.setImageResource(R.drawable.baseline_supervisor_account_white_24dp);partyMembersIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mParty == null || mParty.getHost() == null) { // host should only be null for old parties when leaving parties wasn't fully consistent yet
                    return;
                }
                Intent intent = new Intent(AdminPartyActivity.this, PartyMembersActivity.class);
                try {
                    intent.putExtra(PartyMembersActivity.PARTY, mParty.serialize().toString());
                } catch (Exception e) {}

                startActivity(intent);
            }
        });


        fab.setImageResource(R.drawable.ic_search_white_24dp);
        mPlaybackControlAddSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    startActivityForResult(new Intent(AdminPartyActivity.this, SongSearchActivity.class), SongSearchActivity.SONG_SEARCH_RESULT);
                }
            }
        });

    }

    private void pauseSong(){
        if (currentlyPlayingSong != null) {
            mSpotifyManager.pauseSong();
            mIsPlaying = false;
            mPlaybackControlPlay.setImageResource(R.mipmap.baseline_play_arrow_black_36);
            currentlyPlayingSong.setIsCurrentlyPlaying(false);
            mPartySongsAdapter.notifyDataSetChanged();
        }
    }

    private void playSong(){
        if (mParty.getQueuedSongs() == null || mParty.getQueuedSongs().size() == 0) {
            mSpotifyManager.pauseSong();
            currentlyPlayingSong = null;
            mIsPlaying = false;
            mPlaybackControlPlay.setImageResource(R.mipmap.baseline_play_arrow_black_36);
            return;
        }

        if (!mIsPlaying && currentlyPlayingSong != null) { // resume song
            mPlayer.resume(null);
            mIsPlaying = true;
        } else { // play new song
            currentlyPlayingSong = mParty.getQueuedSongs().get(0);

            mPlayer.playUri(null, currentlyPlayingSong.getUri(), 0, 0);
            mIsPlaying = true;

        }

        currentlyPlayingSong.setIsCurrentlyPlaying(true);
        mPartySongsAdapter.notifyDataSetChanged();
        mPlaybackControlPlay.setImageResource(R.mipmap.baseline_pause_black_36);

    }

    private void playNextSong() {
        if (mParty.getQueuedSongs() == null || mParty.getQueuedSongs().size() == 0) {
            currentlyPlayingSong = null;
            mIsPlaying = false;
            mPlaybackControlPlay.setImageResource(R.mipmap.baseline_play_arrow_black_36);
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
    }

    @Override
    public void onSongFinishedPlaying() {
        playNextSong();
    }

    @Override
    public void onLoggedIn() {

    }

    @Override
    protected void onDestroy() {
        pauseSong();
        leavePartyService.requestService(user, null);
        super.onDestroy();
    }

}
