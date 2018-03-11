package com.miloshzelembaba.play.api.Services;

import com.miloshzelembaba.play.api.APIRequest;
import com.miloshzelembaba.play.api.Request;

import org.json.JSONObject;

/**
 * Created by miloshzelembaba on 2018-03-07.
 */

public class LoginService {
    private APIRequest apiService;

    public interface LoginServiceCallback{
        void onSuccess(String result);
        void onFailure(String errorMessage);
    }


    public void requestService(String email, String password, final LoginServiceCallback callback){
        apiService = new APIRequest();
        Request request = new Request();
        request.setUrl("http://10.0.3.2:8000/login/");
        request.addParameter("email", email);
        request.addParameter("password", password);

        apiService.sendRequest(request,
                new APIRequest.APIRequestCallBack() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        // maybe do something here

                        callback.onSuccess("yay");
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
