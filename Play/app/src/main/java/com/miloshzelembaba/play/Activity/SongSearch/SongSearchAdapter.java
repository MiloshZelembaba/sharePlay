package com.miloshzelembaba.play.Activity.SongSearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.miloshzelembaba.play.Image.ImageStore;
import com.miloshzelembaba.play.Models.Song;
import com.miloshzelembaba.play.R;
import com.miloshzelembaba.play.api.Services.ImageDownloader;

import java.util.ArrayList;

/**
 * Created by miloshzelembaba on 2018-03-14.
 */

public class SongSearchAdapter extends ArrayAdapter<Song> {
    SongSearchActivity mBaseActivity;
    ListView listView;

    public SongSearchAdapter(SongSearchActivity context, int textViewResourceId, ArrayList<Song> listItems, ListView l){
        super(context, textViewResourceId, listItems);
        mBaseActivity = context;
        listView = l;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

         if (convertView == null) {
             LayoutInflater inflater = (LayoutInflater) mBaseActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             convertView = inflater.inflate(R.layout.song_layout, parent, false);
        }

        Song song = getItem(position);
        ((TextView)convertView.findViewById(R.id.song_name)).setText(song.getSongName());
        ((TextView)convertView.findViewById(R.id.song_artists)).setText(song.getSongArtists());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaseActivity.addSong(getItem(position));
                listView.invalidateViews();
            }
        });

        if (position == getCount() - 1) {
            mBaseActivity.updateFragment();
        }

        if (mBaseActivity.containsSong(song)) {
            ImageView imageView = ((ImageView)convertView.findViewById(R.id.song_added));
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.mipmap.baseline_done_black_24);
        } else {
            ImageView imageView = ((ImageView)convertView.findViewById(R.id.song_added));
            imageView.setVisibility(View.GONE);
        }

        if (song.getImage() == null && !ImageStore.getInstance().has(song.getImageUrl())){
            ImageDownloader.getBitmapFromURL(song, (ImageView)convertView.findViewById(R.id.song_image), mBaseActivity);
        } else {
            song.setImage(ImageStore.getInstance().getBitmap(song.getImageUrl()));
            ((ImageView)convertView.findViewById(R.id.song_image)).setImageBitmap(song.getImage());
        }

        return convertView;
    }
}
