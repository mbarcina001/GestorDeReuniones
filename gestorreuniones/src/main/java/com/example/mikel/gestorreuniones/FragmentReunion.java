package com.example.mikel.gestorreuniones;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;


public class FragmentReunion extends Fragment {
    private int colorletra;
    private int colorfondo;
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;
    private SharedPreferences sharedPref;
    private Bundle args;
    private boolean recuperando;

    private String nombreguardado;
    private int diaguardado;
    private int mesguardado;
    private int anyoguardado;
    private int horainicioguardada;
    private int horafinguardada;
    private int minutoinicioguardado;
    private int minutofinguardado;
    private String lugarguardado;

    public FragmentReunion() {  }

    public static FragmentReunion newInstance() {
        FragmentReunion fragment = new FragmentReunion();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.args = this.getArguments();
        this.colorletra = args.getInt("colorletra");
        this.colorfondo = args.getInt("colorfondo");

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener(){
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key){
                if(key.equals("colorfondokey")){
                    colorfondo=prefs.getInt("colorfondokey", 0xfff3f3f3);
                }
                if(key.equals("colorletrakey")){
                    colorletra=prefs.getInt("colorletrakey", 0xff000000);
                }
            }
        };
        sharedPref.registerOnSharedPreferenceChangeListener(prefListener);



        if(savedInstanceState!=null){
            recuperando = true;
            nombreguardado = savedInstanceState.getString("nombre");
            diaguardado = savedInstanceState.getInt("dia");
            mesguardado = savedInstanceState.getInt("mes");
            anyoguardado = savedInstanceState.getInt("anyo");
            horainicioguardada = savedInstanceState.getInt("horainicio");
            horafinguardada = savedInstanceState.getInt("horafin");
            minutoinicioguardado = savedInstanceState.getInt("minutoinicio");
            minutofinguardado = savedInstanceState.getInt("minutofin");
            lugarguardado = savedInstanceState.getString("lugar");

            this.colorletra = savedInstanceState.getInt("ColorLetra");
            this.colorfondo = savedInstanceState.getInt("ColorFondo");
        }else{
            recuperando = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_reunion, container, false);
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        String nombre;
        int dia;
        int mes;
        int anyo;
        int horainicio;
        int horafin;
        int minutoinicio;
        int minutofin;
        String lugar;
        if(recuperando){
            nombre = nombreguardado;
            dia = diaguardado;
            mes = mesguardado;
            anyo = anyoguardado;
            horainicio = horainicioguardada;
            horafin = horafinguardada;
            minutoinicio = minutoinicioguardado;
            minutofin = minutofinguardado;
            lugar = lugarguardado;
        }else{
            nombre = args.getString("nombre");
            dia = args.getInt("dia");
            mes = args.getInt("mes");
            anyo = args.getInt("anyo");
            horainicio = args.getInt("horainicio");
            minutoinicio = args.getInt("minutoinicio");
            horafin = args.getInt("horafin");
            minutofin = args.getInt("minutofin");
            lugar = args.getString("lugar");
        }

        ((TextView)getView().findViewById(R.id.nombre)).setText(nombre);

        String fecha = "";
        Calendar cal = Calendar.getInstance();
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);

        int mesaux = mes;

        if((month==mesaux)&&(year==anyo)){
            if(dayOfMonth==dia){
                fecha = "Hoy";
            }else if(dayOfMonth+1==dia) {
                fecha = "Mañana";
            }else{
                fecha = dia+"/"+mesaux+"/"+anyo;
            }
        }else{
            fecha = dia+"/"+mesaux+"/"+anyo;
        }

        fecha = fecha+"\n";

        String horasinicio;
        if(horainicio<10){
            horasinicio = "0"+horainicio;
        }else{
            horasinicio = String.valueOf(horainicio);
        }


        String minutosinicio;
        if(minutoinicio<10){
            minutosinicio = "0"+minutoinicio;
        }else{
            minutosinicio = String.valueOf(minutoinicio);
        }

        String horasfin;
        if(horafin<10){
            horasfin = "0"+horafin;
        }else{
            horasfin = String.valueOf(horafin);
        }


        String minutosfin;
        if(minutofin<10){
            minutosfin = "0"+minutofin;
        }else{
            minutosfin = String.valueOf(minutofin);
        }

        String hora = horasinicio+":"+minutosinicio+" - "+horasfin+":"+minutosfin;



        fecha = fecha+hora;

        ((TextView)getView().findViewById(R.id.fecha)).setText(fecha);
        ((TextView)getView().findViewById(R.id.lugar)).setText(lugar);

        ponerColores();
    }

    public void ponerColores(){
        ((TextView)getView().findViewById(R.id.nombre)).setTextColor(colorletra);
        ((TextView) getView().findViewById(R.id.fecha)).setTextColor(colorletra);
        ((TextView)getView().findViewById(R.id.lugar)).setTextColor(colorletra);
        this.getView().setBackgroundColor(colorfondo);
    }

    @Override
    public void onResume(){
        super.onResume();
        ponerColores();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        sharedPref.unregisterOnSharedPreferenceChangeListener(prefListener);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        if(recuperando){
            savedInstanceState.putString("nombre", nombreguardado);
            savedInstanceState.putString("lugar", lugarguardado);
            savedInstanceState.putInt("dia", diaguardado);
            savedInstanceState.putInt("mes", mesguardado);
            savedInstanceState.putInt("anyo", anyoguardado);
            savedInstanceState.putInt("horainicio", horainicioguardada);
            savedInstanceState.putInt("horafin", horafinguardada);
            savedInstanceState.putInt("minutoinicio", minutoinicioguardado);
            savedInstanceState.putInt("minutofin", minutofinguardado);
        }else {
            savedInstanceState.putString("nombre", args.getString("nombre"));
            savedInstanceState.putString("lugar", args.getString("lugar"));
            savedInstanceState.putInt("dia", args.getInt("dia"));
            savedInstanceState.putInt("mes", args.getInt("mes"));
            savedInstanceState.putInt("anyo", args.getInt("anyo"));
            savedInstanceState.putInt("horainicio", args.getInt("horainicio"));
            savedInstanceState.putInt("horafin", args.getInt("horafin"));
            savedInstanceState.putInt("minutoinicio", args.getInt("minutoinicio"));
            savedInstanceState.putInt("minutofin", args.getInt("minutofin"));
        }

        savedInstanceState.putInt("ColorLetra", colorletra);
        savedInstanceState.putInt("ColorFondo", colorfondo);
    }
}
