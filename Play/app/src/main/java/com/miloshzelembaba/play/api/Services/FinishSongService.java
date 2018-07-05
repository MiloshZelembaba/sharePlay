package com.miloshzelembaba.play.api.Services;

import com.miloshzelembaba.play.Models.Party;
import com.miloshzelembaba.play.Models.Song;
import com.miloshzelembaba.play.api.APIRequest;
import com.miloshzelembaba.play.api.Request;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by miloshzelembaba on 2018-03-24.
 */

public class FinishSongService {
    private APIRequest apiService;

    public interface FinishSongServiceCallback {
        void onSuccess(Party party);
        void onFailure(String errorMessage);
    }

    public void requestService(String partyId, Song song, final FinishSongServiceCallback callback) {
        apiService = new APIRequest();
        Request request = new Request();
        request.setUrl("finishSong/");
        request.addParameter("party_id", partyId);
        request.addParameter("song", song);

        apiService.sendRequest(request,
                new APIRequest.APIRequestCallBack() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        if (callback != null) {
                            // TODO: should this be spun off of a new thread?
                            try {
                                Party party = new Party(result.getJSONObject("party"));

                                callback.onSuccess(party);
                            } catch (JSONException e) {
                                onFailure(e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        if (callback != null) {
                            callback.onFailure(errorMessage);
                        }
                    }
                }
        );
    }

    public void requestService(Song song, final FinishSongServiceCallback callback){
        requestService("", song, callback);
    }
}
