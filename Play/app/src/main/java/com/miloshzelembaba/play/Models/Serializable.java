package com.miloshzelembaba.play.Models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by miloshzelembaba on 2018-03-11.
 */

public abstract class Serializable {

    public abstract JSONObject serialize() throws JSONException;
}
