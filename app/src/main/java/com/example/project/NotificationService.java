package com.example.project;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.project.R;
import com.example.project.map.MapsActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Map;

import static com.example.project.R.string.app_name;

public class NotificationService extends FirebaseMessagingService {
    public static final String ACTION_MESSAGE_BROADCAST=NotificationService.class.getName() + "MessageBroadcast";
    private static final String TAG = "MyService";
    private static final String CHANNEL_1_ID = "channel_1";
    int idNotification = 0;

    public NotificationService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            String title=data.get("title");
            String body=data.get("body");
            String id_parking=null;
            String address=null;
            String distance=null;
            if (data.containsKey("parking_id")) id_parking=data.get("parking_id");
            if(data.containsKey("address")) distance=data.get("distance");
            if(data.containsKey("address")) address=data.get("address");
            sendNotification(title,body);
            sendBroadcastMessage(id_parking, distance,address);


        }

        /*
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }*/

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);


        //send the Instance ID token to your app server.
       // sendRegistrationToServer(token);  DA IMPLEMENTARE
    }

    public void sendNotification (String title,String body){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // creo notifica con i channel
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.main_ic_logo_onlyimage)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(body))
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

          //Setto il ringtone
            Uri alarmSound = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(alarmSound);
          //Creo Pending Intent
            Intent resultIntent = new Intent(this, MapsActivity.class);
            resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resultIntent.setAction(Intent.ACTION_MAIN);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(idNotification, builder.build());
            idNotification++;

        }else{
            // creo notifica senza i channel
            Intent intent = new Intent(this, MapsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.main_ic_logo_onlyimage)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(idNotification, builder.build());
            idNotification++;
        }

    }
    private void sendBroadcastMessage(@Nullable String id_parking,@Nullable String distance,@Nullable String address) {
            Intent intent = new Intent(ACTION_MESSAGE_BROADCAST);
        if (id_parking != null && distance != null && address != null) {
            intent.putExtra("id_parking", id_parking);
            intent.putExtra("distance", distance);
            intent.putExtra("address", address);
        }
            Log.i("BroadcastSender","ok");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }
}
