package com.example.mausami.firebasepractice;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.mausami.firebasepractice.helpers.SharedPreferencesHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.example.mausami.firebasepractice.MainActivity.adapter;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FDM_SERVICE";

    @SuppressLint("WrongThread")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Log data to Log Cat
        Log.i(TAG, "From: " + remoteMessage.getFrom());
        Log.i(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());


        // Create notification to device
        createNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());

        // Show message
        showMessage(remoteMessage.getFrom(), remoteMessage.getNotification().getBody());
    }

    /**
     * Create notification on device
     *
     * @param title
     * @param messageBody
     */
    private void createNotification(String title, String messageBody) {
        Intent intent = new Intent( this , MainActivity. class );
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultIntent = PendingIntent.getActivity( this , 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder( this, "CHANNEL_ID")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel( true )
                .setSound(notificationSoundURI)
                .setContentIntent(resultIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, mNotificationBuilder.build());

        return;
    }

    /**
     * Show Message
     *
     * @param from
     * @param messageBody
     */
    private void showMessage(String from, String messageBody) {
        SharedPreferencesHelper.putString(SharedPreferencesHelper.MESSAGE_BODY, from, getApplicationContext());
        SharedPreferencesHelper.putString(SharedPreferencesHelper.MESSAGE_BODY, messageBody, getApplicationContext());
        startService(new Intent(getApplicationContext(),MyShowMessageService.class));
    }
}
