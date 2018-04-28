package com.miloshzelembaba.play.Activity.PartyActivityStuff;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.miloshzelembaba.play.Models.Song;
import com.miloshzelembaba.play.R;

import java.util.ArrayList;

/**
 * Created by miloshzelembaba on 2018-03-16.
 */

public class PartySongsAdapter extends ArrayAdapter {
    Activity mBaseActivity;

    public PartySongsAdapter(Activity context, int textViewResourceId, ArrayList<Song> songs){
        super(context, textViewResourceId, songs);
        mBaseActivity = context;
    }

    @Override
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
            ((TextView) convertView.findViewById(R.id.song_name)).setTextColor(ContextCompat.getColor(getContext(), R.color.spotify_green));
            ((TextView) convertView.findViewById(R.id.song_artists)).setTextColor(ContextCompat.getColor(getContext(), R.color.spotify_green));
            ((TextView) convertView.findViewById(R.id.song_vote_count)).setTextColor(ContextCompat.getColor(getContext(), R.color.spotify_green));
        } else {
            ((TextView) convertView.findViewById(R.id.song_name)).setTextColor(ContextCompat.getColor(getContext(), R.color.gray2));
            ((TextView) convertView.findViewById(R.id.song_artists)).setTextColor(ContextCompat.getColor(getContext(), R.color.gray2));
            ((TextView) convertView.findViewById(R.id.song_vote_count)).setTextColor(ContextCompat.getColor(getContext(), R.color.gray2));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((PartyMethods)mBaseActivity).incrementSongCount(song);
                }
            });
        }


//        if (song.getImage() == null){
//            ImageDownloader.getBitmapFromURL(song, convertView, mBaseActivity);
//        } else {
//            ((ImageView)convertView.findViewById(R.id.song_image)).setImageBitmap(song.getImage());
//        }

        return convertView;
    }

}
