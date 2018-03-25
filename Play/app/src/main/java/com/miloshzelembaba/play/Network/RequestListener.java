package com.miloshzelembaba.play.Network;

import android.os.Build;

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

public class RequestListener implements Runnable {
    User user;

    public RequestListener(User user) {
        this.user = user;
    }

    @Override
    public void run() {
        try {
            ServerSocket tcpSocket = establishTCPConnection(); // the socket clients will communicate

            System.out.println("HERROOO ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println(NetworkInfo.getInstance().getAddress());
            System.out.println(NetworkInfo.getInstance().getPort());

            UpdateNetworkInfoService updateNetworkInfoService = new UpdateNetworkInfoService();
            updateNetworkInfoService.requestService(user); // updates the NetworkInfo server side

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

                } catch (Exception e) {
                    // TODO: custom popup shit here
                }
            }
        } catch (Exception e){
            // TODO: popup thing
        }
    }

    // TODO: should i have an onkill method?


    private String getIpAddress(ServerSocket tcpSocket){
        if (isEmulator()){
            return "10.0.3.2";
        } else {
            return "10.0.0.253"; // for physical phone
        }

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
        NetworkInfo.getInstance().setPort(tcpSocket.getLocalPort()); // save the port

        try {
            // TODO: VEERRYYY temporary, only does localhost
            NetworkInfo.getInstance().setAddress(getIpAddress(tcpSocket));
        } catch (Exception e) {
            // TODO: custom popup shit here
        }

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
            socket = new ServerSocket(port);
            return socket;
        } catch (IOException e) {
            System.out.println(e);
        }

        return null;
    }
}
