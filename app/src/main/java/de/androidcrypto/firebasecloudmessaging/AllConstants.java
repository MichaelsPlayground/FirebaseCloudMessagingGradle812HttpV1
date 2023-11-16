package de.androidcrypto.firebasecloudmessaging;

public interface AllConstants {

    String CHANNEL_ID = "1000";

    String NOTIFICATION_URL = "https://fcm.googleapis.com/v1/projects/fir-cloudmessaging-b020c/messages:send" ;

    String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    String[] SCOPES = { MESSAGING_SCOPE };

}
