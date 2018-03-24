package com.miloshzelembaba.play.api.Services;

import com.miloshzelembaba.play.Models.User;
import com.miloshzelembaba.play.api.APIRequest;
import com.miloshzelembaba.play.api.Request;

import org.json.JSONObject;

/**
 * Created by miloshzelembaba on 2018-03-24.
 */

public class LeavePartyService {
    private APIRequest apiService;

    public interface LeavePartyServiceCallback{
        void onSuccess(String partyId);
        void onFailure(String errorMessage);
    }


    public void requestService(User user, final LeavePartyService.LeavePartyServiceCallback callback){
        apiService = new APIRequest();
        Request request = new Request();
        request.setUrl("leaveParty/");
        request.addParameter("user", user);

        apiService.sendRequest(request,
                new APIRequest.APIRequestCallBack() {
                    @Override
                    public void onSuccess(JSONObject result) {

                    }

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
