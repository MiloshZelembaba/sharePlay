package com.miloshzelembaba.play.Activity.PartyActivityStuff;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
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
import com.miloshzelembaba.play.Network.NetworkManager;
import com.miloshzelembaba.play.R;
import com.miloshzelembaba.play.Spotify.SpotifyManager;
import com.miloshzelembaba.play.Spotify.SpotifyUpdateListener;
import com.miloshzelembaba.play.Utils.StringUtil;
import com.miloshzelembaba.play.api.Services.AddSongToPartyService;
import com.miloshzelembaba.play.api.Services.GetPartyDetailsService;
import com.miloshzelembaba.play.api.Services.IncrementSongVoteCountService;
import com.miloshzelembaba.play.api.Services.LeavePartyService;
import com.miloshzelembaba.play.api.Services.RemoveSongFromPartyService;
import com.spotify.sdk.android.player.Player;

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
    private TextView mPlaybackControlPlay;
    private TextView mPlaybackControlNextSong;
    private boolean mIsPlaying;
    private Song currentlyPlayingSong;
    private FloatingActionButton fab;


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
        mPlaybackControlPlay = (TextView) findViewById(R.id.music_controls_play);
        mPlaybackControlNextSong = (TextView) findViewById(R.id.music_controls_next_song);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mMusicControls = (LinearLayout) findViewById(R.id.music_controls_container);
        mMusicControls.setVisibility(VISIBLE);
        mPlaybackControlPlay.setText(getString(R.string.play));
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
        mPlaybackControlNextSong.setText(getString(R.string.next));
        mPlaybackControlNextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextSong();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
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
            mPlayer.pause(null);
            mIsPlaying = false;
            mPlaybackControlPlay.setText(getString(R.string.resume));
            currentlyPlayingSong.setIsCurrentlyPlaying(false);
            mPartySongsAdapter.notifyDataSetChanged();
        }
    }

    private void playSong(){
        if (mParty.getQueuedSongs() == null || mParty.getQueuedSongs().size() == 0) {
            currentlyPlayingSong = null;
            mIsPlaying = false;
            mPlaybackControlPlay.setText(getString(R.string.play));
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
        mPlaybackControlPlay.setText(getString(R.string.pause));

    }

    private void playNextSong() {
        if (mParty.getQueuedSongs() == null || mParty.getQueuedSongs().size() == 0) {
            currentlyPlayingSong = null;
            mIsPlaying = false;
            mPlaybackControlPlay.setText(getString(R.string.play));
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
//        mSongsListView.removeHeaderView()
        mParty = party;
        mPartySongsAdapter = new PartySongsAdapter(this, 0, party.getQueuedSongs());
        if (party.getCurrentlyPlaying() != null) {
            mSongsListView.addHeaderView(inflateSong(party.getCurrentlyPlaying()));
        }
        mSongsListView.setAdapter(mPartySongsAdapter);
        setTitle(mParty.getName() + " " + StringUtil.padZeros(mParty.getId()));
    }

    private View inflateSong(Song song) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view= inflater.inflate(R.layout.party_song_layout, null, false);
        ((TextView)view.findViewById(R.id.song_name)).setText(song.getSongName());
        ((TextView)view.findViewById(R.id.song_artists)).setText(song.getSongArtists());
        ((TextView)view.findViewById(R.id.song_vote_count)).setText(Integer.toString(song.getVoteCount()));

        return view;
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
