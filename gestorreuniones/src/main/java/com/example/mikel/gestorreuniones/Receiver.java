package com.example.mikel.gestorreuniones;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class Receiver extends BroadcastReceiver {
    //Esta clase recibe los Broadcast lanzados por el AlarmManager creado en el main

    public Receiver(){
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Cuando se recibe un Broadcast se crea una notificación y se lanza

        String nombre = intent.getStringExtra("NombreReunion");
        int hora = intent.getIntExtra("HoraInicio", 0);
        int minutos = intent.getIntExtra("MinutoInicio", 0);

        Log.d("Nombre", nombre);
        Log.d("HoraInicio", String.valueOf(hora));
        Log.d("MinutoInicio", String.valueOf(minutos));

        String horasString;
        if(hora>10){
            horasString = String.valueOf(hora);
        }else{
            horasString = "0"+hora;
        }

        String minutosString;
        if(minutos>10){
            minutosString = String.valueOf(minutos);
        }else{
            minutosString = "0"+minutos;
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
        .setContentTitle(context.getResources().getString(R.string.reunion)+": "+nombre)
        .setContentText(context.getResources().getString(R.string.a_las)+" "+horasString+":"+minutosString)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setTicker("Aviso de reunión")
        .setDefaults(Notification.DEFAULT_SOUND);

        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(1, mBuilder.build());
    }

}