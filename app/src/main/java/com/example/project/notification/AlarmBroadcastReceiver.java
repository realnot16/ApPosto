package com.example.project.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.project.R;
import com.example.project.map.MapsActivity;

import static com.example.project.notification.App.CHANNEL_1_ID;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_MESSAGE_BROADCAST=NotificationService.class.getName() + "MessageBroadcast";
    public static final String ACTION_ALARM= "com.example.alarms.ACTION_ALARM";
    private int notificationId = 0;

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        SharedPreferences prefs = context.getSharedPreferences("com.example.alarms", Context.MODE_PRIVATE);

        if(ACTION_ALARM.equals(intent.getAction())){
            //Al click su "imposta Alarm" mostra un toast
            Log.i("mylog", "Ecco l'alarm.");
            //Toast.makeText(context, ACTION_ALARM, Toast.LENGTH_SHORT).show();
            sendNotification();
        }else if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            //Ho riavviato il dispositivo
            Log.i("mylog", "Dispositivo riavviato");
            Toast.makeText(context, "BOOT_COMPLETED", Toast.LENGTH_SHORT).show();

            long data = prefs.getLong("tempo", 0);
            if(System.currentTimeMillis()<data)
                setAlarm(data-System.currentTimeMillis());
        }
    }

    private void setAlarm(long data) {
        Intent intentToFire = new Intent(context, AlarmBroadcastReceiver.class);
        intentToFire.setAction(AlarmBroadcastReceiver.ACTION_ALARM);

        PendingIntent alarmIntent = MapsActivity.getAlarmIntent();
        AlarmManager alarmManager = MapsActivity.getAlarmManager();
        alarmManager.set(AlarmManager.RTC_WAKEUP, data, alarmIntent);
    }

    private void sendNotification(){
        //Accendo lo schermo
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = Build.VERSION.SDK_INT >= 20 ? pm.isInteractive() : pm.isScreenOn();
        if(!isScreenOn){
            PowerManager.WakeLock wl = pm.newWakeLock((PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "myApp:notificationLock");
            wl.acquire(3000);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.main_ic_logo_onlyimage)
                .setContentTitle(context.getResources().getString(R.string.alarm_notif_title))
                .setContentText(context.getResources().getString(R.string.alarm_notif_text))
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // create intent that will be broadcast.
        Intent resultIntent = new Intent(context, MapsActivity.class);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        resultIntent.putExtra("time", "fromBroadcast");


        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        sendBroadcastMessage();

        notificationManager.notify(notificationId, builder.build());
        notificationId++;
    }

    private void sendBroadcastMessage() {
        Intent intent = new Intent(ACTION_MESSAGE_BROADCAST);
            intent.putExtra("time", "fromBroadcast");

        Log.i("BroadcastSender","ok");

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }



}
