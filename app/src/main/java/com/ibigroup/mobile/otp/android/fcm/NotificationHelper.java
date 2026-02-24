/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibigroup.mobile.otp.android.fcm;

import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.Build;

import com.ibigroup.mobile.otp.android.MyApplication;
import com.ibigroup.mobile.otp.android.R;
import com.ibigroup.mobile.otp.android.utility.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Helper class to manage notification channels, and create notifications.
 */
public class NotificationHelper {
    public static final String PRIMARY_CHANNEL = "application";
    public static final String SECONDARY_CHANNEL = "general";

    /**
     * Registers notification channels, which can be used later by individual notifications.
     */

    public static void generateChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chan1 = new NotificationChannel(PRIMARY_CHANNEL,
                    MyApplication.getInstance().getString(R.string.notification_channel_default), NotificationManager.IMPORTANCE_LOW);
            chan1.enableLights(false);
            chan1.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            chan1.setShowBadge(false);

            Logger.i("notification", "visibility:" + chan1.getLockscreenVisibility());
            getManager().createNotificationChannel(chan1);

            NotificationChannel temp = getManager().getNotificationChannel(PRIMARY_CHANNEL);
            if (temp != null) {
                temp.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
                Logger.i("notification", "new visibility:" + temp.getLockscreenVisibility());
            }

            NotificationChannel chan2 = new NotificationChannel(SECONDARY_CHANNEL,
                    MyApplication.getInstance().getString(R.string.notification_channel_second), NotificationManager.IMPORTANCE_HIGH);
            chan2.enableLights(true);
            chan2.setLightColor(Color.BLUE);
            chan2.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            getManager().createNotificationChannel(chan2);
        }

    }

    /**
     * Get a notification of type 1
     * <p>
     * Provide the builder rather than the notification it's self as useful for making notification
     * changes.
     *
     * @param title the title of the notification
     * @param body  the body text for the notification
     * @return the builder as it keeps a reference to the notification (since API 24)
     */
    public static Notification.Builder getNotification1(String title, String body,
                                                        PendingIntent pendingIntent,
                                                        PendingIntent deleteIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (getManager().getNotificationChannel(PRIMARY_CHANNEL) == null) {
                generateChannels();
            }
            return new Notification.Builder(MyApplication.getInstance().getApplicationContext(), PRIMARY_CHANNEL)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setStyle(new Notification.BigTextStyle()
                            .bigText(body))
                    .setSmallIcon(getSmallIcon())
                    .setContentIntent(pendingIntent)
                    .setDeleteIntent(deleteIntent)
                    .setOngoing(false)
                    .setAutoCancel(false);
        } else {
            return null;
        }
    }

    /**
     * Build notification for secondary channel.
     *
     * @param title Title for notification.
     * @param body  Message for notification.
     * @return A Notification.Builder configured with the selected channel and details
     */
    public static Notification.Builder getNotification2(String title, String body,
                                                        PendingIntent pendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (getManager().getNotificationChannel(SECONDARY_CHANNEL) == null) {
                generateChannels();
            }
            return new Notification.Builder(MyApplication.getInstance().getApplicationContext(), SECONDARY_CHANNEL)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setStyle(new Notification.BigTextStyle()
                            .bigText(body))
                    .setSmallIcon(getSmallIcon())
                    .setContentIntent(pendingIntent)
                    .setOngoing(false)
                    .setShowWhen(true)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true);
        } else {
            return null;
        }
    }

    /**
     * Send a notification.
     *
     * @param id           The ID of the notification
     * @param notification The notification object
     */
    public static void notify(int id, Notification.Builder notification) {
        getManager().notify(id, notification.build());
    }

    /**
     * Get the small icon for this app
     *
     * @return The small icon resource id
     */
    private static int getSmallIcon() {
//        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
//                && (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.M);
//        return useWhiteIcon ? R.drawable.ic_notification : R.drawable.ic_launcher;
        return R.drawable.ic_notification;
    }

    /**
     * Get the notification manager.
     * <p>
     * Utility method as this helper works with it a lot.
     *
     * @return The system service NotificationManager
     */
    public static NotificationManager getManager() {
        return (NotificationManager) MyApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    private static boolean isNotificationEnabled(Context mContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return getManager().areNotificationsEnabled();
            } else {
                AppOpsManager mAppOps = (AppOpsManager) mContext.getSystemService(Context.APP_OPS_SERVICE);
                ApplicationInfo appInfo = mContext.getApplicationInfo();
                String pkg = mContext.getApplicationContext().getPackageName();
                int uid = appInfo.uid;
                Class appOpsClass;
                try {
                    appOpsClass = Class.forName(AppOpsManager.class.getName());
                    Method checkOpNoThrowMethod =
                            appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);

                    Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
                    int value = (int) opPostNotificationValue.get(Integer.class);
                    return ((int) checkOpNoThrowMethod.invoke(mAppOps, value, uid,
                            pkg) == AppOpsManager.MODE_ALLOWED);
                } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException
                         | InvocationTargetException | IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } else {
            return false;
        }
    }
}
