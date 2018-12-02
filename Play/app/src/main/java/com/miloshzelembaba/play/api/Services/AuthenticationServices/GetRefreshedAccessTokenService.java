package com.miloshzelembaba.play.api.Services.AuthenticationServices;

import com.miloshzelembaba.play.Models.User;
import com.miloshzelembaba.play.api.APIRequest;
import com.miloshzelembaba.play.api.Request;

import org.json.JSONObject;


public class GetRefreshedAccessTokenService {
    // todo: need to change the server address for these api requests
    private APIRequest apiService;

    public interface GetRefreshedAccessTokenServiceCallback{
        void onSuccess(JSONObject response);
        void onFailure(String errorMessage);
    }

    public void requestService(User user, final GetRefreshedAccessTokenServiceCallback callback){
        apiService = new APIRequest();
        Request request = new Request();
        request.setUrl("refreshAccessToken/");
        request.addParameter("email", user.getEmail());

        apiService.sendRequest(request,
                new APIRequest.APIRequestCallBack() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        callback.onFailure(errorMessage);
                    }
                }
        );
    }
}
