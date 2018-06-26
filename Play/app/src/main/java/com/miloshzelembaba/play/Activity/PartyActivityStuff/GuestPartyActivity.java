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
import com.miloshzelembaba.play.Models.User;
import com.miloshzelembaba.play.Network.NetworkEventTypeCallbacks.OnHostSwitchEvent;
import com.miloshzelembaba.play.Network.NetworkManager;
import com.miloshzelembaba.play.R;
import com.miloshzelembaba.play.Spotify.SpotifyUpdateListener;
import com.miloshzelembaba.play.Utils.ApplicationUtil;
import com.miloshzelembaba.play.api.Services.GetPartyDetailsService;

import org.json.JSONException;
import org.json.JSONObject;

public class GuestPartyActivity extends BaseParty implements SpotifyUpdateListener, OnHostSwitchEvent {
    public static final String EXTRA_PARTY_ID = "ExtraPartyId";
    public static final String EXTRA_USER = "ExtraUser";

    // Local
    private Context mContext;

    // Views
    private FloatingActionButton fab;
    private ImageView partyMembersIcon;
    private ImageView mPartyControlAddSong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_activity_party);
        mContext = this;
        NetworkManager.getInstance().setHostSwitchListener(this);
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

    @Override
    protected void initViews() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mSongsListView = (ListView) findViewById(R.id.party_songs);
        partyMembersIcon = (ImageView) findViewById(R.id.party_members);
        header = (TextView) findViewById(R.id.party_activity_header);
        mPartyControlAddSong = (ImageView) findViewById(R.id.music_controls_add_song);
        mPartyControlAddSong.setImageResource(R.mipmap.baseline_add_black_36);

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
                } catch (Exception e) {}

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

    }

    @Override
    protected void initServices() {
        super.initServices();
    }

    @Override
    public void onHostSwitchEvent(final String partyId) {
        // TODO: does this need to be run on the UI thread?
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, "you've become the party host!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(mContext, AdminPartyActivity.class);
                intent.putExtra(AdminPartyActivity.EXTRA_PARTY_ID, partyId);
                try {
                    intent.putExtra(AdminPartyActivity.EXTRA_USER, ApplicationUtil.getInstance().getUser().serialize().toString());
                } catch (JSONException e) {
                    ErrorService.showErrorMessage(mContext,
                            "couldn't switch parties",
                            ErrorService.ErrorSeverity.HIGH);
                }
                startActivity(intent);

                finish();
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
