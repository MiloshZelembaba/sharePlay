package com.miloshzelembaba.play.api.Services;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.miloshzelembaba.play.Error.ErrorService;
import com.miloshzelembaba.play.Network.NetworkInfoMessenger;

import org.json.JSONObject;

public class FCMService extends FirebaseMessagingService {
    public FCMService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        final String clientString = remoteMessage.getData().get("body");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject recievedJSON = new JSONObject(clientString);
                    NetworkInfoMessenger messenger = new NetworkInfoMessenger(recievedJSON, getBaseContext());
                    // spin off new thread to notify the ui thread
                    Thread thread = new Thread(messenger);
                    thread.start();

                } catch (final Exception e) {
                    ErrorService.showErrorMessage(getBaseContext(), "FCM error: " + e.getMessage(), ErrorService.ErrorSeverity.HIGH);
                }
            }
        };

        Thread newThread = new Thread(runnable);
        newThread.start();
    }

}
