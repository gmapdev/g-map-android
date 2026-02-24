package com.ibigroup.mobile.otp.android.manager;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION;
import static com.ibigroup.mobile.otp.android.utility.Constants.NOTIFICATION_ID;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.ForegroundInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ibigroup.mobile.otp.android.ActiveRouteActivity;
import com.ibigroup.mobile.otp.android.R;
import com.ibigroup.mobile.otp.android.utility.Logger;


public class LocationWork extends Worker {
    private final Context context;
    private final NewLocationManager newLocationManager;

    public LocationWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        newLocationManager = NewLocationManager.getInstance(context);

    }

    @NonNull
    @Override
    public Result doWork() {
        setForegroundAsync(showNotification());
        while (true) {
            if (1 > 2) {
                break;
            }
            newLocationManager.startLocationUpdates();
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Result.success();
    }

    @NonNull
    private ForegroundInfo showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return new ForegroundInfo(NOTIFICATION_ID, CreateNotification(), FOREGROUND_SERVICE_TYPE_LOCATION);
        } else {
            return new ForegroundInfo(NOTIFICATION_ID, CreateNotification());
        }
    }

    private Notification CreateNotification() {
        String CHANNEL_ID = "100";
        String title = context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, title, NotificationManager.IMPORTANCE_HIGH));
        }
        Intent exitIntent = new Intent(context, ActiveRouteActivity.class);
        exitIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent piExit;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            piExit = PendingIntent.getActivity(context, (int) System.currentTimeMillis() - 1, exitIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        } else {
            piExit = PendingIntent.getActivity(context, (int) System.currentTimeMillis() - 1, exitIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }
        PendingIntent deleteIntent = WorkManager.getInstance(context)
                .createCancelPendingIntent(getId());
        String message = context.getString(R.string.tap_retrun_trip, title);
        Notification nb = new NotificationCompat.Builder(context, CHANNEL_ID).setContentTitle("").setTicker("")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSmallIcon(R.drawable.ic_notification)
                .setOngoing(false)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setDeleteIntent(deleteIntent)
                .setContentIntent(piExit)
                .build();

        return nb;
    }

    @Override
    public void onStopped() {
        Logger.d("ClearFromRecentService", "stop work");
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID);
        super.onStopped();
    }

}
