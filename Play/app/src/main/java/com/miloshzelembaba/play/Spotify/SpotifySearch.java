package com.miloshzelembaba.play.Spotify;

import com.miloshzelembaba.play.Activity.SongSearch.SongSearchActivity;
import com.miloshzelembaba.play.Models.Song;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.SavedTrack;
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

    static public void getUserLibrary(int offset, int limit, final SongSearchActivity.SongSearchResultCallBack callBack) {
        Map<String, Object> options = new HashMap<>();
        options.put("offset", offset);
        options.put("limit", limit);

        api.setAccessToken(SpotifyManager.ACCESS_TOKEN);

        try {
            spotify.getMySavedTracks(options, new Callback<Pager<SavedTrack>>() {
                @Override
                public void success(Pager<SavedTrack> tracksPager, Response response) {
                    tmp =  new ArrayList<>();
                    for (SavedTrack savedTrack: tracksPager.items){
                        Track track = savedTrack.track;
                        String allArtists = track.artists.get(0).name;
                        for (int i=1; i < track.artists.size(); ++i){ // notice that it starts on i=1
                            allArtists += " & " + track.artists.get(i).name;
                        }
                        Song song = new Song(track.uri, track.name, allArtists, track.album.images);
                        tmp.add(song);
                    }
                    callBack.onSuccess(tmp);
                }

                @Override
                public void failure(RetrofitError error) {
                    callBack.onFailure(error.toString());
                }
            });
        } catch (RetrofitError e){
            callBack.onFailure(e.getResponse().toString());
        }
    }

    static public void getResults(String query, int offset, int limit,  final SongSearchActivity.SongSearchResultCallBack callBack){
        api.setAccessToken(SpotifyManager.ACCESS_TOKEN);

        // TODO: should we make this async?
        // TODO: i think we should also make this part of the spotify manager
        // - I don't think we need to since you can't do anything on the ui anyways until the
        // result comes back
        // - But when the user scrolls to the bottom the results list and more results are loaded,
        // that part should be async so we might as well make everything async

        try {
            Map<String, Object> options = new HashMap<>();
            options.put("offset", offset);
            options.put("limit", limit);
            spotify.searchTracks(query, options, new Callback<TracksPager>() {
                @Override
                public void success(TracksPager tracksPager, Response response) {
                    tmp =  new ArrayList<>();
                    for (Track track: tracksPager.tracks.items){
                        String allArtists = track.artists.get(0).name;
                        for (int i=1; i < track.artists.size(); ++i){ // notice that it starts on i=1
                            allArtists += " & " + track.artists.get(i).name;
                        }
                        Song song = new Song(track.uri, track.name, allArtists, track.album.images);
                        tmp.add(song);
                    }
                    callBack.onSuccess(tmp);
                }

                @Override
                public void failure(RetrofitError error) {
                    callBack.onFailure(error.toString());
                }
            });
        } catch (RetrofitError e){
            callBack.onFailure(e.getResponse().toString());
        }
    }


}
