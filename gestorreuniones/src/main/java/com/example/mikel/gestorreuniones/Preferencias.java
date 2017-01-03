package com.example.mikel.gestorreuniones;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class Preferencias extends PreferenceActivity {

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
        mContext = this;

        Preference button = (Preference)findPreference(getString(R.string.sync));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SyncCalendar.readCalendar();
                return true;
            }
        });

        Preference button2 = (Preference)findPreference(getString(R.string.clean));
        button2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                alertDialogBuilder
                        .setTitle(getResources().getString(R.string.removeconfirm))
                        .setCancelable(false)
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SQLiteDatabase db;
                                GestorDB manejador = new GestorDB(MainActivity.getAppContext(), "Reunion", null, 1);
                                db = manejador.getReadableDatabase();
                                String s = "delete from Reunion;";
                                db.execSQL(s);
                                db.close();
                            }
                        })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                return true;
            }
        });
    }
}
