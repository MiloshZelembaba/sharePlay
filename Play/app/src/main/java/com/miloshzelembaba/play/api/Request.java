package com.miloshzelembaba.play.api;

import com.miloshzelembaba.play.Models.Serializable;
import com.miloshzelembaba.play.Network.NetworkController;
import com.miloshzelembaba.play.Utils.ApplicationUtil;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by miloshzelembaba on 2018-03-07.
 */

public class Request {
    private HashMap<String, Object> params;
    private String url;

    public Request(){
        url = "https://shareplay-204722.appspot.com/";
//        url = "http://192.168.0.19:8000/";
//        if (ApplicationUtil.getInstance().getAppMode().equals("dev_server")) {
//            url = "https://shareplay-204722.appspot.com/";
//        } else if (ApplicationUtil.getInstance().getAppMode().equals("local")) {
//            if (ApplicationUtil.getInstance().getLocalServerAddress().isEmpty()) {
//                findLocalServer();
//            }
//            url = ApplicationUtil.getInstance().getLocalServerAddress();
//        }
        params = new HashMap<>();
        addParameter("user", ApplicationUtil.getInstance().getUser());
        addParameter("request_id", System.currentTimeMillis());
        NetworkController.getInstance().put(Long.toString((Long)params.get("request_id")));
    }

    private void findLocalServer() {
        ApplicationUtil.getInstance().setLocalServerAddress("http://192.168.0.21:8000/");
        //            url = "http://192.168.0.21:8000/";
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

    public Object getParameter(String id) {
        return params.get(id);
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
