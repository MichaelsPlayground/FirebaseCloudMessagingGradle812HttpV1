package de.androidcrypto.firebasecloudmessaging;


import static com.google.android.gms.common.util.CollectionUtils.mapOf;

import android.app.Activity;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FcmNotificationsSender {

    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private final String fcmServerKey = "AAAA94XS4eA:APA91bFHSFNsjdEOlFiIKcko2Y_vDRohRG_zTKUx1vVE-zChNi1roi7DDODLF_Cobi1jkEstGn5EfV45t6Jvn3Dh1mjRN71h9fpC-BlePVKrrKSEflCFmF1FgJDWUb4S3fTbxOudqZEm";
    String userFcmToken;
    String title;
    String body;
    Context mContext;
    Activity mActivity;
    private RequestQueue requestQueue;

    public FcmNotificationsSender(String userFcmToken, String title, String body, Context mContext, Activity mActivity) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    public void SendNotificationsV2() {

        // Construct the FCM message payload
        // https://blog.stackademic.com/integrating-firebase-cloud-messaging-for-real-time-push-notifications-in-android-5b550d2a8f6

        Map payload = null;
        payload = mapOf("sender", "john", "message", "myMessage", "dat", "myDat");

        FirebaseMessaging.getInstance().send(
                new RemoteMessage.Builder(userFcmToken)
                        .setMessageType("chat")
                        .setData(payload)
                        .build()
        );

    }

    public void SendNotifications() {

        requestQueue = Volley.newRequestQueue(mActivity);
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("to", userFcmToken);
            JSONObject notificationObject = new JSONObject();
            notificationObject.put("title", title);
            notificationObject.put("body", body);
            notificationObject.put("icon", "icon_for_splash");
            notificationObject.put("sound", "little_bell_14606.mp3");
            notificationObject.put("android_channel_id",R.string.default_notification_channel_id);
            mainObject.put("notification", notificationObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    // code run is got response

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // code run is got error

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=" + fcmServerKey);
                    return header;
                }
            };
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}