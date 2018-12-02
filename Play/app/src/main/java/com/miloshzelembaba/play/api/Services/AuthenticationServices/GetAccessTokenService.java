package com.miloshzelembaba.play.api.Services.AuthenticationServices;

import com.google.firebase.iid.FirebaseInstanceId;
import com.miloshzelembaba.play.api.APIRequest;
import com.miloshzelembaba.play.api.Request;

import org.json.JSONObject;


public class GetAccessTokenService {
    // todo: need to change the server address for these api requests
    private APIRequest apiService;

    public interface GetAccessTokenServiceCallback{
        void onSuccess(JSONObject response);
        void onFailure(String errorMessage);
    }

    public void requestService(String authCode, final GetAccessTokenServiceCallback callback){
        apiService = new APIRequest();
        Request request = new Request();
        request.setUrl("getAccessToken/");
        request.addParameter("auth_code", authCode);
        request.addParameter("firebase_refresh_token", FirebaseInstanceId.getInstance().getToken());

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
