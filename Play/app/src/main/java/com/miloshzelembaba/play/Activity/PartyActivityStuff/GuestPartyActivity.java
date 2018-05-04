package com.miloshzelembaba.play.Activity.PartyActivityStuff;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.miloshzelembaba.play.Activity.SongSearch.SongSearchActivity;
import com.miloshzelembaba.play.Error.ErrorService;
import com.miloshzelembaba.play.Models.Party;
import com.miloshzelembaba.play.Models.Song;
import com.miloshzelembaba.play.Models.User;
import com.miloshzelembaba.play.Network.NetworkEventTypeCallbacks.OnPartyUpdated;
import com.miloshzelembaba.play.Network.NetworkManager;
import com.miloshzelembaba.play.R;
import com.miloshzelembaba.play.Spotify.SpotifyUpdateListener;
import com.miloshzelembaba.play.Utils.ApplicationUtil;
import com.miloshzelembaba.play.Utils.StringUtil;
import com.miloshzelembaba.play.api.Services.AddSongToPartyService;
import com.miloshzelembaba.play.api.Services.GetPartyDetailsService;
import com.miloshzelembaba.play.api.Services.IncrementSongVoteCountService;
import com.miloshzelembaba.play.api.Services.LeavePartyService;

import org.json.JSONException;
import org.json.JSONObject;

public class GuestPartyActivity extends AppCompatActivity implements OnPartyUpdated, PartyMethods, SpotifyUpdateListener {
    public static final String EXTRA_PARTY_ID = "ExtraPartyId";
    public static final String EXTRA_USER = "ExtraUser";

    // Services
    GetPartyDetailsService getPartyDetailsService;
    AddSongToPartyService addSongToPartyService;
    IncrementSongVoteCountService incrementSongVoteCountService;
    LeavePartyService leavePartyService;

    // Local
    private Context mContext;
    private Party mParty;
    private User user;

    // Views
    private ListView mSongsListView;
    private PartySongsAdapter mPartySongsAdapter;
    private FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_activity_party);
        mContext = this;
        NetworkManager.getInstance().addPartyUpdateListener(this);

        initServices();
        initViews();

        try {
            user = new User(new JSONObject(getIntent().getStringExtra(EXTRA_USER)));
        } catch (JSONException e){
            user = null;
        }

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

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mSongsListView = (ListView) findViewById(R.id.party_songs);

        fab.setOnClickListener(new View.OnClickListener() {
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

    }

    private void initServices() {
        getPartyDetailsService = new GetPartyDetailsService();
        addSongToPartyService = new AddSongToPartyService();
        incrementSongVoteCountService = new IncrementSongVoteCountService();
        leavePartyService = new LeavePartyService();
    }

    // this method should be updated prolly
    private void setParty(Party party) {
        mParty = party;
        mPartySongsAdapter = new PartySongsAdapter(this, 0, party.getQueuedSongs());
        mSongsListView.setAdapter(mPartySongsAdapter);
        setTitle(mParty.getName() + " " + StringUtil.padZeros(mParty.getId()));
    }

    private void addSongToParty(Song song) {
        Toast.makeText(this, "Added " + song.getSongName(), Toast.LENGTH_SHORT).show();
        addSongToPartyService.requestService(user, mParty, song, null);
    }

    @Override
    public void incrementSongCount(Song song){
        incrementSongVoteCountService.requestService(song,
                new IncrementSongVoteCountService.IncrementSongVoteCountServiceCallback() {
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
    public void onPartyUpdated(final Party party) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    setParty(party);
                } catch (Exception e) {}
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
                ErrorService.showErrorMessage(mContext,
                        "Unable to add song",
                        ErrorService.ErrorSeverity.LOW);
            }
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
        leavePartyService.requestService(user, null);
        super.onDestroy();
    }

}
