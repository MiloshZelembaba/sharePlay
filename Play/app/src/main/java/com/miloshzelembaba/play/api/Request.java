package com.miloshzelembaba.play.api;

import com.miloshzelembaba.play.Models.Serializable;
import com.miloshzelembaba.play.Utils.ApplicationUtil;
import com.miloshzelembaba.play.Utils.SharedPreferenceUtil;

import org.json.JSONObject;

import java.util.HashMap;

public class Request {
    private HashMap<String, Object> params;
    private String url;

    public Request(){
        if (SharedPreferenceUtil.getInstance(ApplicationUtil.getInstance().getContext()).getServerMode()) {
            url = "https://shareplay-204722.appspot.com/";
        } else {
            url = "http://10.0.0.15:8000/";
        }
        params = new HashMap<>();
        addParameter("user", ApplicationUtil.getInstance().getUser());
    }

    public void setUrl(String url){
        // TODO: this should eventually only specify the endpoint, not the whole url. We should build the url ourselves here
        this.url += url;
    }

    public void addParameter(String key, Object value){
        if (!params.containsKey(key)) {
            params.put(key, value);
        }
    }

    public String buildURL(){
        return url;
    }

    private JSONObject buildParamJSON(){
        JSONObject jsonObject = new JSONObject();

        for (String key : params.keySet()){
            try {
                // for custom models
                if (params.get(key) instanceof Serializable){
                    jsonObject.put(key, ((Serializable) params.get(key)).serialize());
                } else { // for primitive values
                    jsonObject.put(key, params.get(key));
                }
            } catch (Exception e){
                System.out.println(e);
            }
        }

        return jsonObject;
    }

    public byte[] getParamBytes(){
        JSONObject jsonObject = buildParamJSON();

        StringBuilder postData = new StringBuilder();
        postData.append(jsonObject.toString());

        try {
            return postData.toString().getBytes("UTF-8");
        } catch (Exception e){
            System.out.println(e);
            return null;
        }
    }
}
