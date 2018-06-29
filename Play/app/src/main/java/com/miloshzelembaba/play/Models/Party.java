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
    private ArrayList<User> partyMembers;
    private Song mCurrentlyPlaying;

    public Party(JSONObject object) throws JSONException{
        if (object.has("id")) {
            mId = object.getString("id");
        } else {
            throw new JSONException("invalid json object");
        }

        if (object.has("party_name")) {
            mPartyName = object.getString("party_name");
        } else {
            throw new JSONException("invalid json object");
        }

        if (object.has("host")) {
            mHost = new User(object.getJSONObject("host"));
        } else {
            throw new JSONException("invalid json object");
        }

        if (object.has("party_members")) {
            addPartyMembers(object.getJSONArray("party_members"));
        } else {
            throw new JSONException("invalid json object");
        }

        if (object.has("songs")) {
            addSongs(object.getJSONArray("songs"));

            mCurrentlyPlaying = null;
            if (object.has("current_song_uri")) {
                String uri = object.getString("current_song_uri");
                String songName = object.getString("current_song_name");
                String artists = object.getString("current_song_artists");
                String imageUrl = object.getString("current_song_imageUrl");

                if (uri.equals("")) {
                    mCurrentlyPlaying = null;
                } else {
                    mCurrentlyPlaying = new Song(uri, songName, artists, imageUrl);
                }
            }
        }


    }

    public JSONObject serialize() throws JSONException{
        JSONObject object = new JSONObject();

        object.put("id", mId);
        object.put("party_name", mPartyName);
        object.put("host", mHost.serialize());
        object.put("party_members",  createJsonArray(partyMembers));

        return object;
    }

    private JSONArray createJsonArray(ArrayList<User> objects) { // should be extended more generally
        JSONArray array = new JSONArray();

        for (User user: objects) {
            try {
                array.put(user.serialize());
            } catch(Exception e) {}
        }

        return array;
    }

    public ArrayList<Song> getQueuedSongs(){
        Collections.sort(songs, new SongVoteCountComparator());
        if (mCurrentlyPlaying != null && songs.contains(mCurrentlyPlaying)) {
            songs.remove(mCurrentlyPlaying);
        }
        return songs;
    }

    public void addPartyMembers(JSONArray array) throws JSONException{
        if (partyMembers != null){
            partyMembers.clear();
        } else {
            partyMembers= new ArrayList<>();
        }

        for (int i=0; i<array.length(); ++i){
            partyMembers.add(new User(array.getJSONObject(i)));
        }
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

    public ArrayList<User> getPartyMembers() {
        return partyMembers;
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
