package com.miloshzelembaba.play.Models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by miloshzelembaba on 2018-03-12.
 */

public class Party extends Serializable {
    private String mId;
    private String mPartyName;
    private String mUniqueCode;
    private User mHost;

    public Party(JSONObject object) throws JSONException{
        if (object.has("id")) {
            mId = object.getString("id");
        } else {
            throw new JSONException("invalid json object");
        }

        if (object.has("name")) {
            mPartyName = object.getString("name");
        } else {
            throw new JSONException("invalid json object");
        }

        if (object.has("code")) {
            mUniqueCode = object.getString("code");
        } else {
            throw new JSONException("invalid json object");
        }

        if (object.has("host")) {
            mHost = new User(object.getJSONObject("host"));
        } else {
            throw new JSONException("invalid json object");
        }
    }

    public JSONObject serialize() throws JSONException{
        JSONObject object = new JSONObject();

        object.put("id", mId);
        object.put("party_name", mPartyName);
        object.put("unique_code", mUniqueCode);
        object.put("host", mHost.serialize().toString()); // TODO: hmm, would rather not do this

        return object;
    }

    public String getName(){
        return mPartyName;
    }

}
