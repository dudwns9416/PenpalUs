package com.sc.fopa.penpalus.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sc.fopa.penpalus.activity.HomeActivity;
import com.sc.fopa.penpalus.R;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by fopa on 2017-11-15.
 */

public class PenpalMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0){

            try {
                String title = URLDecoder.decode(remoteMessage.getData().get("title"),"utf-8");
                String message = URLDecoder.decode(remoteMessage.getData().get("message"),"utf-8");
                sendNotification(title,message);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if (remoteMessage.getNotification() != null) {
            try {
                String test = URLDecoder.decode(remoteMessage.getNotification().getBody(),"utf-8");
                Log.d(TAG, "Message Notification Body: " + test);
                sendNotification(test,"메시지가 도착했습니다");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    private void sendNotification(String messageTitle, String messageBody) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_fcm_logo)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 , notificationBuilder.build());
    }
}
