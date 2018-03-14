package com.miloshzelembaba.play.Models;

import org.json.JSONObject;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by miloshzelembaba on 2018-03-14.
 */

public class Song extends Serializable {
    private String mSpotifyURI;
    private String mSongName;
    private String mArtists;
    private Image mAlbumCover;


    public Song(String spotifyURI, String name, String artists, Image albumCover){
        this.mSpotifyURI = spotifyURI;
        this.mSongName = name;
        this.mArtists = artists;
        this.mAlbumCover = albumCover;
    }

    @Override
    public JSONObject serialize(){
        JSONObject object = new JSONObject();

        return object;
    }
}
