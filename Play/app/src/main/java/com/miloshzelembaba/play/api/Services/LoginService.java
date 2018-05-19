package com.miloshzelembaba.play.api.Services;

import com.miloshzelembaba.play.Models.User;
import com.miloshzelembaba.play.api.APIRequest;
import com.miloshzelembaba.play.api.Request;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by miloshzelembaba on 2018-03-07.
 */

public class LoginService {
    public static final String SPOTIFY_PREMIUM = "spotify_premium";
    public static final String SPOTIFY_FREE = "spotify_free";
    public static final String NONE = "none";
    public static final String APPLE_MUSIC = "apple_music";
    private APIRequest apiService;

    public interface LoginServiceCallback{
        void onSuccess(User user);
        void onFailure(String errorMessage);
    }


    public void requestService(String email, String displayName, String product, final LoginServiceCallback callback){
        apiService = new APIRequest();
        Request request = new Request();
        request.setUrl("login/");
        request.addParameter("email", email);
        request.addParameter("display_name", displayName);

        if (product.equals("premium")) {
            request.addParameter("product", SPOTIFY_PREMIUM);
        } else if (product.equals("open")) {
            request.addParameter("product", SPOTIFY_FREE);
        } else {
            request.addParameter("product", NONE);
        }

        apiService.sendRequest(request,
                new APIRequest.APIRequestCallBack() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        // TODO: should this be spun off of a new thread?

                        if (!result.has("first_name")){
                            callback.onSuccess(null);
                        } else {
                            try {
                                User user = new User(result);
                                callback.onSuccess(user);
                            } catch (JSONException e) {
                                callback.onFailure(e.getMessage());
                            }
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
