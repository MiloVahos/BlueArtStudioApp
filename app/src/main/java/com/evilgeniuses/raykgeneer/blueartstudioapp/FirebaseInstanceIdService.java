package com.evilgeniuses.raykgeneer.blueartstudioapp;

import com.google.firebase.iid.FirebaseInstanceId;

public class FirebaseInstanceIdService extends com.google.firebase.iid.FirebaseInstanceIdService {

    private static final String  REG_TOKEN = "REG_TOKEN";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();;
    }

}
