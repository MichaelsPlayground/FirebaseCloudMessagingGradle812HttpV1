# Firebase Cloud Messaging

This app can send 1:1 messages ("notifications") from one (Android) device to another device. Both 
apps need the app installed to get it working.

To run this app you need to setup your own Firebase project on Firebase console.

Project is working !

Updated to SDK 33 and Gradle 8.12. 

Note: do not update in **settings.gradle** the entry

classpath 'com.google.gms:google-services:4.3.14'

to 4.4.x - this won't work

Second note: do not update in **build.gradle (app)** the entry:

implementation 'com.google.android.material:material:1.8.0'

to 1.10.x - this won't work ether, use 1.9.0.

Firebase Cloud Messaging migration guide: https://firebase.google.com/docs/cloud-messaging/migrate-v1

additional project for new HttpV1 api: https://github.com/basilmt/FCM-Snippet


AndroidManifest.xml
```plaintext
        <service
            android:name=".FirebaseMessagingService"
            android:exported="false">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
            <meta-data
                android:name="com.google.firebase.messaging.default_notification_channel_id"
                android:value="@string/default_notification_channel_id" />
        </service>

```

old:
```plaintext
        <service
            android:name=".FirebaseMessagingService"
            android:permission="TODO"
            android:exported="true">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
        </service>
```

```plaintext
Things to get started
goto this url: https://console.firebase.google.com/u/0/project/_/settings/serviceaccounts/adminsdk
Select firebase admin sdk and then click on generate new private key. Rename the downloaded file into service_account.json
Copy that file into res/raw directory
In MainActivity.java
Find function getTokenForNotification.
Replace the token value with the token of the person whom you want to sent the notification.

Important : In module level build.gradle file
android{
    packagingOptions {
        exclude 'META-INF/DEPENDENCIES'
    }
    .
    .
    .
 
    implementation 'com.android.volley:volley:1.2.0'
    implementation 'com.google.auth:google-auth-library-oauth2-http:0.25.3'
}
```

```plaintext
FileInputStream serviceAccount =
new FileInputStream("path/to/serviceAccountKey.json");

FirebaseOptions options = new FirebaseOptions.Builder()
  .setCredentials(GoogleCredentials.fromStream(serviceAccount))
  .setDatabaseUrl("https://fir-cloudmessaging-b020c-default-rtdb.europe-west1.firebasedatabase.app")
  .build();

FirebaseApp.initializeApp(options);
```

```plaintext
Android client for Cloud Messaging, get it here:
You can get your Client ID and secret from: https://console.cloud.google.com/apis/credentials

Configure your Consent screen and then create an OAuth Client ID of type Desktop app.

1063102112224-s8cu1uji2epdi4kpcoctd90kh9gmi118.apps.googleusercontent.com

```

https://console.cloud.google.com/apis/api/googlecloudmessaging.googleapis.com/quotas?project=fir-cloudmessaging-b020c




