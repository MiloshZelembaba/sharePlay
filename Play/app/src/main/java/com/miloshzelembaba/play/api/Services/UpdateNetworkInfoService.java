package com.miloshzelembaba.play.api.Services;

import com.miloshzelembaba.play.Models.User;
import com.miloshzelembaba.play.Network.NetworkManager;
import com.miloshzelembaba.play.api.APIRequest;
import com.miloshzelembaba.play.api.Request;

import org.json.JSONObject;

/**
 * Created by miloshzelembaba on 2018-03-18.
 */

public class UpdateNetworkInfoService {
    private APIRequest apiService;

    public void requestService(User user){
        apiService = new APIRequest();
        Request request = new Request();
        request.setUrl("updateNetworkInfo/");
        request.addParameter("user", user);
        request.addParameter("address", NetworkManager.getInstance().getAddress());
        request.addParameter("port", NetworkManager.getInstance().getPort());

        apiService.sendRequest(request,
                new APIRequest.APIRequestCallBack() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        // unused
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        // unused
                    }
                }
        );
    }
}
