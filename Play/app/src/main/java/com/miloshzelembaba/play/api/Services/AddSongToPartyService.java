package com.miloshzelembaba.play.api.Services;

import com.miloshzelembaba.play.Models.Party;
import com.miloshzelembaba.play.Models.Song;
import com.miloshzelembaba.play.Models.User;
import com.miloshzelembaba.play.api.APIRequest;
import com.miloshzelembaba.play.api.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by miloshzelembaba on 2018-03-15.
 */

public class AddSongToPartyService {
    private APIRequest apiService;

    public interface AddSongToPartyServiceCallback{
        void onSuccess(Party party);
        void onFailure(String errorMessage);
    }


    public void requestService(User user, Party party, ArrayList<Song> songs, final AddSongToPartyService.AddSongToPartyServiceCallback callback){
        apiService = new APIRequest();
        Request request = new Request();
        request.setUrl("addSongToParty/");
        request.addParameter("user", user);
        request.addParameter("party", party);

        /* we serialize the songs before hand*/ //TODO: need to abstract this out to Request class
        JSONArray array = new JSONArray();
        try {
            for (Song song: songs) {
                try {
                    array.put(song.serialize());
                } catch (Exception e) {}
            }
        } catch (Exception e) {}
        request.addParameter("songs", array.toString());


        apiService.sendRequest(request,
                new APIRequest.APIRequestCallBack() {
                    @Override
                    public void onSuccess(JSONObject result) {}

                    @Override
                    public void onFailure(String errorMessage) {
                        // TODO: create a generic error popup
                        if (callback != null) {
                            callback.onFailure(errorMessage);
                        }
                    }
                }
        );
    }
}
