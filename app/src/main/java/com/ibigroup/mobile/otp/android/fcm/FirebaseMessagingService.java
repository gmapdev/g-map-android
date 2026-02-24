/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibigroup.mobile.otp.android.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibigroup.mobile.otp.android.R;
import com.ibigroup.mobile.otp.android.ScreenSplashActivity;
import com.ibigroup.mobile.otp.android.api.Resource;
import com.ibigroup.mobile.otp.android.json.NotificationResponse;
import com.ibigroup.mobile.otp.android.utility.Configurations;
import com.ibigroup.mobile.otp.android.utility.Constants;
import com.ibigroup.mobile.otp.android.utility.Logger;
import com.ibigroup.mobile.otp.android.utility.ServerDelegate;
import com.ibigroup.mobile.otp.android.utility.Utilities;

import java.util.Map;


/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 * <p>
 * In order to make this Java sample functional, you must remove the following from the Kotlin messaging
 * service in the AndroidManifest.xml:
 * <p>
 * <intent-filter>
 * <action android:name="com.google.firebase.MESSAGING_EVENT" />
 * </intent-filter>
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private NotificationManager mNotificationManager;
    private static final String TAG = "FirebaseMsgService";


    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            //Logger.d(TAG, "Message data payload: " + remoteMessage.getData());

            generateNotification(remoteMessage.getData());
        }
    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Logger.d(TAG, "Refreshed token: " + token);
        if (Utilities.isLoggedIn()) {
            sendRegistrationToServer(token);
        } else {
            Configurations.setString("device_token", token);
        }
    }
    // [END on_new_token]


    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        try {
            ServerDelegate.postRegistration(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateNotification(Map<String, String> message) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent;
        //if app is closed...
        Logger.i("fcm", "generating notification...");

        intent = new Intent(this, ScreenSplashActivity.class);

        String defaultString = message.get("default");
        Logger.e("fcm", "defaultString:" + defaultString);

        NotificationResponse notificationResponse = null;
        Gson gson = new GsonBuilder().serializeNulls().create();
        if (defaultString != null && defaultString.startsWith("{")) {
            try {
                notificationResponse = gson.fromJson(defaultString, NotificationResponse.class);

                Logger.e("fcm", "notificationResponse:" + gson.toJson(notificationResponse));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int requestCode = (int) (Math.random() * 10000);

        if (notificationResponse != null) {
            if (notificationResponse.getNotificationType() != null && notificationResponse.getNotificationType().equals("2")) {
                String surveyUrl = Resource.API_TRIP_SURVEY_OPEN + String.format("?user_id=%s&trip_id=%s&notification_id=%s",
                        notificationResponse.getUserId(),
                        notificationResponse.getTripId(),
                        notificationResponse.getNotificationId());
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(surveyUrl));
                intent.setData(Uri.parse(surveyUrl));
            }
            if (notificationResponse.getTripId() != null) {
                intent.putExtra(Constants.NOTIFICATION_KEY_TRIP_ID, notificationResponse.getTripId());
                Logger.d("fcm", "trip_id:" + notificationResponse.getTripId());
            }
        }
        intent.putExtra("message", message.get("message"));
        intent.putExtra("notificationMessage", message.get("message"));
        Logger.i("fcm", "message:" + message);

        intent.putExtra("from_notification", true);

        PendingIntent pendingIntent;
        Logger.e("fcm", "requestCode:" + requestCode);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        //used
        int notificationID = (int) (Math.random() * 10000);
        if (message.get("default") != null && !message.get("default").equalsIgnoreCase("null")) {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            String content;
            if (notificationResponse != null && notificationResponse.getMsg() != null) {
                content = notificationResponse.getMsg();
            } else {
                content = message.get("default");
            }

            String title = this.getApplicationInfo().loadLabel(this.getPackageManager()).toString();

            if (notificationResponse != null && notificationResponse.getTitle() != null && !notificationResponse.getTitle().isEmpty()) {
                title = notificationResponse.getTitle();
            }

            //title = "new Gmap title";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder nb = NotificationHelper.getNotification2(
                        title,
                        content, pendingIntent);
                if (nb != null) {
                    NotificationHelper.notify(notificationID, nb);
                }

            } else {
                Notification builder = new NotificationCompat.Builder(this)
                        .setSmallIcon(getNotificationIcon())
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(content))
                        .setContentText(content)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .setShowWhen(true)
                        .setWhen(System.currentTimeMillis())
                        .setSound(alarmSound)
                        .build();

                mNotificationManager.notify(notificationID, builder);
            }
        }
    }

    private int getNotificationIcon() {
        return R.drawable.ic_notification;
    }
}
