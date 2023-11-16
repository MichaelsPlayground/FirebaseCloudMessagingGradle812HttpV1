package de.androidcrypto.firebasecloudmessaging;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.auth.oauth2.GoogleCredentials;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FcmNotificationsSenderHttpV1 {

    // this is using the new Api HttpV1, see migration guide:
    // based on https://github.com/basilmt/FCM-Snippet

    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private final String fcmServerKey = "AAAA94XS4eA:APA91bFHSFNsjdEOlFiIKcko2Y_vDRohRG_zTKUx1vVE-zChNi1roi7DDODLF_Cobi1jkEstGn5EfV45t6Jvn3Dh1mjRN71h9fpC-BlePVKrrKSEflCFmF1FgJDWUb4S3fTbxOudqZEm";
    String userFcmToken;
    String title;
    String body;
    Context mContext;
    Activity mActivity;
    private RequestQueue requestQueue;

    public FcmNotificationsSenderHttpV1(String userFcmToken, String title, String body, Context mContext, Activity mActivity) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    // new
    public void prepNotification(String token) {
        JSONObject message = new JSONObject();
        JSONObject to = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data.put("title", "Notification");
            data.put("body", "you have 1 notification");

            to.put("token", token);
            to.put("data", data);

            message.put("message", to);
            if (token != null) {
                sentNotification(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sentNotification(JSONObject to) {
        System.out.println("*** sentNotification: " + to);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AllConstants.NOTIFICATION_URL,to, response -> {

        },error -> {
            error.printStackTrace();
        }){
            @Override
            public Map<String, String> getHeaders() {

                Map<String,String> map = new HashMap<>();
                try {
                    String tkn =  getAccessToken();
                    map.put("Authorization", "Bearer " + tkn);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                map.put("Content-Type", "application/json");

                return map;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

        requestQueue.addRequestEventListener(new RequestQueue.RequestEventListener() {
            @Override
            public void onRequestEvent(Request<?> request, int event) {
                System.out.println("*** requestQueue: " + request.toString());
            }
        });

    }

    private String getAccessToken() throws IOException {
        InputStream inputStream = mContext.getResources().openRawResource(R.raw.service_account);

        GoogleCredentials googleCredential = GoogleCredentials
                .fromStream(inputStream)
                .createScoped(Arrays.asList(AllConstants.SCOPES));
        googleCredential.refresh();

        Log.i("TAGggg", "getAccessToken: " + googleCredential.toString());
        return googleCredential.getAccessToken().getTokenValue();
    }

    // following: OLD



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

            System.out.println("*** starting request with mainObject: " + mainObject.toString());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    // code run is got response
                    System.out.println("*** request response: " + response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // code run is got error
                    System.out.println("*** request onErrorResponse: " + error.toString());

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=" + fcmServerKey);
                    System.out.println("*** getHeaders: " + header.toString());
                    return header;
                }
            };
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}