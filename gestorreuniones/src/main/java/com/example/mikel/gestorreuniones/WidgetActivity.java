package com.example.mikel.gestorreuniones;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WidgetActivity extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Código del laboratorio
        //Se recorren todos los widgets que tiene el usuario en el escritorio
        final int N = appWidgetIds.length;

        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews elementosgraficos = new RemoteViews(context.getPackageName(), R.layout.interfaz_widget);
            elementosgraficos.setOnClickPendingIntent(R.id.widget, pendingIntent);

            //Actualizar Próxima Reunión
            SQLiteDatabase db;
            GestorDB manejador = new GestorDB(context, "Reunion", null, 1);
            db = manejador.getReadableDatabase();

            Cursor c = db.rawQuery("SELECT COUNT(*) FROM Reunion WHERE DATETIME(fecha)>=DATETIME('now');", null);
            c.moveToNext();
            int cuenta = c.getInt(0);
            Log.d("CUENTAWIDGET", String.valueOf(cuenta));

            //-----------------------------------------------------------------

            c = db.rawQuery("SELECT DATETIME('now');", null);
            c.moveToNext();
            Log.d("NOW", c.getString(0));

            c = db.rawQuery("SELECT fecha FROM Reunion;", null);

            if (c.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                do {
                    Log.d("REUNION", c.getString(0));
                } while (c.moveToNext());
            }

            //-----------------------------------------------------------------

            String proximareunion;
            if (cuenta == 0) {
                proximareunion = "No hay ninguna reunión programada";
            } else {
                c = db.rawQuery("SELECT * FROM Reunion WHERE DATETIME(fecha)>=DATETIME('now') ORDER BY fecha;", null);
                c.moveToNext();
                String nombrereunion = c.getString(1);
                String lugarreunion = c.getString(10);
                int hora = c.getInt(2);
                String shora;
                if(hora<10){
                    shora = "0"+hora;
                }else{
                    shora = String.valueOf(hora);
                }
                int minutos = c.getInt(3);
                String sminutos;
                if(minutos<10){
                    sminutos = "0"+minutos;
                }else{
                    sminutos = String.valueOf(minutos);
                }
                proximareunion = nombrereunion + " " + context.getResources().getString(R.string.on)
                        + " " + lugarreunion + " " + context.getResources().getString(R.string.at) + " " +
                        shora + ":" + sminutos;
            }
            Log.d("UPDATE", "setTextViewText1");
            elementosgraficos.setTextViewText(R.id.reunion, proximareunion);
            Log.d("UPDATE", proximareunion);

            //Actualizar fecha última actualización
            Calendar calendario = Calendar.getInstance();
            SimpleDateFormat formato = new SimpleDateFormat("HH:mm:ss");
            String horaconformato = formato.format(calendario.getTime());

            elementosgraficos.setTextViewText(R.id.lastdate,
                    context.getResources().getString(R.string.lastdate) + " " + horaconformato);
            appWidgetManager.updateAppWidget(appWidgetId, elementosgraficos);

            c.close();
        }
    }
}
