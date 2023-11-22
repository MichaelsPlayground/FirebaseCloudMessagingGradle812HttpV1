package de.androidcrypto.firebasecloudmessaging;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import de.androidcrypto.firebasecloudmessaging.models.UserModel;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "FirebaseCloudMessaging";
    com.google.android.material.textfield.TextInputEditText signedInUser;
    com.google.android.material.textfield.TextInputEditText etReceipient, etMessage, etMessageTitle;
    com.google.android.material.textfield.TextInputLayout etMessageLayout, etMessageTitleLayout;

    Button signIn, signOut, listUser;

    private static String notificationUid = "", notificationEmail, notificationName, notificationToken;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    public static final int REQUESTCODE_SIGN_IN = 1;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build());

    public static AtomicInteger msgId = new AtomicInteger(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signedInUser = findViewById(R.id.etMainSignedInUser);
        etReceipient = findViewById(R.id.etMainReceipient);
        etMessageTitle = findViewById(R.id.etMainMessageTitle);
        etMessageTitleLayout = findViewById(R.id.etMainMessageTitleLayout);
        etMessage = findViewById(R.id.etMainMessage);
        etMessageLayout = findViewById(R.id.etMainMessageLayout);

        signIn = findViewById(R.id.btnMainSignIn);
        signOut = findViewById(R.id.btnMainSignOut);
        listUser = findViewById(R.id.btnMainListUser);

        // check that device is online
        if (!isOnline()) {
            Toast.makeText(this, "your app is NOT online and cannot send messages", Toast.LENGTH_LONG).show();
        } else {
            //Toast.makeText(this, "great, your app is online and can send messages", Toast.LENGTH_SHORT).show();
        }

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Realtime database
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //Toast.makeText(MainActivity.this, "User Signed In", Toast.LENGTH_SHORT).show();
                    signedInUser.setText(user.getEmail() + "\nDisplayName: " + user.getDisplayName());
                    activeButtonsWhileUserIsSignedIn(true);
                } else {
                    signedInUser.setText("no user is signed in");
                    activeButtonsWhileUserIsSignedIn(false);
                }
            }
        };

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent signInIntent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(true)
                        .setAvailableProviders(providers)
                        .setTheme(R.style.Theme_FirebaseCloudMessaging)
                        .build();
                signInLauncher.launch(signInIntent);

                /*
                // org
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(true)
                                .setAvailableProviders(providers)
                                .setTheme(R.style.Theme_FirebaseCloudMessaging)
                                .build(),
                        REQUESTCODE_SIGN_IN
                );*/
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "sign out the current user");
                mFirebaseAuth.signOut();
                signedInUser.setText(null);
            }
        });

        listUser = findViewById(R.id.btnMainListUser);
        listUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "list user on database");
                Intent intent = new Intent(MainActivity.this, ListUserActivity.class);
                intent.putExtra("ALL_USERS", true);
                startActivity(intent);
                //finish();
            }
        });

        etMessageLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showProgressBar();
                Log.i(TAG, "clickOnIconEnd");
                String messageTitleString = etMessageTitle.getText().toString();
                String messageString = etMessage.getText().toString();
                Log.i(TAG, "title: " + messageTitleString + " message: " + messageString);
                if (TextUtils.isEmpty(messageTitleString)) {
                    Toast.makeText(getApplicationContext(),
                            "message title is too short" + messageTitleString,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(messageString)) {
                    Toast.makeText(getApplicationContext(),
                            "message is too short" + messageTitleString,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(notificationToken)) {
                    Toast.makeText(getApplicationContext(),
                            "select a receipient first" + messageString,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                FcmNotificationsSenderHttpV1 fcmNotificationsSender = new FcmNotificationsSenderHttpV1(notificationToken, messageTitleString, messageString, MainActivity.this,MainActivity.this);

                // chat GPT
                fcmNotificationsSender.send(notificationToken);



                //fcmNotificationsSender.prepNotification(notificationToken);
                Log.i(TAG, "notification send with HttpV1 api");
                Toast.makeText(getApplicationContext(), "notification send with HttpV1 api", Toast.LENGTH_SHORT).show();
                // old
                //FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender(notificationToken, messageTitleString, messageString, MainActivity.this,MainActivity.this);
                //fcmNotificationsSender.SendNotifications();
                Log.i(TAG, "notification send");
                Toast.makeText(getApplicationContext(), "notification send", Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * incoming intent from ListUserActivity
         */
        Intent intent = getIntent();
        notificationUid = intent.getStringExtra("UID");
        notificationEmail = intent.getStringExtra("EMAIL");
        notificationName = intent.getStringExtra("DISPLAYNAME");
        System.out.println("*** notificationName: " + notificationName);
        notificationToken = intent.getStringExtra("TOKEN");
        if (!TextUtils.isEmpty(notificationUid)) {
            //Toast.makeText(this, "user selected for messaging: " + notificationUid, Toast.LENGTH_SHORT).show();
        }
        if (!TextUtils.isEmpty(notificationToken)) {
            Toast.makeText(this, "token selected for messaging: " + notificationToken, Toast.LENGTH_SHORT).show();
        }
        etReceipient.setText(notificationEmail + " (" + notificationName + ")");

        // create a notification channel
        createNotificationChannel(this);

        // for Android 13+
        askNotificationPermission();
    }

    // create the channel as early as possible as you cannot change the channel later
    public  void createNotificationChannel(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = context.getString(R.string.default_notification_channel_id);
            String channelName = context.getString(R.string.default_notification_channel_name);
            Uri customSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.little_bell_14606);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(customSoundUri, audioAttributes);
            //channel.setVibrationPattern(VIBRATE_PATTERN);
            channel.enableVibration(true);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * section sign in
     */

    private ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            (result) -> {
                IdpResponse response = result.getIdpResponse();
                handleSignInResponse(result.getResultCode(), response);
            });

    private void handleSignInResponse(int resultCode, @Nullable IdpResponse response) {
        // Successfully signed in
        if (resultCode == RESULT_OK) {
            //Toast.makeText(this, "user signed in", Toast.LENGTH_SHORT).show();

            // get Firebase Cloud Messaging token
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                return;
                            }

                            // Get new FCM registration token
                            String uToken = task.getResult();

                            FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
                            String uId = currentUser.getUid();
                            String uEmail = currentUser.getEmail();
                            String uName = "";
                            if (!TextUtils.isEmpty(currentUser.getDisplayName())) {
                                uName = currentUser.getDisplayName();
                            } else {
                                uName = getUsernameFromEmail(uEmail);
                            }
                            if (TextUtils.isEmpty(uToken)) {
                                uToken = "";
                            }
                            Log.i(TAG, "save user data from database for user id: " + currentUser.getUid());
                            writeNewUser(uId, uEmail, uName, uToken);
                            Toast.makeText(getApplicationContext(), "user data written to database", Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
                Toast.makeText(this, "sign in cancelled", Toast.LENGTH_LONG).show();
                //showSnackbar(R.string.sign_in_cancelled);
                return;
            }

            if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                Toast.makeText(this, "no internet connection", Toast.LENGTH_LONG).show();
                //showSnackbar(R.string.no_internet_connection);
                return;
            }

            /*
            if (response.getError().getErrorCode() == ErrorCodes.ANONYMOUS_UPGRADE_MERGE_CONFLICT) {
                Intent intent = new Intent(this, AnonymousUpgradeActivity.class).putExtra
                        (ExtraConstants.IDP_RESPONSE, response);
                startActivity(intent);
            }*/

            if (response.getError().getErrorCode() == ErrorCodes.ERROR_USER_DISABLED) {
                Toast.makeText(this, "account disabled", Toast.LENGTH_LONG).show();
                //showSnackbar(R.string.account_disabled);
                return;
            }
            Toast.makeText(this, "unknown error", Toast.LENGTH_LONG).show();
            //showSnackbar(R.string.unknown_error);
            Log.e(TAG, "Sign-in error: ", response.getError());
        }
    }

    /**
     * section SERVICE
     */

    public void writeNewUser(String userId, String email, String name, String token) {
        UserModel user = new UserModel(userId, name, email, token);
        mDatabaseReference.child("users").child(userId).setValue(user);
    }

    public String getUsernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    /**
     * section UI
     */

    private void activeButtonsWhileUserIsSignedIn(boolean isSignedIn) {
        listUser.setEnabled(isSignedIn);
        //databaseUserProfile.setEnabled(isSignedIn);
        //sendMessage.setEnabled(isSignedIn);
        //images.setEnabled(isSignedIn);
        //uploadImage.setEnabled(isSignedIn);
        //listImages.setEnabled(isSignedIn);

    }

    private void showSnackbar(String errorMessageRes) {
        //Snackbar.make(MainActivity.this, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

    /**
     * network section
     */

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);

    }

    /**
     * section for permission handling on Android 13+
     * see: https://github.com/firebase/quickstart-android/blob/master/messaging/app/src/main/java/com/google/firebase/quickstart/fcm/java/MainActivity.java
     * and https://firebase.google.com/docs/cloud-messaging/android/client#request-permission13
     */

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(this, "FCM can't post notifications without POST_NOTIFICATIONS permission",
                            Toast.LENGTH_LONG).show();
                }
            });

    private void askNotificationPermission() {
        // This is only necessary for API Level > 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
                Toast.makeText(this, "FCM can post notifications using POST_NOTIFICATIONS permission",
                        Toast.LENGTH_LONG).show();
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}