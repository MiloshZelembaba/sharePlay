package com.miloshzelembaba.play.Activity.SongSearch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;

import com.miloshzelembaba.play.Models.Song;
import com.miloshzelembaba.play.R;

import java.util.ArrayList;

public class SongSearchActivity extends FragmentActivity implements OnFragmentInteractionListener {
    public static final int SONG_SEARCH_RESULT = 1;

    SongSearchActivity thisActivity;


    TabLayout mTabLayout;
    PagerAdapter mPagerAdapter;
    ViewPager mPager;

    public interface SongSearchResultCallBack{
        void onSuccess(ArrayList<Song> songs);
        void onFailure(String errorMessage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_search);
        thisActivity = this;

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
        searchActivityFragments.add(SearchFragment.newInstance());
        searchActivityFragments.add(UserLibraryFragment.newInstance());
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

    }

    public void onFragmentInteraction(Uri uri){

    }

    public void updateFragment() {
        ((SongFragmentUpdate)mPagerAdapter.getItem(mPager.getCurrentItem())).updateFragment();
    }

    public void addSong(Song song){
        Intent data = new Intent();
        try {
            data.putExtra("song", song.serialize().toString());
        } catch (Exception e){
            data.putExtra("song", "");
        }
        setResult(SONG_SEARCH_RESULT, data);
        finish();
    }
}
