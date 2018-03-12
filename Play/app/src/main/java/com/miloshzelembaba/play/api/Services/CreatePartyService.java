package com.miloshzelembaba.play.api.Services;

import com.miloshzelembaba.play.Models.User;
import com.miloshzelembaba.play.api.APIRequest;
import com.miloshzelembaba.play.api.Request;

import org.json.JSONObject;

/**
 * Created by miloshzelembaba on 2018-03-12.
 */

public class CreatePartyService {
    private APIRequest apiService;

    public interface CreatePartyServiceCallback{
        void onSuccess(String herrrorooooooo);
        void onFailure(String errorMessage);
    }


    public void requestService(User user, String partyName, final CreatePartyService.CreatePartyServiceCallback callback){
        apiService = new APIRequest();
        Request request = new Request();
        request.setUrl("http://10.0.3.2:8000/createParty/");
        request.addParameter("host", user);
        request.addParameter("party_name", partyName);

        apiService.sendRequest(request,
                new APIRequest.APIRequestCallBack() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        // TODO: should this be spun off of a new thread?
                        System.out.println("no wayt his worked");
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
