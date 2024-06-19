package com.lksnext.parkingagarcia.domain;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class EndReservationReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "reservation")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Reservation Reminder")
                .setContentText("Your reservation ends in 15 minutes!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(201, builder.build());
        } else {
            Log.d("EndReservationReminderReceiver", "no permission to post notifications");
        }
    }
}
