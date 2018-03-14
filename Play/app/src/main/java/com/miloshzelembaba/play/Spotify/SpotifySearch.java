package com.miloshzelembaba.play.Spotify;

import android.util.Log;

import com.miloshzelembaba.play.Activity.SongSearch.SongSearchActivity;
import com.miloshzelembaba.play.Models.Song;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by miloshzelembaba on 2017-03-14.
 */

public class SpotifySearch{

    static SpotifyApi api = new SpotifyApi();
    static SpotifyService spotify = api.getService();
    static ArrayList<Song> tmp = new ArrayList<>();

    static public void getResults(String query, final SongSearchActivity.SongSearchResultCallBack callBack){
        //api.setAccessToken(CustomSpotifyPlayer.auth.getAccessToken());

        // TODO: big todo - can we make this async?

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
                    callBack.onSuccess(tmp);
                }

                @Override
                public void failure(RetrofitError error) {
                    callBack.onFailure(error.toString());
                    Log.d("Album failure", error.toString());
                }
            });
        } catch (RetrofitError e){
            callBack.onFailure(e.getResponse().toString());
            System.out.println(e.getResponse().getStatus());
        }
    }


}
