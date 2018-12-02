package com.miloshzelembaba.play.Activity.PartyActivityStuff;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.ArraySwipeAdapter;
import com.miloshzelembaba.play.Error.ErrorService;
import com.miloshzelembaba.play.Image.ImageStore;
import com.miloshzelembaba.play.Models.Party;
import com.miloshzelembaba.play.Models.Song;
import com.miloshzelembaba.play.Models.User;
import com.miloshzelembaba.play.Network.NetworkEventTypeCallbacks.OnPartyUpdated;
import com.miloshzelembaba.play.R;
import com.miloshzelembaba.play.Utils.ApplicationUtil;
import com.miloshzelembaba.play.Utils.StringUtil;
import com.miloshzelembaba.play.api.Services.AddSongToPartyService;
import com.miloshzelembaba.play.api.Services.GetPartyDetailsService;
import com.miloshzelembaba.play.api.Services.ImageDownloader;
import com.miloshzelembaba.play.api.Services.IncrementSongVoteCountService;
import com.miloshzelembaba.play.api.Services.LeavePartyService;

import java.util.ArrayList;

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
    protected GetPartyDetailsService getPartyDetailsService = new GetPartyDetailsService();
    protected AddSongToPartyService addSongToPartyService = new AddSongToPartyService();
    protected IncrementSongVoteCountService incrementSongVoteCountService = new IncrementSongVoteCountService();
    protected LeavePartyService leavePartyService = new LeavePartyService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseActivity = this;
        numVotes = 10;
        user = ApplicationUtil.getInstance().getUser();
    }

    abstract protected void initViews();

    protected void incrementSongCount(Song song) {
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
                    ErrorService.showErrorMessage(BaseParty.this, "error updating party", ErrorService.ErrorSeverity.HIGH);
                }
            }
        });
    }

    protected void addSongsToParty(ArrayList<Song> songs) {
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
        if (mPartySongsAdapter == null) {
            mPartySongsAdapter = new PartySongsAdapter(this, 0, party.getQueuedSongs());
            mSongsListView.setAdapter(mPartySongsAdapter);
        } else {
            mPartySongsAdapter.clear();
            mPartySongsAdapter.addAll(party.getQueuedSongs());
        }

        String headerText = "Party Code: " + StringUtil.padZeros(mParty.getId());
        header.setText(headerText);
    }

    public boolean isAdminParty() {
        return false;
    }

    private class PartySongsAdapter extends ArraySwipeAdapter<Song> {
        private BaseParty mBaseActivity;

        public PartySongsAdapter(BaseParty context, int textViewResourceId, ArrayList<Song> songs){
            super(context, textViewResourceId, songs);
            mBaseActivity = context;
        }

        @Override
        @NonNull
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mBaseActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.party_song_layout, parent, false);
            }

            final Song song = ((Song)getItem(position));
            ((TextView)convertView.findViewById(R.id.song_name)).setText(song.getSongName());
            ((TextView)convertView.findViewById(R.id.song_artists)).setText(song.getSongArtists());
            ((TextView)convertView.findViewById(R.id.song_vote_count)).setText(Integer.toString(song.getVoteCount()));

            if (song.isCurrentlyPlaying()) {
//            ((TextView) convertView.findViewById(R.id.song_name)).setTextColor(ContextCompat.getColor(getContext(), R.color.spotify_green));
//            ((TextView) convertView.findViewById(R.id.song_artists)).setTextColor(ContextCompat.getColor(getContext(), R.color.spotify_green));
//            ((TextView) convertView.findViewById(R.id.song_vote_count)).setTextColor(ContextCompat.getColor(getContext(), R.color.spotify_green));
            } else {
                ((TextView) convertView.findViewById(R.id.song_name)).setTextColor(ContextCompat.getColor(getContext(), R.color.gray2));
                ((TextView) convertView.findViewById(R.id.song_artists)).setTextColor(ContextCompat.getColor(getContext(), R.color.gray2));
                ((TextView) convertView.findViewById(R.id.song_vote_count)).setTextColor(ContextCompat.getColor(getContext(), R.color.gray2));
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!song.isBeingSwiped()) {
                            mBaseActivity.incrementSongCount(song);
                        }
                    }
                });
            }


            if (song.getImage() == null && !ImageStore.getInstance().has(song.getImageUrl())){
                ImageDownloader.getBitmapFromURL(song, (ImageView)convertView.findViewById(R.id.song_image), mBaseActivity);
            } else {
                song.setImage(ImageStore.getInstance().getBitmap(song.getImageUrl()));
                ((ImageView)convertView.findViewById(R.id.song_image)).setImageBitmap(song.getImage());
            }

            SwipeLayout swipeLayout = (SwipeLayout) convertView;
            swipeLayout.setSwipeEnabled(false);
            if (mBaseActivity.isAdminParty()) {
                swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
                swipeLayout.setSwipeEnabled(true);
                swipeLayout.addDrag(SwipeLayout.DragEdge.Left, convertView.findViewById(R.id.bottom_wrapper));
                swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                    @Override
                    public void onClose(SwipeLayout layout) {
                        song.setIsBeingSwiped(false);
                    }

                    @Override
                    public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                        //you are swiping.
                    }

                    @Override
                    public void onStartOpen(SwipeLayout layout) {
                        song.setIsBeingSwiped(true);
                    }

                    @Override
                    public void onOpen(SwipeLayout layout) {
                        //when the BottomView totally show.
                        song.setIsBeingSwiped(false);
                        ((AdminPartyActivity)mBaseActivity).deleteSong(song);
                    }

                    @Override
                    public void onStartClose(SwipeLayout layout) {
                        song.setIsBeingSwiped(true);
                    }

                    @Override
                    public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                        if (xvel != 0 || yvel != 0) {
                            song.setIsBeingSwiped(true);
                        }
                    }
                });
            }

            return convertView;
        }

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return R.layout.party_song_layout;
        }

    }
}
