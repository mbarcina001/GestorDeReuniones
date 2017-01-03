package com.example.mikel.gestorreuniones;


import android.app.AlarmManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.format.DateUtils;
import android.util.Log;

import java.util.Date;
import java.util.HashSet;
import java.util.regex.Pattern;

public class SyncCalendar {

    static Cursor cursor;

    public static void readCalendar() {
        //Fuente de buena parte del código: http://stackoverflow.com/questions/5883938/getting-events-from-calendar

        Context context = MainActivity.getAppContext();
        ContentResolver contentResolver = context.getContentResolver();

        cursor = contentResolver.query(Uri.parse("content://com.android.calendar/calendars"),
                (new String[] { "_id", "calendar_displayName"}), null, null, null);

        HashSet<String> calendarIds = new HashSet<String>();

        try{
            System.out.println("Count="+cursor.getCount());
            if(cursor.getCount() > 0){
                while (cursor.moveToNext()) {
                    String _id = cursor.getString(0);
                    calendarIds.add(_id);
                }
            }
        }
        catch(AssertionError ex){
            ex.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        // For each calendar, display all the events from the previous week to the end of next week.
        for (String id : calendarIds) {
            Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
            long now = new Date().getTime();

            ContentUris.appendId(builder, now);
            ContentUris.appendId(builder, now + DateUtils.DAY_IN_MILLIS * 10000);

            Cursor eventCursor = contentResolver.query(builder.build(),
                    new String[]  { "title", "begin", "end", "eventLocation"}, "calendar_id=" + id,
                    null, "startDay ASC, startMinute ASC");

            if(eventCursor.getCount()>0)
            {

                if(eventCursor.moveToFirst())
                {
                    do
                    {

                        String title = eventCursor.getString(0);
                        final Date begin = new Date(eventCursor.getLong(1));
                        final Date end = new Date(eventCursor.getLong(2));
                        final String location = eventCursor.getString(3);

                        Pattern p = Pattern.compile(" ");
                        String[] items = p.split(begin.toString());
                        int calendar_metting_beginday,calendar_metting_beginmonth,calendar_metting_beginyear,
                                 calendar_metting_beginhour, calendar_metting_beginminute;
                        String scalendar_metting_begintime, scalendar_metting_beginday,
                            scalendar_metting_beginmonth, scalendar_metting_beginhour, scalendar_metting_beginminute;

                        calendar_metting_beginday = Integer.valueOf(items[2]);
                        scalendar_metting_beginday = items[2];
                        calendar_metting_beginmonth = getMonthNumber(items[1]);
                        if(calendar_metting_beginmonth<10){
                            scalendar_metting_beginmonth = "0"+String.valueOf(getMonthNumber(items[1]));
                        }else{
                            scalendar_metting_beginmonth = String.valueOf(getMonthNumber(items[1]));
                        }
                        calendar_metting_beginyear = Integer.valueOf(items[5]);
                        scalendar_metting_begintime = items[3];
                        calendar_metting_beginhour = Integer.valueOf(scalendar_metting_begintime.substring(0, 2));
                        calendar_metting_beginminute = Integer.valueOf(scalendar_metting_begintime.substring(3, 5));
                        scalendar_metting_beginhour = scalendar_metting_begintime.substring(0, 2);
                        scalendar_metting_beginminute = scalendar_metting_begintime.substring(3, 5);

                        String[] enditems = p.split(end.toString());
                        int calendar_metting_endday,calendar_metting_endmonth,calendar_metting_endyear,
                                calendar_metting_endhour, calendar_metting_endminute;
                        String scalendar_metting_endtime;

                        calendar_metting_endday = Integer.valueOf(enditems[2]);
                        calendar_metting_endmonth = getMonthNumber(items[1]);
                        calendar_metting_endyear = Integer.valueOf(enditems[5]);
                        scalendar_metting_endtime = enditems[3];
                        calendar_metting_endhour = Integer.valueOf(scalendar_metting_endtime.substring(0, 2));
                        calendar_metting_endminute = Integer.valueOf(scalendar_metting_endtime.substring(3, 5));

                        if(calendar_metting_beginday==calendar_metting_endday){
                            String fecha = calendar_metting_beginyear + "-" + scalendar_metting_beginmonth + "-" +
                                    scalendar_metting_beginday + " " + scalendar_metting_beginhour + ":" +
                                    scalendar_metting_beginminute + ":00";
                            SQLiteDatabase db;
                            GestorDB manejador = new GestorDB(MainActivity.getAppContext(), "Reunion", null, 1);
                            db = manejador.getReadableDatabase();

                            MainActivity.anadirReunionStatic(title, calendar_metting_beginhour, calendar_metting_beginminute,
                                    calendar_metting_endhour, calendar_metting_endminute, calendar_metting_beginday,
                                    calendar_metting_beginmonth, calendar_metting_beginyear, fecha, location, db,
                                    MainActivity.getAppContext(), false);

                            db.close();
                        }else if(calendar_metting_beginmonth==calendar_metting_endmonth){
                            int dayaux = calendar_metting_beginday;
                            while(dayaux<calendar_metting_endday){
                                int horaaux=0;
                                int minutoaux=0;
                                if(dayaux==calendar_metting_beginday){
                                    horaaux=calendar_metting_beginhour;
                                    minutoaux=calendar_metting_beginminute;
                                }else{
                                    title = title+" (cont)";
                                }

                                String dayaux2;
                                if(dayaux<10){
                                    dayaux2="0"+String.valueOf(dayaux);
                                }else{
                                    dayaux2=String.valueOf(dayaux);
                                }

                                String horaaux2;
                                if(horaaux<10){
                                    horaaux2="0"+String.valueOf(horaaux);
                                }else{
                                    horaaux2=String.valueOf(horaaux);
                                }

                                String minutoaux2;
                                if(minutoaux<10){
                                    minutoaux2="0"+String.valueOf(minutoaux);
                                }else{
                                    minutoaux2=String.valueOf(minutoaux);
                                }

                                String fecha = calendar_metting_beginyear + "-" + scalendar_metting_beginmonth + "-" + dayaux2 +
                                         " " + horaaux2 + ":" + minutoaux2 + ":00";

                                SQLiteDatabase db;
                                GestorDB manejador = new GestorDB(MainActivity.getAppContext(), "Reunion", null, 1);
                                db = manejador.getReadableDatabase();

                                MainActivity.anadirReunionStatic(title, horaaux, minutoaux,
                                        23, 59, dayaux,
                                        calendar_metting_beginmonth, calendar_metting_beginyear, fecha, location, db,
                                        MainActivity.getAppContext(), false);

                                db.close();
                                dayaux++;
                            }

                            String dayaux2;
                            if(dayaux<10){
                                dayaux2="0"+String.valueOf(dayaux);
                            }else{
                                dayaux2=String.valueOf(dayaux);
                            }

                            String fecha = calendar_metting_beginyear + "-" + scalendar_metting_beginmonth + "-"
                                     + dayaux2 + " 00:00:00";

                            SQLiteDatabase db;
                            GestorDB manejador = new GestorDB(MainActivity.getAppContext(), "Reunion", null, 1);
                            db = manejador.getReadableDatabase();

                            MainActivity.anadirReunionStatic(title, 0, 0,
                                    calendar_metting_endhour, calendar_metting_endminute, dayaux,
                                    calendar_metting_beginmonth, calendar_metting_beginyear, fecha, location, db,
                                    MainActivity.getAppContext(), false);

                            db.close();

                        }


                    }
                    while(eventCursor.moveToNext());
                }
            }
        }

    }

    public static int getMonthNumber(String month){
        int rdo = 0;
        switch(month){
            case "Jan":
                rdo = 1;
                break;
            case "Feb":
                rdo = 2;
                break;
            case "Mar":
                rdo = 3;
                break;
            case "Apr":
                rdo = 4;
                break;
            case "May":
                rdo = 5;
                break;
            case "Jun":
                rdo = 6;
                break;
            case "Jul":
                rdo = 7;
                break;
            case "Aug":
                rdo = 8;
                break;
            case "Sep":
                rdo = 9;
                break;
            case "Oct":
                rdo = 10;
                break;
            case "Nov":
                rdo = 11;
                break;
            case "Dec":
                rdo = 12;
                break;
        }
        return rdo;
    }
}
