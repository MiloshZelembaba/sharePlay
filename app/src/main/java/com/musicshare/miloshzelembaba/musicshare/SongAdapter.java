package com.musicshare.miloshzelembaba.musicshare;

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
    ArrayAdapter<Song> adapter;
    Context context;
    LayoutInflater inflater;
    ArrayList<Song> songs = new ArrayList<>();

    public SongAdapter(Context context, int textViewResourceId, ArrayList<Song> listItems){
        super(context, textViewResourceId, listItems);
        this.context = context;
        songs = listItems;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v != null) {
            TextView songName = (TextView) v.findViewById(R.id.song_name);
            songName.setText(songs.get(position).getName());
        } else {
            v = inflater.inflate(R.layout.song_layout, parent, false);
            TextView songName = (TextView) v.findViewById(R.id.song_name);
            songName.setText(songs.get(position).getName());
        }

        return v;
    }

    public void addSong(Song song){
        songs.add(song);
    }



}
