package com.miloshzelembaba.play.Network;

import com.miloshzelembaba.play.Network.NetworkEventTypeCallbacks.OnHostSwitchEvent;
import com.miloshzelembaba.play.Network.NetworkEventTypeCallbacks.OnPartyUpdated;

import java.util.ArrayList;

/**
 * Created by miloshzelembaba on 2018-03-18.
 */

public class NetworkManager {
    private static NetworkManager instance;
    private static String address;
    private static int port;
    private static ArrayList<OnPartyUpdated> onPartyUpdatedListeners;
    private static OnHostSwitchEvent onHostSwitchEventListener;

    private NetworkManager(){
        onPartyUpdatedListeners = new ArrayList<>();
    }

    // should be a singleton
    public static NetworkManager getInstance(){
        if (instance == null){
            instance = new NetworkManager();
        }

        return instance;
    }

    public void addPartyUpdateListener(OnPartyUpdated listener){
        onPartyUpdatedListeners.add(listener);
    }

    public void removePartyUpdateListener(OnPartyUpdated listener){
        onPartyUpdatedListeners.remove(listener);
    }

    public ArrayList<OnPartyUpdated> getOnPartyUpdatedListeners(){
        return onPartyUpdatedListeners;
    }

    public void setHostSwitchListener(OnHostSwitchEvent listener) {
        onHostSwitchEventListener = listener;
    }

    public OnHostSwitchEvent getOnHostSwitchEventListener() {
        return onHostSwitchEventListener;
    }
}
