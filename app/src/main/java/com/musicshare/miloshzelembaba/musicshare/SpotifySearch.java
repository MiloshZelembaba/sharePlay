package com.musicshare.miloshzelembaba.musicshare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.lang.reflect.Array;
import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by miloshzelembaba on 2017-02-05.
 */

public class SpotifySearch{

    static SpotifyApi api = new SpotifyApi();
    static SpotifyService spotify = api.getService();
    static ArrayList<Song> tmp = new ArrayList<>();

    static public ArrayList<Song> getResults(String query){
        //api.setAccessToken(CustomSpotifyPlayer.auth.getAccessToken());

        try {
            spotify.searchTracks(query, new Callback<TracksPager>() {
                @Override
                public void success(TracksPager tracksPager, Response response) {
                    for (Track track: tracksPager.tracks.items){
                        Song song = new Song(track.uri, track.name);
                        tmp.add(song);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("Album failure", error.toString());
                }
            });
        } catch (RetrofitError e){
            System.out.println(e.getResponse().getStatus());
        }
        return tmp;
    }


}
