package com.musicshare.miloshzelembaba.musicshare;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by miloshzelembaba on 2017-02-04.
 */

public class SongListView extends ListActivity {
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<Song> listItems=new ArrayList<Song>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<Song> adapter;
    Context context;

    public SongListView(Context context){
        this.context = context;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_party);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, // should change to song_layout
                listItems);
        setListAdapter(adapter);
    }

    @Override
    public Intent getIntent(){
        Intent intent = new Intent(context, ListActivity.class);
        return intent;
    }

    //METHOD WHICH WILL HANDLE DYNAMIC INSERTION
    public void addItems(Song song) {
        listItems.add(song);
        adapter.notifyDataSetChanged();
    }

}
