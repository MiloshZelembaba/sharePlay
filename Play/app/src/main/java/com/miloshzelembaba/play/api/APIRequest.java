package com.miloshzelembaba.play.api;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by miloshzelembaba on 2018-02-28.
 */

public class APIRequest extends AsyncTask<Request, Void, String> {
    String result;
    APIRequestCallBack callBack;

    public interface APIRequestCallBack{
        void onSuccess(String result);
        void onFailure(String errorMessage);
    }

    @Override
    public void onPreExecute(){}

    @Override
    public String doInBackground(Request... requests){
        if (requests.length != 1){
            callBack.onFailure("Requests has length " + requests.length + " for some reason");
            return "";
        }

        try {
            result = getResponse(requests[0]);
            return result;
        } catch (Exception e) {
            callBack.onFailure(e.getMessage());
            return "";
        }
    }

    @Override
    public void onPostExecute(String result){
        callBack.onSuccess(result);

    }

    public void sendRequest(Request request, APIRequestCallBack callBack){
        this.callBack = callBack;
        execute(request);
    }

    private String getResponse(Request request) throws Exception{
        String urlRequest = request.buildURL();

        StringBuilder result = new StringBuilder();
        URL url = new URL(urlRequest);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }

}
