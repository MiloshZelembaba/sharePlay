package com.miloshzelembaba.play.Network;

import android.content.Context;

import com.miloshzelembaba.play.Error.ErrorService;
import com.miloshzelembaba.play.Models.Party;
import com.miloshzelembaba.play.Network.NetworkEventTypeCallbacks.OnPartyUpdated;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NetworkInfoMessenger implements Runnable {
    private final JSONObject recievedJSON;
    private final Context context;
    private final String UPDATE_PARTY = "update_party";
    private final String HOST_SWITCH = "host_switch";

    public NetworkInfoMessenger(JSONObject obj, Context context){
        this.context = context;
        recievedJSON = obj;
    }

    @Override
    public void run() {
        try {
            handelResult();
        } catch (JSONException e){
            ErrorService.showErrorMessage(context, e.getMessage(), ErrorService.ErrorSeverity.HIGH);
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
