package com.miloshzelembaba.play.api.Services;

import com.miloshzelembaba.play.Models.User;
import com.miloshzelembaba.play.api.APIRequest;
import com.miloshzelembaba.play.api.Request;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by miloshzelembaba on 2018-03-11.
 */

public class JoinPartyService {
    private APIRequest apiService;

    public interface JoinPartServiceCallback{
        void onSuccess(String partyId);
        void onFailure(String errorMessage);
    }


    public void requestService(String partyCode, User user, final JoinPartyService.JoinPartServiceCallback callback){
        apiService = new APIRequest();
        Request request = new Request();
        request.setUrl("joinParty/");
        request.addParameter("code", partyCode);
        request.addParameter("user", user);

        apiService.sendRequest(request,
                new APIRequest.APIRequestCallBack() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        // TODO: should this be spun off of a new thread?
                        try {
                            String partyId = result.getString("party_id");
                            callback.onSuccess(partyId);
                        } catch (JSONException e) {
                            onFailure(e.getMessage());
                        }

                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        // TODO: create a generic error popup
                        callback.onFailure(errorMessage);
                    }
                }
        );
    }
}
