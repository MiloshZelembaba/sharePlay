package com.miloshzelembaba.play.Activity.PartyActivityStuff;

import com.miloshzelembaba.play.Models.Song;

import java.util.Comparator;

/**
 * Created by miloshzelembaba on 2018-03-21.
 */

public class SongVoteCountComparator implements Comparator<Song> {
    @Override
    public int compare(Song o1, Song o2) {
        return (o1.getVoteCount() > o2.getVoteCount() ? -1 : 1);
    }
}
