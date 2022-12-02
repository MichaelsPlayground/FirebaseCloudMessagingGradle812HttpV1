# Firebase Cloud Messaging

This app can send 1:1 messages ("notifications") from one (Android) device to another device. Both 
apps need the app installed to get it working.

To run this app you need to setup your own Firebase project on Firebase console.



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


