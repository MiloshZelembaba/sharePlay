package com.miloshzelembaba.play.api.Services;

import com.miloshzelembaba.play.Models.Party;
import com.miloshzelembaba.play.api.APIRequest;
import com.miloshzelembaba.play.api.Request;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by miloshzelembaba on 2018-03-12.
 */

public class GetPartyDetailsService {
    private APIRequest apiService;

    public interface GetPartyDetailsServiceCallback{
        void onSuccess(Party party);
        void onFailure(String errorMessage);
    }


    public void requestService(String partyId, final GetPartyDetailsService.GetPartyDetailsServiceCallback callback){
        apiService = new APIRequest();
        Request request = new Request();
        request.setUrl("getPartyDetails/");
        request.addParameter("party_id", partyId);

        apiService.sendRequest(request,
                new APIRequest.APIRequestCallBack() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        // TODO: should this be spun off of a new thread?
                        try {
                            Party party = new Party(result);

                            callback.onSuccess(party);
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
