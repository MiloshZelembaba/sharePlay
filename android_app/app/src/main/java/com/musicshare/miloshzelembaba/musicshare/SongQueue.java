package com.musicshare.miloshzelembaba.musicshare;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by miloshzelembaba on 2017-02-04.
 */

public class SongQueue {
    private Queue<Song> songQueue = new LinkedList<Song>();

    public boolean isSongQueueEmpty(){
        return songQueue.isEmpty();
    }

    public void addSong(Song song){
        songQueue.add(song);
    }

    public void removeSong(Song song) { songQueue.remove(song); }
}
