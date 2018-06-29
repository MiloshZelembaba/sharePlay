package com.miloshzelembaba.play.api.Services;

import com.miloshzelembaba.play.Spotify.SpotifyManager;
import com.miloshzelembaba.play.api.APIRequest;
import com.miloshzelembaba.play.api.Request;

import org.json.JSONObject;

/**
 * Created by miloshzelembaba on 2018-06-27.
 */

public class RefreshSpotifyAccessTokenService {
    private APIRequest apiService;

    public void requestService(){
        apiService = new APIRequest();
        Request request = new Request();
        request.setUrl("refreshSpotifyAccessToken/");

        apiService.sendRequest(request,
                new APIRequest.APIRequestCallBack() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        SpotifyManager.setAccessToken(result.optString("access_token"));
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        System.out.println("errrro");
                    }
                }
        );
    }
}
