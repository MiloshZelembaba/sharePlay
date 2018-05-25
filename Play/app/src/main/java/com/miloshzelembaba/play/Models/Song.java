package com.miloshzelembaba.play.Models;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by miloshzelembaba on 2018-03-14.
 */

public class Song extends Serializable {
    private String mId;
    private String mSpotifyURI;
    private String mSongName;
    private String mArtists;
    private String mImageUrl;
    private Bitmap mImage;
    private int mVoteCount;
    private boolean mIsCurrentlyPlaying;


    public Song(String spotifyURI, String name, String artists, List<Image> images){
        this.mSpotifyURI = spotifyURI;
        this.mSongName = name;
        this.mArtists = artists;
        this.mImageUrl = (images != null && images.size() > 0 ? images.get(0).url : null);
        this.mVoteCount = 1;
        mIsCurrentlyPlaying = false;
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

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImage(Bitmap bitmap) {
        mImage = bitmap;
    }

    public Bitmap getImage() {
        return mImage;
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

    public void setIsCurrentlyPlaying(boolean bool) {
        mIsCurrentlyPlaying = bool;
    }

    public boolean isCurrentlyPlaying() {
        return mIsCurrentlyPlaying;
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
