package com.miloshzelembaba.play.api.Services;

import com.miloshzelembaba.play.Models.Party;
import com.miloshzelembaba.play.Models.Song;
import com.miloshzelembaba.play.api.APIRequest;
import com.miloshzelembaba.play.api.Request;

import org.json.JSONException;
import org.json.JSONObject;

public class RemoveSongFromPartyService {
    private APIRequest apiService;

    public interface RemoveSongFromPartyServiceCallback{
        void onSuccess(Party party);
        void onFailure(String errorMessage);
    }

    public void requestService(Song song, final RemoveSongFromPartyService.RemoveSongFromPartyServiceCallback callback) {
        apiService = new APIRequest();
        Request request = new Request();
        request.setUrl("removeSongFromParty/");
        request.addParameter("song", song);

        apiService.sendRequest(request,
                new APIRequest.APIRequestCallBack() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        if (callback != null) {
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
}
