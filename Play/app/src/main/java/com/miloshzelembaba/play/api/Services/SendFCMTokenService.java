package com.miloshzelembaba.play.api.Services;

import com.miloshzelembaba.play.api.APIRequest;
import com.miloshzelembaba.play.api.Request;

import org.json.JSONObject;

/**
 * Created by miloshzelembaba on 2018-05-24.
 */

public class SendFCMTokenService {
    private APIRequest apiService;

    public interface SendFCMTokenCallback{
        void onSuccess();
        void onFailure(String errorMessage);
    }


    public void requestService(String refreshToken, final SendFCMTokenService.SendFCMTokenCallback callback){
        apiService = new APIRequest();
        Request request = new Request();
        request.setUrl("updateFCMRefreshToken/");
        request.addParameter("refresh_token", refreshToken);


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
