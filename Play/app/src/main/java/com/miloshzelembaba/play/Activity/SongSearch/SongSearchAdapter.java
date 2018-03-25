package com.miloshzelembaba.play.Activity.SongSearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.miloshzelembaba.play.Models.Song;
import com.miloshzelembaba.play.R;
import com.miloshzelembaba.play.api.Services.ImageDownloader;

import java.util.ArrayList;

/**
 * Created by miloshzelembaba on 2018-03-14.
 */

public class SongSearchAdapter extends ArrayAdapter {
//    ArrayList<Song> songs;
    SongSearchActivity baseActivity;

    public SongSearchAdapter(SongSearchActivity context, int textViewResourceId, ArrayList<Song> listItems){
        super(context, textViewResourceId, listItems);
//        songs = listItems;

        baseActivity = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

         if (convertView == null) {
             LayoutInflater inflater = (LayoutInflater) baseActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             convertView = inflater.inflate(R.layout.song_layout, parent, false);
        }

        Song song = ((Song)getItem(position));
        ((TextView)convertView.findViewById(R.id.song_name)).setText(song.getSongName());
        ((TextView)convertView.findViewById(R.id.song_artists)).setText(song.getSongArtists());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseActivity.addSong((Song)getItem(position));
            }
        });

        if (song.getImage() == null){
            ImageDownloader.getBitmapFromURL(song, convertView);
        } else {
            ((ImageView)convertView.findViewById(R.id.song_image)).setImageBitmap(song.getImage());
        }

        return convertView;
    }

    public void updateData(ArrayList<Song> songs){
//        this.songs = songs;
        notifyDataSetChanged();
    }

}
