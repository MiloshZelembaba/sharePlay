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
    private int mVoteCount;


    public Song(String spotifyURI, String name, String artists, Image image){
        this.mSpotifyURI = spotifyURI;
        this.mSongName = name;
        this.mArtists = artists;
        this.mImage = image;
        this.mImageUrl = "nothing yet";
        this.mVoteCount = 1;
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

        if (object.has("vote_count")) {
            mVoteCount = object.getInt("vote_count");
        } else {
            throw new JSONException("invalid json object");
        }
    }

    public String getUri(){
        return mSpotifyURI;
    }

    public String getSongName(){
        return mSongName;
    }

    public String getSongArtists(){
        return mArtists;
    }

    public int getVoteCount(){
        return mVoteCount;
    }

    public void incrementVoteCount(){
        mVoteCount += 1;
    }

    @Override
    public JSONObject serialize() throws JSONException {
        JSONObject object = new JSONObject();

        object.put("id", mId);
        object.put("uri", mSpotifyURI);
        object.put("song_name", mSongName);
        object.put("artists", mArtists);
        object.put("image_url", mImageUrl);
        object.put("vote_count", mVoteCount);

        return object;
    }
}
