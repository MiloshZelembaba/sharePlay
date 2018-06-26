package com.miloshzelembaba.play.Activity.SongSearch;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by miloshzelembaba on 2018-06-26.
 */

public class PagerAdapter extends FragmentPagerAdapter {
    private final int NUM_ITEMS = 3;
    ArrayList<Fragment> fragmentArrayList;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentArrayList = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    public void setItems(ArrayList<Fragment> items) {
        fragmentArrayList.addAll(items);
    }

    @Override
    public String getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Search";
            case 1:
                return "Your Library";
            case 2:
                return "Playlists";
            default:
                return "Unknown";
        }
    }
}
