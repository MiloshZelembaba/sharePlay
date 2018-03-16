package com.miloshzelembaba.play.Models;

import org.json.JSONException;
import org.json.JSONObject;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by miloshzelembaba on 2018-03-14.
 */

public class Song extends Serializable {
    private String mId;
    private String mSpotifyURI;
    private String mSongName;
    private String mArtists;
    private Image mImage;
    private String mImageUrl;


    public Song(String spotifyURI, String name, String artists, Image image){
        this.mSpotifyURI = spotifyURI;
        this.mSongName = name;
        this.mArtists = artists;
        this.mImage = image;
        this.mImageUrl = "nothing yet";
    }

    public Song(JSONObject object) throws JSONException{
        if (object.has("id")) {
            mId = object.getString("id");
        }

        if (object.has("song_name")) {
            mSongName = object.getString("song_name");
        } else {
            throw new JSONException("invalid json object");
        }

        if (object.has("artists")) {
            mArtists = object.getString("artists");
        } else {
            throw new JSONException("invalid json object");
        }

        if (object.has("image_url")) {
            mImageUrl = object.getString("image_url");
        } else {
            throw new JSONException("invalid json object");
        }

        if (object.has("uri")) {
            mSpotifyURI = object.getString("uri");
        } else {
            throw new JSONException("invalid json object");
        }
    }

    public String getSongName(){
        return mSongName;
    }

    public String getSongArtists(){
        return mArtists;
    }

    @Override
    public JSONObject serialize() throws JSONException {
        JSONObject object = new JSONObject();

        object.put("id", mId);
        object.put("uri", mSpotifyURI);
        object.put("song_name", mSongName);
        object.put("artists", mArtists);
        object.put("image_url", mImageUrl);

        return object;
    }
}
