package com.musicshare.miloshzelembaba.musicshare;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.TracksToRemove;

/**
 * Created by miloshzelembaba on 2017-02-04.
 */

/*
 * TODO: trying to remove the views properly from the removeSong() method so that the view dissapears when the song is deleted
 * shits gay tho.
 */

public class PlaylistAdapter extends ArrayAdapter {
    Context context;
    LayoutInflater inflater;
    public ArrayList<Song> songs = new ArrayList<>();
    public ArrayList<View> views = new ArrayList<>();
    Activity baseActivity;

    public PlaylistAdapter(Context context, int textViewResourceId, ArrayList<Song> listItems, Activity base){
        super(context, textViewResourceId, listItems);
        baseActivity = base;
        this.context = context;
        songs = listItems;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v != null) {
            TextView songName = (TextView) v.findViewById(R.id.song_name);
            songName.setText(songs.get(position).getName());
            TextView artistsName = (TextView) v.findViewById(R.id.artist_name);
            artistsName.setText(songs.get(position).getArtists());
            getImageBitmap(songs.get(position).getAlbumCover().url,v);
        } else {
            v = inflater.inflate(R.layout.song_layout, parent, false);
            TextView songName = (TextView) v.findViewById(R.id.song_name);
            songName.setText(songs.get(position).getName());
            TextView artistsName = (TextView) v.findViewById(R.id.artist_name);
            artistsName.setText(songs.get(position).getArtists());
            getImageBitmap(songs.get(position).getAlbumCover().url,v);
            views.add(v);
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PartyActivity)baseActivity).playSong(songs.get(position));
            }
        });

        v.setOnTouchListener(new OnSwipeTouchListener(context) {

            @Override
            public void onSwipeLeft(){
                ((PartyActivity)baseActivity).removeSong(songs.get(position),true);
            }


            @Override
            public void onSwipeRight(){
                ((PartyActivity)baseActivity).removeSong(songs.get(position),true);
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

    public void removeSong(Song song){
        int position = songs.indexOf(song);
        songs.remove(position);
        remove(views.get(position));
        views.remove(position);
    }

    private void getImageBitmap(String url, View v) {
        ImageDownloader.getBitmapFromURL(url,v);
    }



}
