package com.miloshzelembaba.play.api;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class APIRequest extends AsyncTask<Request, Void, JSONObject> {
    JSONObject result;
    APIRequestCallBack callBack;

    public interface APIRequestCallBack{
        void onSuccess(JSONObject result);
        void onFailure(String errorMessage);
    }

    @Override
    public void onPreExecute(){}

    @Override
    public JSONObject doInBackground(Request... requests){
        if (requests.length != 1){
            callBack.onFailure("Requests has length " + requests.length + " for some reason");
            return null;
        }

//        NetworkController networkController = NetworkController.getInstance();
//        String requestId = Long.toString((Long)requests[0].getParameter("request_id"));

        try {
            result = getResponse(requests[0]);
//            if (networkController.numRequests() == 1 && networkController.has(requestId)) {
//                networkController.remove(requestId);
                return result;
//            }
//            networkController.remove(requestId);
//            return null;
        } catch (Exception e) {
//            networkController.remove(requestId);
            callBack.onFailure(e.getMessage());
            return null;
        }
    }

    @Override
    public void onPostExecute(JSONObject result){
        if (result != null) {

            callBack.onSuccess(result);
        } else {
            // onFailure callback is taken care of in doInBackground()
        }
    }

    public void sendRequest(Request request, APIRequestCallBack callBack){
        this.callBack = callBack;
        execute(request);
    }

    private JSONObject getResponse(Request request) throws Exception{
        String urlRequest = request.buildURL();
        byte[] postDataBytes = request.getParamBytes();


        StringBuilder result = new StringBuilder();
        URL url = new URL(urlRequest);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);



        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return new JSONObject(result.toString()); // TODO: this won't work
    }

}
