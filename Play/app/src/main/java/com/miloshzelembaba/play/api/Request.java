package com.miloshzelembaba.play.api;

import java.util.HashMap;

/**
 * Created by miloshzelembaba on 2018-03-07.
 */

public class Request {
    private HashMap<String, Object> params;
    private String url;

    public Request(){
        params = new HashMap<>();
    }

    public void setUrl(String url){
        // TODO: this should eventually only specify the endpoint, not the whole url. We should build the url ourselves here
        this.url = url;
    }

    public void addParameter(String key, Object value){
        params.put(key, value);
    }

    public String buildURL(){
        return url;
    }
}
