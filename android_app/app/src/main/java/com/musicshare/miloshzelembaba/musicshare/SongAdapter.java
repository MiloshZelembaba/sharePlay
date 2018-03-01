package com.musicshare.miloshzelembaba.musicshare;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
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
        clear();
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
            //getImageBitmap(songs.get(position).getAlbumCover().url,v);
        } else {
            v = inflater.inflate(R.layout.song_layout_search, parent, false);
            TextView songName = (TextView) v.findViewById(R.id.song_name);
            songName.setText(songs.get(position).getName());
            TextView artistsName = (TextView) v.findViewById(R.id.artist_name);
            artistsName.setText(songs.get(position).getArtists());
            getImageBitmap(songs.get(position).getAlbumCover().url,v);

        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                songTapped(view,songs.get(position));
            }
        });

        return v;
    }

    private void songTapped(View view, Song song){ // TODO: check button doesn't show
        if (view.findViewById(R.id.plus_btn).isEnabled()){
            // setup views
            view.findViewById(R.id.plus_btn).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.plus_btn).setEnabled(false);
            view.findViewById(R.id.check_btn).setVisibility(View.VISIBLE);
            view.findViewById(R.id.check_btn).setEnabled(true);
            // add song
            ((PartyActivity)baseActivity).addSong(song);
        } else {
            // setup views
            view.findViewById(R.id.plus_btn).setVisibility(View.VISIBLE);
            view.findViewById(R.id.plus_btn).setEnabled(true);
            view.findViewById(R.id.check_btn).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.check_btn).setEnabled(false);
            // remove song
            ((PartyActivity)baseActivity).removeSong(song, false);
        }
    }

    public void setList(ArrayList<Song> list){
        songs = list;
    }

    public void addSong(Song song){
        songs.add(song);
    }

    private void getImageBitmap(String url, View v) {
        ImageDownloader.getBitmapFromURL(url,v);
    }



}
