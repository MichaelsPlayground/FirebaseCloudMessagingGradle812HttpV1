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

to 1.10.x - this won't work ether.


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


