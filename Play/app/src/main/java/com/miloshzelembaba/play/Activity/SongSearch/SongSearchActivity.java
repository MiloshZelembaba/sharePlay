package com.miloshzelembaba.play.Activity.SongSearch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.miloshzelembaba.play.Models.Song;
import com.miloshzelembaba.play.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SongSearchActivity extends FragmentActivity implements OnFragmentInteractionListener {
    public static final int SONG_SEARCH_RESULT = 1;

    // local
    SongSearchActivity thisActivity;
    Map<String, Song> songsToAdd;

    // views
    TabLayout mTabLayout;
    PagerAdapter mPagerAdapter;
    ViewPager mPager;
    Button mAddSongs;
    TextView mNumSongsAdded;

    public interface SongSearchResultCallBack{
        void onSuccess(ArrayList<Song> songs);
        void onFailure(String errorMessage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_search);
        thisActivity = this;
        songsToAdd = new HashMap<>();

        /* init the size of the activity */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        getWindow().setLayout((int)(width * 0.8), (int)(height * 0.6));

        /* init the pager adapter */
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        ArrayList<android.support.v4.app.Fragment> searchActivityFragments = new ArrayList<>();
        searchActivityFragments.add(UserLibraryFragment.newInstance());
        searchActivityFragments.add(SearchFragment.newInstance());
        searchActivityFragments.add(UserPlaylistFragment.newInstance());
        mPagerAdapter.setItems(searchActivityFragments);
        mPager.setAdapter(mPagerAdapter);

        /* init tabs */
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mPager);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        /* init views */
        mAddSongs = (Button) findViewById(R.id.add_songs);
        mNumSongsAdded = (TextView) findViewById(R.id.num_songs_added);
        setupViews();
    }

    public void setupViews() {
        String text = String.format(getResources().getString(R.string.selected_songs), songsToAdd.size());
        mNumSongsAdded.setText(text);

        mAddSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithResult();
            }
        });
    }

    public void onFragmentInteraction(Uri uri){

    }

    public void updateFragment() {
        ((SongFragmentUpdate)mPagerAdapter.getItem(mPager.getCurrentItem())).updateFragment();
    }

    private void finishWithResult() {
        Intent data = new Intent();
        JSONArray array = new JSONArray();

        try {
            for (Song song: songsToAdd.values()) {
                try {
                    array.put(song.serialize());
                } catch (Exception e) {}
            }

            data.putExtra("songs", array.toString());
        } catch (Exception e) {
            data.putExtra("songs", "");
        }

        setResult(SONG_SEARCH_RESULT, data);
        finish();

    }

    public void addSong(Song song){
        if (songsToAdd.containsKey(song.getUri())) {
            songsToAdd.remove(song.getUri());
        } else {
            songsToAdd.put(song.getUri(), song);
        }


        String text = String.format(getResources().getString(R.string.selected_songs), songsToAdd.size());
        mNumSongsAdded.setText(text);
    }

    public boolean containsSong(Song song) {
        return songsToAdd.containsKey(song.getUri());
    }
}
