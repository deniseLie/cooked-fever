package com.example.cooked_fever.game;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

import com.example.cooked_fever.MainActivity;
import com.example.cooked_fever.R;

/**
 * A class for displaying system notifications.
 */
public class NotificationPublisher {

    public static void showNotification(Context context) {
        final String channelId = "my_notifications";
        final Object notificationService = context.getSystemService(Context.NOTIFICATION_SERVICE);
        android.app.NotificationManager notificationManager = (NotificationManager)notificationService;
        final int importance = android.app.NotificationManager.IMPORTANCE_HIGH;

        // Creating notification channel
        NotificationChannel notificationChannel = new NotificationChannel(channelId, "My notifications", importance);
        notificationChannel.setDescription("Test notifications");
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.YELLOW);
        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(new long[]{0, 250, 500, 1000});
        notificationManager.createNotificationChannel(notificationChannel);     // Register channel to system

        // Action when user tap notification
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Build notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId);
        notificationBuilder.setAutoCancel(true)             // dismiss when tapped
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_foreground)    // Show icon in notification bar
                .setPriority(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setTicker("CS205")
                .setContentTitle("Congratulations!")
                .setContentText("You won!")
                .setContentInfo("Click to get back to the menu.");
        Notification notification = notificationBuilder.build();
        notificationManager.notify(1, notification);
    }
}
