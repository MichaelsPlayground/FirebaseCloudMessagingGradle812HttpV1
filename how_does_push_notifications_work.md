# How does Firebase Cloud Messaging (Push Notifications) work ?

To send a **notification** from one Android device to another Android device we use a Firebase product 
called **Firebase Cloud Messaging**. It is a perfect way to send a small amount of data directly to 
the other smartphone with the great advantage, that the recipient gets informed about the incoming message 
without the necessity to open an app. 

This app will show what modules are involved to get this direct messaging system to run. It is based on an 
up to date Gradle (8.14) with Java 17 and is using the state of the art **HTTP V1 API**.

## What is the general workflow for "Push Notifications"

This is workflow in short:
1) User A ("sender") logs in to Firebase Authentication and stores its devices ID in a Firebase Realtime Database
2) User B ("recipient") logs in to Firebase Authentication and stores its devices ID in a Firebase Realtime Database
3) User A selects a user for sending a notification and retrieves the recipient's devices ID
4) User A sends a message to the Firebase Cloud Messaging backend
5) The Firebase backend server identifies the recipient by his device ID and sends the message to the device of user B
6) The device of user B gets the message and shows a notification in the Status Bar of the device.

## What modules do we need for "Push Notifications"

### 1 Google account

As a Push Notification is based on Firebase Cloud Messaging we do need a Google account to setup the background 
systems. A general hint: **please do not use your regular Google account** that is bundled with your Android smartphone 
but setup another Google account for Firebase related purposes.

### 2 Firebase setup

A a general rule regarding Firebase related apps: you should setup your basic app before you are running any Firebase setups. 
This is because a Firebase setup is bundled with your app's package name and development IDE, and if you will (later) change the 
package name the Firebase part of your app will stop working.

Login to your (Firebase) Google account and visit https://firebase.google.com. Click on "go to console" to enter the Firebase 
project overview. At this point I assume you are familiar with a Firebase setup to shorten this part. Click on the button with your project.

### 3 Firebase Authentication

Each time a user signs in (or signs up for the first time) the user is authenticated in **Firebase Authentication**. In my sample app 
I'm using the two provider "Email with password" and "Google account" for the authentication, this is done using **FirebaseAuthUi**. 
After sign-in the device is self-registering in **Firebase Cloud Messaging** and retrieves a **device ID** or **device token**. This is 
the "address" for sending a notification to... what are we doing with this information ? It is stored in a database - see next part.

### 4 Firebase Real Time Database

As the sending process should be as easy as possible the device token is stored in a data set called "users" in a **Firebase Real Time Database**. 
When thinking of a chat app you already have the database in place, so this information is just an additional entry in the user data record. 
The sender retrieves a list of all users from the database, selects one recipient and gets his (recipients) device token.

### 5 Preparing the notification

As the Firebase Cloud Messaging ("FCM") backend server expects a JSON structured file we need to prepare a JSON message with a **Notification Title**, 
a **Notification Body** and a **Notification recipients device token**.

### 6 Send the JSON file to the FCM Rest API

There are several ways to send (or "upload") a file to a server, but most important is the **authentication of the sender** that needs to accompany the 
JSON file. This is the most important difference to the legacy and (in 2024) deprecated API Cloud Messaging API. To run the new **HTTP V1 authentication** 
we need a new key in a "service_account.json" file. See the separate description on how to get the key file and where to place it in your app folder.

Do not confuse the "service_acount.json" file with the "google-services.json" file. The first one is necessary for 
Cloud Messaging, the second one is needed for all other Firebase stuff.

### 7 FCM backend server tasks

The incoming message is checked and the recipient's device is selected; FCM backend sends the message directly to the device.

### 8 Notification on Recipients device

The device is receiving the message and tries to find a **Notification Channel**; if there is a channel the message is shown in the Status Bar (eventually 
playing a sound, showing an icon or whatever you want).







