package com.miloshzelembaba.play.Activity.PartyActivityStuff;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.miloshzelembaba.play.Activity.PartyMembers.PartyMembersActivity;
import com.miloshzelembaba.play.Activity.SongSearch.SongSearchActivity;
import com.miloshzelembaba.play.Error.ErrorService;
import com.miloshzelembaba.play.Models.Party;
import com.miloshzelembaba.play.Models.Song;
import com.miloshzelembaba.play.Network.NetworkEventTypeCallbacks.OnHostSwitchEvent;
import com.miloshzelembaba.play.Network.NetworkManager;
import com.miloshzelembaba.play.R;
import com.miloshzelembaba.play.Spotify.SpotifyUpdateListener;
import com.miloshzelembaba.play.Utils.ApplicationUtil;
import com.miloshzelembaba.play.api.Services.GetPartyDetailsService;
import com.miloshzelembaba.play.api.Services.ImageDownloader;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GuestPartyActivity extends BaseParty implements SpotifyUpdateListener, OnHostSwitchEvent {
    public static final String EXTRA_PARTY_ID = "ExtraPartyId";
    public static final String EXTRA_USER = "ExtraUser";

    // Local
    private Context mContext;
    private boolean performingHostSwitch;

    // Views
    private FloatingActionButton fab;
    private ImageView partyMembersIcon;
    private ImageView mPartyControlAddSong;
    private ImageView cpSongImage;
    private TextView cpSongName;
    private TextView cpArtists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_activity_party);
        mContext = this;
        performingHostSwitch = false;
        NetworkManager.getInstance().setHostSwitchListener(this);
        NetworkManager.getInstance().addPartyUpdateListener(this);

        initViews();

        // Get partyId from extras and get the party details
        String partyId = getIntent().getStringExtra(EXTRA_PARTY_ID);
        getPartyDetailsService.requestService(partyId,
                new GetPartyDetailsService.GetPartyDetailsServiceCallback() {
                    @Override
                    public void onSuccess(Party party) {
                        setParty(party);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        ErrorService.showErrorMessage(mContext,
                                errorMessage,
                                ErrorService.ErrorSeverity.HIGH);
                    }
                });
    }

    @Override
    protected void initViews() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mSongsListView = (ListView) findViewById(R.id.party_songs);
        partyMembersIcon = (ImageView) findViewById(R.id.party_members);
        header = (TextView) findViewById(R.id.party_activity_header);
        mPartyControlAddSong = (ImageView) findViewById(R.id.music_controls_add_song);
        mPartyControlAddSong.setImageResource(R.mipmap.baseline_add_black_36);
        cpArtists = (TextView) findViewById(R.id.currently_plauying_song_artists);
        cpSongName = (TextView) findViewById(R.id.currently_playing_song_name);
        cpSongImage = (ImageView) findViewById(R.id.currently_playing_song_image);
        voteCount = (TextView) findViewById(R.id.vote_count);

        partyMembersIcon.setImageResource(R.drawable.baseline_supervisor_account_white_24dp);
        partyMembersIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mParty == null || mParty.getHost() == null) { // host should only be null for old parties when leaving parties wasn't fully consistent yet
                    return;
                }
                Intent intent = new Intent(GuestPartyActivity.this, PartyMembersActivity.class);
                try {
                    intent.putExtra(PartyMembersActivity.PARTY, mParty.serialize().toString());
                } catch (Exception e) {
                }

                startActivity(intent);
            }
        });

        mPartyControlAddSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ApplicationUtil.getInstance().getUser().isTemporaryUser()) {
                    ErrorService.showErrorMessage(mContext,
                            "you must have a spotify account to add songs, sorry",
                            ErrorService.ErrorSeverity.LOW);
                } else {
                    startActivityForResult(new Intent(GuestPartyActivity.this, SongSearchActivity.class), SongSearchActivity.SONG_SEARCH_RESULT);
                }
            }
        });

        voteCount.setText(numVotes + "");
    }

    @Override
    protected void incrementSongCount(Song song) {
        if (numVotes <= 0) {
            Toast.makeText(this, "Not Enough votes", Toast.LENGTH_SHORT).show();
            return;
        }
        numVotes--;
        if (voteCount != null) {
            voteCount.setText(numVotes + "");
            super.incrementSongCount(song);
        }
    }

    @Override
    public void onHostSwitchEvent(final String partyId) {
        // TODO: does this need to be run on the UI thread?
        performingHostSwitch = true;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, "you've become the party host!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(mContext, AdminPartyActivity.class);
                intent.putExtra(AdminPartyActivity.EXTRA_PARTY_ID, partyId);
                startActivity(intent);

                finish();
            }
        });
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
    protected void setParty(Party party) {
        super.setParty(party);
        setCurrentlyPlayingViews();
    }

    private void setCurrentlyPlayingViews() {
        String previousSongName = cpSongName.getText().toString();
        Song currentlyPlayingSong = mParty.getCurrentlyPlaying();
        if (currentlyPlayingSong != null) {
            if (!previousSongName.equals(currentlyPlayingSong.getSongName())) {
                numVotes = 10;
                voteCount.setText(numVotes + "");
            }

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

    @Override
    public void onSongFinishedPlaying() {
        // unused for guest parties
    }

    @Override
    public void onLoggedIn() {
        // unused for guest parties
    }

    @Override
    protected void onDestroy() {
        NetworkManager.getInstance().setHostSwitchListener(null);
        NetworkManager.getInstance().removePartyUpdateListener(this);
        if (!performingHostSwitch) {
            leavePartyService.requestService(user, null);
        }
        super.onDestroy();
    }

}
