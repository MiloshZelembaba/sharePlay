package com.miloshzelembaba.play.Activity.PartyActivityStuff;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.miloshzelembaba.play.Models.Party;
import com.miloshzelembaba.play.Models.Song;
import com.miloshzelembaba.play.Models.User;
import com.miloshzelembaba.play.Network.NetworkEventTypeCallbacks.OnPartyUpdated;
import com.miloshzelembaba.play.R;
import com.miloshzelembaba.play.Utils.StringUtil;
import com.miloshzelembaba.play.api.Services.AddSongToPartyService;
import com.miloshzelembaba.play.api.Services.GetPartyDetailsService;
import com.miloshzelembaba.play.api.Services.IncrementSongVoteCountService;
import com.miloshzelembaba.play.api.Services.LeavePartyService;

import java.util.ArrayList;

/**
 * Created by miloshzelembaba on 2018-06-08.
 */

public abstract class BaseParty extends AppCompatActivity implements OnPartyUpdated {
    protected Party mParty;
    protected PartySongsAdapter mPartySongsAdapter;
    protected ListView mSongsListView;
    protected TextView header;
    protected TextView voteCount;
    protected User user;
    protected BaseParty mBaseActivity;
    protected int numVotes;

    // services
    protected GetPartyDetailsService getPartyDetailsService;
    protected AddSongToPartyService addSongToPartyService;
    protected IncrementSongVoteCountService incrementSongVoteCountService;
    protected LeavePartyService leavePartyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseActivity = this;
        numVotes = 10;
    }

    protected void initServices() {
        getPartyDetailsService = new GetPartyDetailsService();
        addSongToPartyService = new AddSongToPartyService();
        incrementSongVoteCountService = new IncrementSongVoteCountService();
        leavePartyService = new LeavePartyService();
    }
    abstract protected void initViews();

    protected void incrementSongCount(Song song) {
        song.incrementVoteCount();
        ArrayList<Song> tmp = new ArrayList<>(mParty.getQueuedSongs());
        mPartySongsAdapter.clear();
        mParty.setSongs(tmp);
        mPartySongsAdapter.addAll(mParty.getQueuedSongs());
        mPartySongsAdapter.notifyDataSetChanged();
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

    protected void addSongsToParty(ArrayList<Song> songs) {
        mPartySongsAdapter.addAll(songs);
        mPartySongsAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Added " + songs.size() + " song(s)", Toast.LENGTH_SHORT).show();
        addSongToPartyService.requestService(user, mParty, songs, new AddSongToPartyService.AddSongToPartyServiceCallback() {
            @Override
            public void onSuccess(Party party) {
                setParty(party);
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }

    protected void setParty(Party party) {
        mParty = party;
        mPartySongsAdapter = new PartySongsAdapter(this, 0, party.getQueuedSongs());
        mSongsListView.setAdapter(mPartySongsAdapter);
        String headerText = "Party Code: " + StringUtil.padZeros(mParty.getId());
        header.setText(headerText);
    }

    private View inflateSong(Song song) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view= inflater.inflate(R.layout.party_song_layout, null, false);
        ((TextView)view.findViewById(R.id.song_name)).setText(song.getSongName());
        ((TextView)view.findViewById(R.id.song_artists)).setText(song.getSongArtists());
        ((TextView)view.findViewById(R.id.song_vote_count)).setText(Integer.toString(song.getVoteCount()));

        return view;
    }

    public boolean isAdminParty() {
        return false;
    }
}
