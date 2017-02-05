package com.musicshare.miloshzelembaba.musicshare;

/**
 * Created by miloshzelembaba on 2017-02-04.
 */

public class Song {

    private String spotifyURI;
    private String name;
    private String artists;


    public Song(String spotifyURI, String name){
        this.spotifyURI = spotifyURI;
        this.name = name;
    }

    public Song(String spotifyURI, String name, String artists){
        this.spotifyURI = spotifyURI;
        this.name = name;
        this.artists = artists;
    }

    public String getURI(){
        return spotifyURI;
    }



    public String getName(){
        return name;
    }
    // other info
}
