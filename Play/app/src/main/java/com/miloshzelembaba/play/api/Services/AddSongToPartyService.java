package com.miloshzelembaba.play.api.Services;

import com.miloshzelembaba.play.Models.Party;
import com.miloshzelembaba.play.Models.Song;
import com.miloshzelembaba.play.Models.User;
import com.miloshzelembaba.play.api.APIRequest;
import com.miloshzelembaba.play.api.Request;

import org.json.JSONObject;

/**
 * Created by miloshzelembaba on 2018-03-15.
 */

public class AddSongToPartyService {
    private APIRequest apiService;

    public interface AddSongToPartyServiceCallback{
        void onSuccess(Party party);
        void onFailure(String errorMessage);
    }


    public void requestService(User user, Party party, Song song, final AddSongToPartyService.AddSongToPartyServiceCallback callback){
        apiService = new APIRequest();
        Request request = new Request();
        request.setUrl("addSongToParty/");
        request.addParameter("user", user);
        request.addParameter("party", party);
        request.addParameter("song", song);


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
