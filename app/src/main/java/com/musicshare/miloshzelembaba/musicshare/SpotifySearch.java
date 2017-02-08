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
import kaaes.spotify.webapi.android.models.ArtistSimple;
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

    static public void getResults(String query, final PartyActivity partyActivity){
        //api.setAccessToken(CustomSpotifyPlayer.auth.getAccessToken());

        try {
            spotify.searchTracks(query, new Callback<TracksPager>() {
                @Override
                public void success(TracksPager tracksPager, Response response) {
                    tmp =  new ArrayList<>();
                    for (Track track: tracksPager.tracks.items){
                        String allArtists = track.artists.get(0).name;
                        for (int i=1; i < track.artists.size(); ++i){ // notice that it starts on i=1
                            allArtists += " & " + track.artists.get(i).name;
                        }
                        Song song = new Song(track.uri, track.name, allArtists, track.album.images.get(0));
                        tmp.add(song);
                    }
                    partyActivity.onSearchResult(tmp);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("Album failure", error.toString());
                }
            });
        } catch (RetrofitError e){
            System.out.println(e.getResponse().getStatus());
        }
    }


}
