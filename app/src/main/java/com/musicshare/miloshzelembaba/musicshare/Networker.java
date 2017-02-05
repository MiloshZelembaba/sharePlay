package com.musicshare.miloshzelembaba.musicshare;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by jack on 2017-02-05.
 */

public class Networker {
    RequestQueue mRequestQueue;
    private Context mContext;
    // Make sure this is accurate
    private final String url = "http://10.20.243.240:8000";
    private static Networker instance = null;

    private Networker(Context cx) {
        this.mContext = cx;
        createQueue();
    }

    public boolean registerParty(String partyId){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("pid", partyId);
        postRequest("/party", params);
        return false;
    }

    // NOTE: PATH must start with a '/' i.e "/test"
    private void postRequest(String path, HashMap<String, String> params) {
        // Post params to be sent to the server
        JsonObjectRequest req = new JsonObjectRequest(url + path, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                }
        });
        mRequestQueue.add(req);
    }

    private void createQueue(){
        Cache cache = new DiskBasedCache(this.mContext.getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
    }

    public static Networker getInstance(Context cx){
        if (instance == null){
            instance = new Networker(cx);
            return instance;
        }
        return instance;
    }

}
