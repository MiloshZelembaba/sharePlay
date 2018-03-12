package com.miloshzelembaba.play.api.Services;

import com.miloshzelembaba.play.api.APIRequest;
import com.miloshzelembaba.play.api.Request;

import org.json.JSONObject;

/**
 * Created by miloshzelembaba on 2018-03-11.
 */

public class JoinPartyService {
    private APIRequest apiService;

    public interface JoinPartServiceCallback{
        void onSuccess(String user);
        void onFailure(String errorMessage);
    }


    public void requestService(String partyCode, final JoinPartyService.JoinPartServiceCallback callback){
        apiService = new APIRequest();
        Request request = new Request();
        request.setUrl("http://10.0.3.2:8000/joinParty/");
        request.addParameter("party_code", partyCode);

        apiService.sendRequest(request,
                new APIRequest.APIRequestCallBack() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        // TODO: should this be spun off of a new thread?


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
