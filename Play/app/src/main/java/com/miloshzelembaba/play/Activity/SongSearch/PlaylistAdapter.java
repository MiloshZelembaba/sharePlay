package com.miloshzelembaba.play.Activity.SongSearch;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.miloshzelembaba.play.Image.ImageStore;
import com.miloshzelembaba.play.R;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.PlaylistSimple;

/**
 * Created by miloshzelembaba on 2018-07-05.
 */

public class PlaylistAdapter extends ArrayAdapter<PlaylistSimple> {
    SongSearchActivity mBaseActivity;
    ListView listView;

    public PlaylistAdapter(SongSearchActivity context, int textViewResourceId, ArrayList<PlaylistSimple> listItems, ListView l){
        super(context, textViewResourceId, listItems);
        mBaseActivity = context;
        listView = l;
    }

    @Override
    @NonNull
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mBaseActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.playlist_layout, parent, false);
        }

        PlaylistSimple playlist = getItem(position);
        Image playlistImage = (playlist.images.size() < 3 ? playlist.images.get(0) : playlist.images.get(1));
        Bitmap playlistBitmap = (ImageStore.getInstance().getBitmap(playlistImage.url));
        ((ImageView)convertView.findViewById(R.id.playlist_image)).setImageBitmap(playlistBitmap);

        ((TextView)convertView.findViewById(R.id.playlist_name)).setText(playlist.name);

        return convertView;
    }
}
