package com.miloshzelembaba.play.Activity.SongSearch;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.miloshzelembaba.play.Models.Song;

import java.util.ArrayList;

/**
 * Created by miloshzelembaba on 2018-03-14.
 */

public class SongSearchAdapter extends ArrayAdapter {
    ArrayList<Song> songs;

    public SongSearchAdapter(Context context, int textViewResourceId, ArrayList<Song> listItems){
        super(context, textViewResourceId, listItems);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        return convertView;
    }

    public void updateData(ArrayList<Song> songs){
        this.songs = songs;
        notifyDataSetChanged();
    }

}
