package com.miloshzelembaba.play.Network;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by miloshzelembaba on 2018-07-05.
 */

// TODO:  I believe that this needs to be thread safe
public class NetworkController {
    private static NetworkController instance;
    private Map<String, Integer> requests;

    private NetworkController(){
        requests = new HashMap<>();
    }

    public static NetworkController getInstance(){
        if (instance == null) {
            instance = new NetworkController();
        }

        return instance;
    }

    public void put(String id) {
        requests.put(id, 0);
    }

    public boolean has(String id) {
        return requests.containsKey(id);
    }

    public int numRequests() {
        return requests.size();
    }

    public void remove(String id) {
        requests.remove(id);
    }
}
