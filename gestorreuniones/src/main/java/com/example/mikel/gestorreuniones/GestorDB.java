package com.example.mikel.gestorreuniones;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GestorDB extends SQLiteOpenHelper {
    private static final String nombreDB = "reunion.db";
    private static final int versionDB = 1;

    public GestorDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, nombreDB, factory, versionDB);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE Reunion('codigo' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'nombre' varchar(50), 'horainicio' int, minutoinicio int, horafin int, " +
                "'minutofin' varchar(30), 'dia' int, 'mes' int, 'anyo' int, 'fecha' datetime, 'lugar' varchar(80)); ");
    }

    public void onUpgrade(SQLiteDatabase db, int n1, int n2){        }
}
