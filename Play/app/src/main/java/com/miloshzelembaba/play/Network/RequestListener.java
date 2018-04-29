package com.miloshzelembaba.play.Network;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.miloshzelembaba.play.Models.User;
import com.miloshzelembaba.play.api.Services.UpdateNetworkInfoService;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by miloshzelembaba on 2018-03-18.
 */


// TODO: i think that this should be implemented as a handler
public class RequestListener implements Runnable {
    User user;
    Activity baseActivity;

    public RequestListener(User user, Activity a) {
        this.user = user;
        baseActivity = a;
    }

    @Override
    public void run() {
        try {
            ServerSocket tcpSocket = establishTCPConnection(); // the socket clients will communicate

            UpdateNetworkInfoService updateNetworkInfoService = new UpdateNetworkInfoService();
            updateNetworkInfoService.requestService(user); // updates the NetworkManager server side

            while (true) {
                // accept any incoming connections
                Socket clientSocket = tcpSocket.accept();

                // read in the connection details (upload/download/terminate)
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String clientString = inFromClient.readLine();
                try {
                    JSONObject recievedJSON = new JSONObject(clientString);
                    NetworkInfoMessenger messenger = new NetworkInfoMessenger(recievedJSON);
                    // spin off new thread to notify the ui thread
                    Thread thread = new Thread(messenger);
                    thread.start();

                } catch (final Exception e) {
                    baseActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                Toast t = Toast.makeText(baseActivity, e.getMessage(), Toast.LENGTH_LONG);
                                t.show();
                            } catch (Exception e){
                                System.out.println("uhoh");
                            }
                        }
                    });
                    Log.e("Request Listener:", "idk what the error is");
                }
            }
        } catch (Exception e){
            System.out.println("wtf");
            // TODO: popup thing
        }
    }

    // TODO: should i have an onkill method?


    private String getIpAddress(ServerSocket tcpSocket){
        return "";
//        if (isEmulator()){
//            return "10.0.3.2";
//        } else {
////            return "10.0.0.253"; // for physical phone
//            return "10.0.0.99";
//        }
//

//        try {
//            return tcpSocket.getInetAddress().getLocalHost().toString().replace("localhost/", "");
//        } catch (Exception e){
//            System.out.println("duh herro");
//        }
//
//        return "";
    }

    private boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    // establishes a TCP connection
    private ServerSocket establishTCPConnection() {
        ServerSocket tcpSocket;

        tcpSocket = createTCPSocket(); // open tcp connection
        NetworkManager.getInstance().setPort(tcpSocket.getLocalPort()); // save the port

        try {
            NetworkManager.getInstance().setAddress(getIpAddress(tcpSocket));
        } catch (Exception e) {}

        return tcpSocket;
    }

    // creates a TCP socket
    private ServerSocket createTCPSocket(){
        int n_port;
        ServerSocket socket;

        while (true){
            n_port = 0;
            socket = tryTCPOnPort(n_port);

            if (socket != null){
                break;
            }
        }

        return socket;
    }

    // trys opening on a port specified by 'port'
    private ServerSocket tryTCPOnPort(int port){
        ServerSocket socket;
        try {
            // TODO: only just localhost
            socket = new ServerSocket(0);
            return socket;
        } catch (IOException e) {
            System.out.println(e);
        }

        return null;
    }
}
