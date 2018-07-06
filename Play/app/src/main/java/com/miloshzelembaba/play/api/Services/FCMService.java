package com.miloshzelembaba.play.api.Services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.miloshzelembaba.play.Network.NetworkController;
import com.miloshzelembaba.play.Network.NetworkInfoMessenger;

import org.json.JSONObject;

public class FCMService extends FirebaseMessagingService {
    public FCMService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
//        Log.d("AH", "From: " + remoteMessage.getFrom());
//        Log.d("AH", "Notification Message Body: " + remoteMessage.getNotification().getBody());

        if (NetworkController.getInstance().numRequests() != 0) {
            return;
        }

        final String clientString = remoteMessage.getData().get("body");

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject recievedJSON = new JSONObject(clientString);
                    NetworkInfoMessenger messenger = new NetworkInfoMessenger(recievedJSON);
                    // spin off new thread to notify the ui thread
                    Thread thread = new Thread(messenger);
                    thread.start();

                } catch (final Exception e) {
//                    baseActivity.runOnUiThread(new Runnable() {
//                        public void run() {
//                            try {
//                                Toast t = Toast.makeText(baseActivity, e.getMessage(), Toast.LENGTH_LONG);
//                                t.show();
//                            } catch (Exception e){
//                                System.out.println("uhoh");
//                            }
//                        }
//                    });
                    Log.e("FCMService", "idk what the error is");
                }
            }
        };

        Thread newThread = new Thread(runnable);
        newThread.start();
    }

}
