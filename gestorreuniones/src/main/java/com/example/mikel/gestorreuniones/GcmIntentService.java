package com.example.mikel.gestorreuniones;


import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;

    public GcmIntentService(){
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if(!extras.isEmpty()){
            if(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)){
                sendNotification("Send error: " + extras.toString());
            }else if(GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)){
                sendNotification("Deleted messages on server: " + extras.toString());
            }else if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)){
                sendNotification("Nueva reunión añadida!");
            }
        }

        SQLiteDatabase db;
        GestorDB manejador = new GestorDB(MainActivity.getAppContext(), "Reunion", null, 1);
        db = manejador.getReadableDatabase();

        MainActivity.anadirReunionStatic(extras.getString("Nombre"), Integer.valueOf(extras.getString("HoraInicio")),
                Integer.valueOf(extras.getString("MinutoInicio")), Integer.valueOf(extras.getString("HoraFin")),
                Integer.valueOf(extras.getString("MinutoFin")), Integer.valueOf(extras.getString("Dia")),
                Integer.valueOf(extras.getString("Mes")), Integer.valueOf(extras.getString("Anyo")),
                extras.getString("Fecha"), extras.getString("Lugar"), db, MainActivity.getAppContext(), false);

        //Liberar el bloqueo
        GcmBroadcastReceiver.completeWakefulIntent(intent);
        db.close();
    }

    private void sendNotification(String msg) {
        Log.d("SERVICE", msg);
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent myintent = new Intent(this, MainActivity.class);
        myintent.putExtra("message", msg);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                myintent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("GCM Notification")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
