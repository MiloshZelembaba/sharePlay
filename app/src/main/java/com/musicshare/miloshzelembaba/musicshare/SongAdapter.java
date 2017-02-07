package com.musicshare.miloshzelembaba.musicshare;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by miloshzelembaba on 2017-02-04.
 */

public class SongAdapter extends ArrayAdapter {
    Context context;
    LayoutInflater inflater;
    public ArrayList<Song> songs = new ArrayList<>();
    Activity baseActivity;

    public SongAdapter(Context context, int textViewResourceId, ArrayList<Song> listItems, Activity base){
        super(context, textViewResourceId, listItems);
        baseActivity = base;
        this.context = context;
        songs = listItems;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void clearPreviousSearch(){
        songs.clear();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v != null) {
            TextView songName = (TextView) v.findViewById(R.id.song_name);
            songName.setText(songs.get(position).getName());
            TextView artistsName = (TextView) v.findViewById(R.id.artist_name);
            artistsName.setText(songs.get(position).getArtists());
        } else {
            v = inflater.inflate(R.layout.song_layout, parent, false);
            TextView songName = (TextView) v.findViewById(R.id.song_name);
            songName.setText(songs.get(position).getName());
            TextView artistsName = (TextView) v.findViewById(R.id.artist_name);
            artistsName.setText(songs.get(position).getArtists());
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PartyActivity)baseActivity).addSong(songs.get(position));
                ((PartyActivity)baseActivity).endSearch();
            }
        });

        return v;
    }

    public void setList(ArrayList<Song> list){
        songs = list;
    }

    public void addSong(Song song){
        songs.add(song);
    }



}
