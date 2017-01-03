package com.example.mikel.gestorreuniones;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;


public class Eliminar extends Fragment {
    private ArrayList<String> nombres;
    private ArrayList<Integer> ids;
    private int colorletra;
    private int colorfondo;
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;
    private SharedPreferences sharedPref;

    private OnFragmentInteractionListener mListener;

    public Eliminar() {      }

    public static Eliminar newInstance() {
        Eliminar fragment = new Eliminar();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle args = this.getArguments();
            this.colorletra = args.getInt("colorletra");
            this.colorfondo = args.getInt("color");
        }else{
            this.colorletra = savedInstanceState.getInt("ColorLetra");
            this.colorfondo = savedInstanceState.getInt("ColorFondo");
        }

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener(){
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key){
                if(key.equals("colorfondokey")){
                    colorfondo=prefs.getInt("colorfondokey", 0xfff3f3f3);
                    Log.d("Eliminar-ColorFondo", String.valueOf(colorfondo));
                }
                if(key.equals("colorletrakey")){
                    colorletra=prefs.getInt("colorletrakey", 0xff000000);
                    Log.d("Eliminar-ColorLetra", String.valueOf(colorletra));
                }
            }
        };
        sharedPref.registerOnSharedPreferenceChangeListener(prefListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_eliminar, container, false);
    }

    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);
        anadirLista();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void eliminar(Integer id);
    }

    public void anadirLista(){
        ListView lalista = (ListView) getView().findViewById(R.id.listView);
        final ArrayAdapter<String> adaptador = new ArrayAdapter<String>
                (getContext(), android.R.layout.simple_list_item_1) {
            public View getView(int posicion, View convertView, ViewGroup parent) {
                View view = super.getView(posicion, convertView, parent);
                TextView text = (TextView) view.findViewById((android.R.id.text1));
                text.setTextColor(colorletra);
                return view;
            }
        };

        nombres = new ArrayList<String>();
        ids = new ArrayList<Integer>();
        GestorDB manejador = new GestorDB(this.getContext(), "Amigos", null, 2);
        SQLiteDatabase db = manejador.getWritableDatabase();
        Cursor c = db.rawQuery("Select * FROM Reunion;", null);
        while (c.moveToNext()) {
            nombres.add(c.getString(1));
            ids.add(c.getInt(0));
        }
        c.close();

        Iterator<String> iterador = nombres.iterator();

        while (iterador.hasNext()) {
            adaptador.add(iterador.next());
        }

        lalista.setAdapter(adaptador);

        lalista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int posicion = position;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder
                        .setTitle(getResources().getString(R.string.preguntaeliminar) + " '" +
                                nombres.get(position) + "' " + getResources().getString(R.string.delistareuniones) + "?")
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.si), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mListener.eliminar(ids.get(posicion));
                                adaptador.remove(nombres.get(posicion));
                                for (int i = posicion; i < nombres.size() - 1; i++) {
                                    nombres.set(i, nombres.get(i + 1));
                                    ids.add(i, ids.get(i + 1));
                                }
                                nombres.remove(nombres.size() - 1);
                                ids.remove(ids.size() - 1);
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.no),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    public void onResume(){
        super.onResume();
        Log.d("Eliminar", "OnResume");
        anadirLista();
    }

    public void onDestroy(){
        super.onDestroy();
        sharedPref.unregisterOnSharedPreferenceChangeListener(prefListener);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("ColorLetra", colorletra);
        savedInstanceState.putInt("ColorFondo", colorfondo);
    }
}
