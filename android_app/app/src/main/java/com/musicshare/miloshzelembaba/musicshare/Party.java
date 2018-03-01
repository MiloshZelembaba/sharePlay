package com.musicshare.miloshzelembaba.musicshare;


/**
 * Created by miloshzelembaba on 2017-01-18.
 */

public class Party {

    static Party instance = null;
    private String partyOwner;
    private String partyName;
    private boolean isPartyOwner = false;
    private SongQueue songQueue = new SongQueue();

    private Party(){}

    public static Party getInstance(){
        if (instance == null){
            instance = new Party();
            return instance;
        }
        return instance;
    }

    public boolean isSongQueueEmpty(){
        return songQueue.isSongQueueEmpty();
    }

    public void addSong(Song song){
        songQueue.addSong(song);
    }

    public void removeSong(Song song) { songQueue.removeSong(song);}

    public void setIsPartyOwner(boolean bool){
        isPartyOwner = bool;
    }

    public void setPartyName(String name){
        partyName = name;
    }
}
