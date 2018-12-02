package com.miloshzelembaba.play.Network;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.miloshzelembaba.play.Utils.ApplicationUtil;
import com.miloshzelembaba.play.api.Services.SendFCMTokenService;

public class RequestListener extends FirebaseInstanceIdService {
    SendFCMTokenService sendFCMTokenService = new SendFCMTokenService();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (ApplicationUtil.getInstance().getUser() != null) {
            sendFCMTokenService.requestService(refreshedToken, null);
        }
    }
}
