package com.example.project;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.project.ParametersAsync.ServerTask;
import com.example.project.R;
import com.example.project.map.MapsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import static com.example.project.R.string.app_name;

public class NotificationService extends FirebaseMessagingService {
    public static final String ACTION_MESSAGE_BROADCAST=NotificationService.class.getName() + "MessageBroadcast";
    private static final String TAG = "MyService";
    private static final String CHANNEL_1_ID = "channel_1";
    int idNotification = 0;
    public static final Integer NOTIFICATION_REQUESTCODE=101;

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
            sendNotification(title,body,id_parking,distance,address);
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
        UpdateToken();


        //send the Instance ID token to your app server.
       // sendRegistrationToServer(token);  DA IMPLEMENTARE
    }

    public void sendNotification (String title,String body,@Nullable String id_parking,@Nullable String distance,@Nullable String address){
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
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //resultIntent.setAction(Intent.ACTION_MAIN);
            if (id_parking!=null){
            resultIntent.putExtra("id_parking",id_parking);
            resultIntent.putExtra("distance",distance);
            resultIntent.putExtra("address",address);
            }
            resultIntent.putExtra("requestCode",NOTIFICATION_REQUESTCODE);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_REQUESTCODE, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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
    //Get token
    public void UpdateToken(){
        FirebaseInstanceId.getInstance().getInstanceId().
                addOnCompleteListener(new OnCompleteListener<InstanceIdResult>(){
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task){
                        if(!task.isSuccessful()) {
                            Log.i(TAG, "getInstanceIdfailed", task.getException());
                            return;
                        } //Get new InstanceID token
                        String token= task.getResult().getToken();
                        new UpdateDeviceToken().execute(token);
                        Log.d("ll", token);}});
    }

    //TASK PER AGGIORNARE DEVICE TOKEN SUL DB
    private class UpdateDeviceToken extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... tokens) {

            String url = "https://smartparkingpolito.altervista.org/UpdateDeviceToken.php";
            String params = null;
            String token = tokens[0];
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            //Encoding parametri:

            try {
                params = "id_user=" + URLEncoder.encode(user.getUid(), "UTF-8")
                        + "&device_token=" + URLEncoder.encode(token, "UTF-8");

                JSONArray jsonArray= ServerTask.askToServer(params,url);
                //gestisci JsonArray
                JSONObject jsonObjectControl=jsonArray.getJSONObject(0);
                String control=jsonObjectControl.getString("control");
                Log.i("cntr0",control);
                if (control.equals("OK")){   // non esegue l'if
                    //UTENTE REGISTRATO CORRETTAMENTE
                    Log.i(TAG, "Device Token aggiornato correttamente");
                    return true;
                }

            }
            catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

    }
}
