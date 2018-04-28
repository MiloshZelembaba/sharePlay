package com.miloshzelembaba.play.Models;

import com.miloshzelembaba.play.Activity.PartyActivityStuff.SongVoteCountComparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by miloshzelembaba on 2018-03-12.
 */

public class Party extends Serializable {
    private String mId;
    private String mPartyName;
    private User mHost;
    private ArrayList<Song> songs;
    private Song mCurrentlyPlaying;

    public Party(JSONObject object) throws JSONException{
        if (object.has("id")) {
            mId = object.getString("id");
        } else {
            throw new JSONException("invalid json object");
        }

        if (object.has("name")) {
            mPartyName = object.getString("name");
        } else {
            throw new JSONException("invalid json object");
        }

        if (object.has("host")) {
            mHost = new User(object.getJSONObject("host"));
        } else {
            throw new JSONException("invalid json object");
        }

        if (object.has("songs")) {
            addSongs(object.getJSONArray("songs"));

            if (object.has("currently_playing_uri")) {
                String uri = object.getString("currently_playing_uri");

                for (Song song: songs) {
                    if (song.getUri().equals(uri)) {
                        mCurrentlyPlaying = song;
                        break;
                    }
                }
            }
        }


    }

    public JSONObject serialize() throws JSONException{
        JSONObject object = new JSONObject();

        object.put("id", mId);
        object.put("party_name", mPartyName);
        object.put("host", mHost.serialize().toString());

        return object;
    }

    public ArrayList<Song> getQueuedSongs(){
        Collections.sort(songs, new SongVoteCountComparator());
        if (mCurrentlyPlaying != null && songs.contains(mCurrentlyPlaying)) {
            songs.remove(mCurrentlyPlaying);
        }
        return songs;
    }

    public void addSongs(JSONArray array) throws JSONException{
        if (songs != null){
            songs.clear();
        } else {
            songs = new ArrayList<>();
        }

        for (int i=0; i<array.length(); ++i){
            songs.add(new Song(array.getJSONObject(i)));
        }
    }

    public Song getCurrentlyPlaying() {
        return mCurrentlyPlaying;
    }

    public User getHost() {
        return mHost;
    }

    public String getName(){
        return mPartyName;
    }

    public String getId(){
        return mId;
    }

}
