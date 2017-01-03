package com.example.mikel.gestorreuniones;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        //Indicamos explícitamente que el GcmIntentService se hace cargo del intent
        ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
        Log.d("GBR", "Recibido");

        //Inicia el servicio, manteniendo el dispositivo despierto hasta que acabe
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
