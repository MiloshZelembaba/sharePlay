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
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.TracksToRemove;

/**
 * Created by miloshzelembaba on 2017-02-04.
 */

public class PlaylistAdapter extends ArrayAdapter {
    Context context;
    LayoutInflater inflater;
    public ArrayList<Song> songs = new ArrayList<>();
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
            TextView removeButton = (TextView) v.findViewById(R.id.remove_song);
            removeButton.setVisibility(View.VISIBLE);
            getImageBitmap(songs.get(position).getAlbumCover().url,v);
            //ImageView albumCover = (ImageView) v.findViewById(R.id.album_cover);
            //albumCover.setImageBitmap(getImageBitmap(songs.get(position).getAlbumCover().url));
        } else {
            v = inflater.inflate(R.layout.song_layout, parent, false);
            TextView songName = (TextView) v.findViewById(R.id.song_name);
            songName.setText(songs.get(position).getName());
            TextView artistsName = (TextView) v.findViewById(R.id.artist_name);
            artistsName.setText(songs.get(position).getArtists());
            TextView removeButton = (TextView) v.findViewById(R.id.remove_song);
            removeButton.setVisibility(View.VISIBLE);
            getImageBitmap(songs.get(position).getAlbumCover().url,v);
            //ImageView albumCover = (ImageView) v.findViewById(R.id.album_cover);
            //albumCover.setImageBitmap(getImageBitmap(songs.get(position).getAlbumCover().url));
        }

        TextView removeButton = (TextView) v.findViewById(R.id.remove_song);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PartyActivity)baseActivity).removeSong(songs.get(position));
            }
        });

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PartyActivity)baseActivity).playSong(songs.get(position));
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
        songs.remove(song);
    }

    private void getImageBitmap(String url, View v) {
        ImageDownloader.getBitmapFromURL(url,v);
//        Bitmap bm = null;
//        try {
//            URL aURL = new URL(url);
//            URLConnection conn = aURL.openConnection();
//            conn.connect();
//            InputStream is = conn.getInputStream();
//            BufferedInputStream bis = new BufferedInputStream(is);
//            bm = BitmapFactory.decodeStream(bis);
//            bis.close();
//            is.close();
//        } catch (IOException e) {
//            Log.e(this.toString(), "Error getting bitmap", e);
//        }
//        return bm;
    }



}
