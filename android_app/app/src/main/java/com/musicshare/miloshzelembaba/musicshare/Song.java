package com.musicshare.miloshzelembaba.musicshare;

import android.view.View;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by miloshzelembaba on 2017-02-04.
 */

public class Song {

    private String spotifyURI;
    private String name;
    private String artists;
    private Image albumCover;
    private View playlistView;


    public Song(String spotifyURI, String name){
        this.spotifyURI = spotifyURI;
        this.name = name;
    }

    public void setPlaylistView(View v){
        playlistView = v;
    }

    public View getPlaylistView(){
        return playlistView;
    }

    public Song(String spotifyURI, String name, String artists, Image albumCover){
        this.spotifyURI = spotifyURI;
        this.name = name;
        this.artists = artists;
        this.albumCover = albumCover;
    }

    public String getURI(){
        return spotifyURI;
    }

    public String getArtists(){
        return artists;
    }

    public String getName(){
        return name;
    }

    public Image getAlbumCover(){
        return albumCover;
    }
}
