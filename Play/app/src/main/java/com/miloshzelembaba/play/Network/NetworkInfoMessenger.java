package com.miloshzelembaba.play.Network;

import com.miloshzelembaba.play.Models.Party;
import com.miloshzelembaba.play.Network.NetworkEventTypeCallbacks.OnPartyUpdated;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by miloshzelembaba on 2018-03-18.
 */

public class NetworkInfoMessenger implements Runnable {
    JSONObject recievedJSON;
    private final String UPDATE_PARTY = "update_party";
    private final String HOST_SWITCH = "host_switch";

    public NetworkInfoMessenger(JSONObject obj){
        recievedJSON = obj;
    }

    @Override
    public void run() {
        try {
            handelResult();
        } catch (JSONException e){
            // TODO: create a popup thingy
        }
    }

    private void handelResult() throws JSONException {
        if (recievedJSON.getString("type").equals(UPDATE_PARTY)){
            handlePartyReceieved();
        } else if (recievedJSON.getString("type").equals(HOST_SWITCH)) {
            handleHostSwitch(recievedJSON.getString("party_id"));
        }
    }

    private void handleHostSwitch(String partyId) throws JSONException {
        NetworkManager.getInstance().getOnHostSwitchEventListener().onHostSwitchEvent(partyId);
    }

    private void handlePartyReceieved() throws JSONException {
        Party party = new Party(recievedJSON.getJSONObject("party"));

        ArrayList<OnPartyUpdated> listeners = NetworkManager.getInstance().getOnPartyUpdatedListeners();

        for (OnPartyUpdated listener: listeners){
            listener.onPartyUpdated(party);
        }
    }
}
