package com.miloshzelembaba.play.Network;

import com.miloshzelembaba.play.Network.NetworkEventTypeCallbacks.OnPartyUpdated;

import java.util.ArrayList;

/**
 * Created by miloshzelembaba on 2018-03-18.
 */

public class NetworkInfo {
    private static NetworkInfo instance;
    private static String address;
    private static int port;
    private static ArrayList<OnPartyUpdated> onPartyUpdatedListeners;

    private NetworkInfo(){
        onPartyUpdatedListeners = new ArrayList<>();
    }

    // should be a singleton
    public static NetworkInfo getInstance(){
        if (instance == null){
            instance = new NetworkInfo();
        }

        return instance;
    }

    public void addPartyUpdateListener(OnPartyUpdated listener){
        onPartyUpdatedListeners.add(listener);
    }

    public ArrayList<OnPartyUpdated> getOnPartyUpdatedListeners(){
        return onPartyUpdatedListeners;
    }

    public void setAddress(String ad){
        address = ad;
    }

    public void setPort(int p){
        port = p;
    }

    public String getAddress(){
        return address;
    }

    public int getPort(){
        return port;
    }
}
