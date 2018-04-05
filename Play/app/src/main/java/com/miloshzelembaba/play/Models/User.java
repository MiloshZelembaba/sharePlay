package com.miloshzelembaba.play.Models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by miloshzelembaba on 2018-03-11.
 */

public class User extends Serializable {
    String mId;
    String mFirstName;
    String mLastName;
    String mEmail;

    public User(String id, String fn, String ln, String email){
        mId = id;
        mFirstName = fn;
        mLastName = ln;
        mEmail = email;
    }

    public User(JSONObject object) throws JSONException {
        if (object.has("id")) {
            mId = object.getString("id");
        } else {
            throw new JSONException("invalid json object");
        }

        if (object.has("first_name")) {
            mFirstName = object.getString("first_name");
        } else {
            throw new JSONException("invalid json object");
        }

        if (object.has("last_name")) {
            mLastName = object.getString("last_name");
        } else {
            throw new JSONException("invalid json object");
        }

        if (object.has("email")) {
            mEmail = object.getString("email");
        } else {
            throw new JSONException("invalid json object");
        }
    }

    @Override
    public JSONObject serialize() throws JSONException{
        JSONObject object = new JSONObject();

        object.put("id", mId);
        object.put("first_name", mFirstName);
        object.put("last_name", mLastName);
        object.put("email", mEmail);

        return object;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getId(){
        return mId;
    }
}
