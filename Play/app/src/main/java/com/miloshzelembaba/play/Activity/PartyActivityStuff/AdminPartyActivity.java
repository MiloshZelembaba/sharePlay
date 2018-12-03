package com.miloshzelembaba.play.Activity.PartyActivityStuff;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.miloshzelembaba.play.Activity.PartyMembers.PartyMembersActivity;
import com.miloshzelembaba.play.Activity.SongSearch.SongSearchActivity;
import com.miloshzelembaba.play.Error.ErrorService;
import com.miloshzelembaba.play.Models.Party;
import com.miloshzelembaba.play.Models.Song;
import com.miloshzelembaba.play.Network.NetworkManager;
import com.miloshzelembaba.play.R;
import com.miloshzelembaba.play.Spotify.SpotifyManager;
import com.miloshzelembaba.play.Spotify.SpotifyUpdateListener;
import com.miloshzelembaba.play.api.Services.FinishSongService;
import com.miloshzelembaba.play.api.Services.GetPartyDetailsService;
import com.miloshzelembaba.play.api.Services.ImageDownloader;
import com.miloshzelembaba.play.api.Services.RemoveSongFromPartyService;
import com.spotify.sdk.android.player.Player;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.VISIBLE;

public class AdminPartyActivity extends BaseParty implements SpotifyUpdateListener {
    public static final String EXTRA_PARTY_ID = "ExtraPartyId";
    public static final String EXTRA_USER = "ExtraUser";

    // Services
    FinishSongService finishSongService = new FinishSongService();
    RemoveSongFromPartyService removeSongFromPartyService = new RemoveSongFromPartyService();

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
    private ImageView cpSongImage;
    private TextView cpSongName;
    private TextView cpArtists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NetworkManager.getInstance().addPartyUpdateListener(this);

        initViews();
        mIsPlaying = false;
        mSpotifyManager = SpotifyManager.getInstance();
        mSpotifyManager.setSpotifyUpdateListener(this);
        mPlayer = mSpotifyManager.getPlayer();

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
    protected void initViews() {
        setContentView(R.layout.admin_activity_party);
        partyMembersIcon = (ImageView) findViewById(R.id.party_members);
        header = (TextView) findViewById(R.id.party_activity_header);
        mSongsListView = (ListView) findViewById(R.id.party_songs);
        mPlaybackControlPlay = (ImageView) findViewById(R.id.music_controls_play);
        mPlaybackControlNextSong = (ImageView) findViewById(R.id.music_controls_next_song);
        mPlaybackControlAddSong = (ImageView) findViewById(R.id.music_controls_add_song);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        cpArtists = (TextView) findViewById(R.id.currently_plauying_song_artists);
        cpSongName = (TextView) findViewById(R.id.currently_playing_song_name);

        cpSongImage = (ImageView) findViewById(R.id.currently_playing_song_image);
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
        }
    }

    private void playSong() {
        if (currentlyPlayingSong != null) { // resume a song
            mIsPlaying = true;
            mPlayer.resume(null);
            mPlaybackControlPlay.setImageResource(R.mipmap.baseline_pause_black_36);
        } else {
            ArrayList<Song> queuedSongs = mParty.getQueuedSongs();
            if (queuedSongs != null && queuedSongs.size() > 0) { // play the next available song
                currentlyPlayingSong = queuedSongs.get(0);
                mPlaybackControlPlay.setImageResource(R.mipmap.baseline_pause_black_36);
                mPlayer.playUri(null, currentlyPlayingSong.getUri(), 0, 0);
                finishSongService.requestService(currentlyPlayingSong,
                    new FinishSongService.FinishSongServiceCallback() {
                        @Override
                        public void onSuccess(Party party) {
                            setParty(party);
                            mIsPlaying = true;
                            setCurrentlyPlayingViews();
                        }

                        @Override
                        public void onFailure(String errorMessage) {

                        }
                    });
            } else { // nothing to play
                mSpotifyManager.pauseSong();

                // send update to guests that no song is in queue
                finishSongService.requestService(mParty.getId(), null, null);

                currentlyPlayingSong = null;
                mIsPlaying = false;
                mPlaybackControlPlay.setImageResource(R.mipmap.baseline_play_arrow_black_36);
                setCurrentlyPlayingViews();
            }
        }
    }

    private void setCurrentlyPlayingViews() {
        if (currentlyPlayingSong != null) {
            cpArtists.setText(currentlyPlayingSong.getSongArtists());
            cpSongImage.setImageBitmap(currentlyPlayingSong.getImage());
            if (currentlyPlayingSong.getImage() == null){
                ImageDownloader.getBitmapFromURL(currentlyPlayingSong, cpSongImage, this);
            }
            cpSongName.setText(currentlyPlayingSong.getSongName());
        } else {
            cpArtists.setText("");
            cpSongImage.setImageBitmap(null);
            cpSongName.setText(getResources().getString(R.string.no_song_playing));
        }
    }

    private void playNextSong() {
        currentlyPlayingSong = null;
        mIsPlaying = false;
        playSong();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == SongSearchActivity.SONG_SEARCH_RESULT && intent != null){
            String serializedSongs = intent.getStringExtra("songs");
            try {
                JSONArray jsonArray = new JSONArray(serializedSongs);
                ArrayList<Song> songs = new ArrayList<>();

                Map<String, Integer> temporarySongMap = new HashMap<>();
                for (Song tmp: mParty.getQueuedSongs()) {
                    temporarySongMap.put(tmp.getUri(), 0);
                }

                for (int i=0; i<jsonArray.length(); ++i){
                    Song song = new Song(jsonArray.getJSONObject(i));

                    if (!temporarySongMap.containsKey(song.getUri())) {
                        songs.add(new Song(jsonArray.getJSONObject(i)));
                    }
                }

                addSongsToParty(songs);
            } catch (Exception e) {
                ErrorService.showErrorMessage(this, "error with multiple songs", ErrorService.ErrorSeverity.HIGH);
            }
        }
    }

    @Override
    public void onSongFinishedPlaying() {
        Log.d("Service debugging", "onSongFinishedPlaying called");
        playNextSong();
    }

    @Override
    public void onLoggedIn() { }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.leave_party_alert)
                .setPositiveButton(R.string.leave, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AdminPartyActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(R.string.stay, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        Dialog d = builder.create();
        d.show();
    }

    @Override
    protected void onDestroy() {
        pauseSong();
        NetworkManager.getInstance().removePartyUpdateListener(this);
        leavePartyService.requestService(user, null);
        super.onDestroy();
    }

    @Override
    public boolean isAdminParty() {
        return true;
    }

    public void deleteSong(Song song) {
        removeSongFromPartyService.requestService(song, new RemoveSongFromPartyService.RemoveSongFromPartyServiceCallback() {
            @Override
            public void onSuccess(Party party) {
                setParty(party);
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }
}
