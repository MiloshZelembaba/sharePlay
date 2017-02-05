package com.musicshare.miloshzelembaba.musicshare;

/**
 * Created by miloshzelembaba on 2017-02-04.
 */

public class Song {

    private String spotifyURI;
    private String name;


    public Song(String spotifyURI, String name){
        this.spotifyURI = spotifyURI;
        this.name = name;
    }



    public String getName(){
        return name;
    }
    // other info
}
