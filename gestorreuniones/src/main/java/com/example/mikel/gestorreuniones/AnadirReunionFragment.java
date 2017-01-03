package com.example.mikel.gestorreuniones;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;


public class AnadirReunionFragment extends Fragment {

    private String nombreguardado;
    private String lugarguardado;
    private int diaguardado;
    private int mesguardado;
    private int anyoguardado;
    private int horainicioguardada;
    private int minutoinicioguardado;
    private int horafinguardada;
    private int minutofinguardado;

    private OnFragmentInteractionListener mListener;
    private int colorletra;
    private int colorfondo;

    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;
    private SharedPreferences sharedPref;

    public AnadirReunionFragment() {    }

    public static AnadirReunionFragment newInstance() {
        AnadirReunionFragment fragment = new AnadirReunionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null){
            Log.d("NOT NULL", "savedInstanceState");
            if(savedInstanceState.getString("nombre")!=null){
                Log.d("Load",savedInstanceState.getString("nombre"));
                nombreguardado = savedInstanceState.getString("nombre");
            }if(savedInstanceState.getString("lugar")!=null){
                lugarguardado = savedInstanceState.getString("lugar");
            }
            diaguardado = savedInstanceState.getInt("dia");
            mesguardado = savedInstanceState.getInt("mes");
            anyoguardado = savedInstanceState.getInt("anyo");
            horainicioguardada = savedInstanceState.getInt("horainicio");
            minutoinicioguardado = savedInstanceState.getInt("minutoinicio");
            horafinguardada = savedInstanceState.getInt("horafin");
            minutofinguardado = savedInstanceState.getInt("minutofin");

            this.colorletra = savedInstanceState.getInt("ColorLetra");
            this.colorfondo = savedInstanceState.getInt("ColorFondo");
        }else {
            Bundle args = this.getArguments();
            this.colorletra = args.getInt("colorletra");
            this.colorfondo = args.getInt("colorfondo");
        }

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener(){
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key){
                if(key.equals("colorfondokey")){
                    colorfondo=prefs.getInt("colorfondokey", 0xfff3f3f3);
                    Log.d("AF-ColorFondo", String.valueOf(colorfondo));
                }
                if(key.equals("colorletrakey")){
                    colorletra=prefs.getInt("colorletrakey", 0xff000000);
                    Log.d("AF-ColorLetra", String.valueOf(colorletra));
                }
            }
        };
        sharedPref.registerOnSharedPreferenceChangeListener(prefListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_anadir_reunion, container, false);
    }

    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);

        final TimePicker timePicker1 = (TimePicker) getView().findViewById(R.id.timePicker1);
        timePicker1.setIs24HourView(true);
        final TimePicker timePicker2 = (TimePicker) getView().findViewById(R.id.timePicker2);
        timePicker2.setIs24HourView(true);
        final DatePicker datePicker1 = (DatePicker) getView().findViewById(R.id.datePicker1);
        datePicker1.setCalendarViewShown(false);

        Button btn = (Button) getView().findViewById(R.id.btnAceptar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = ((EditText) getView().findViewById(R.id.nombre)).getText().toString();
                String lugar = ((EditText) getView().findViewById(R.id.lugar)).getText().toString();
                if ((nombre.contains("'")) || (nombre.contains("\"")) || (lugar.contains("'")) || (lugar.contains("\""))) {
                    Context context = getView().getContext();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, "No se pueden introducir comillas simples o dobles", duration);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                } else {
                    int horainicio = timePicker1.getCurrentHour();
                    int minutoinicio = timePicker1.getCurrentMinute();
                    int horafin = timePicker2.getCurrentHour();
                    int minutofin = timePicker2.getCurrentMinute();
                    int dia = datePicker1.getDayOfMonth();
                    int mes = datePicker1.getMonth() + 1;
                    int anyo = datePicker1.getYear();
                    Log.d("MES", String.valueOf(mes));
                    String fecha = obtenerFecha(horainicio, minutoinicio, dia, mes, anyo);
                    Log.d("AnadirReunionFragment", nombre);
                    mListener.anadirReunion(nombre, horainicio, minutoinicio, horafin, minutofin, dia, mes, anyo, fecha, lugar, true);
                }
            }
        });

        ponerColores();
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
        void anadirReunion(String nombre, int horaInicio, int minutoInicio, int horaFin,
                           int minutoFin, int dia, int mes, int anyo, String fecha, String lugar, boolean avisar);
    }

    public String obtenerFecha(int horainicio, int minutoinicio, int dia, int mes, int anyo){
        String fecha = anyo+"-";
        if(mes<10){
            fecha=fecha+0;
        }
        fecha = fecha+mes+"-";
        if(dia<10){
            fecha=fecha+0;
        }
        fecha = fecha+dia+" ";
        if(horainicio<10){
            fecha=fecha+0;
        }
        fecha=fecha+horainicio+":";
        if(minutoinicio<10){
            fecha=fecha+0;
        }
        fecha=fecha+minutoinicio+":00";
        return fecha;
    }

    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.d("SAVE", ((EditText) getView().findViewById(R.id.nombre)).getText().toString());
        savedInstanceState.putString("nombre", ((EditText) getView().findViewById(R.id.nombre)).getText().toString());
        savedInstanceState.putString("lugar", ((EditText) getView().findViewById(R.id.lugar)).getText().toString());
        savedInstanceState.putInt("horainicio", ((TimePicker) getView().findViewById(R.id.timePicker1)).getCurrentHour());
        savedInstanceState.putInt("minutoinicio", ((TimePicker) getView().findViewById(R.id.timePicker1)).getCurrentMinute());
        savedInstanceState.putInt("horafin", ((TimePicker) getView().findViewById(R.id.timePicker2)).getCurrentHour());
        savedInstanceState.putInt("minutofin", ((TimePicker) getView().findViewById(R.id.timePicker2)).getCurrentMinute());
        savedInstanceState.putInt("dia", ((DatePicker) getView().findViewById(R.id.datePicker1)).getDayOfMonth());
        savedInstanceState.putInt("mes", ((DatePicker) getView().findViewById(R.id.datePicker1)).getMonth());
        savedInstanceState.putInt("anyo", ((DatePicker) getView().findViewById(R.id.datePicker1)).getYear());

        savedInstanceState.putInt("ColorLetra", colorletra);
        savedInstanceState.putInt("ColorFondo", colorfondo);
    }

    public void onResume() {
        Log.d("AnadirReunion", "OnResume");
        super.onResume();
        if (nombreguardado != null) {
            Log.d("Nombreguardado", nombreguardado);
            ((EditText) getView().findViewById(R.id.nombre)).setText(nombreguardado);
        }
        if (nombreguardado != null) {
            ((EditText) getView().findViewById(R.id.lugar)).setText(lugarguardado);
        }
        if (horainicioguardada != 0) {
            ((TimePicker) getView().findViewById(R.id.timePicker1)).setCurrentHour(horainicioguardada);
        }
        if(minutoinicioguardado != 0) {
            ((TimePicker) getView().findViewById(R.id.timePicker1)).setCurrentMinute(minutoinicioguardado);
        }
        if(horafinguardada != 0) {
            ((TimePicker) getView().findViewById(R.id.timePicker2)).setCurrentHour(horafinguardada);
        }
        if(minutofinguardado != 0) {
            ((TimePicker) getView().findViewById(R.id.timePicker2)).setCurrentHour(minutofinguardado);
        }
        Calendar cal = Calendar.getInstance();
        Log.d("ANYOGUARDADO", String.valueOf(anyoguardado));
        if(anyoguardado == 0){
            anyoguardado = cal.get(Calendar.YEAR);
            Log.d("ANYOGUARDADO", String.valueOf(anyoguardado));
        }
        Log.d("MESGUARDADO", String.valueOf(mesguardado));
        if(mesguardado == 0){
            mesguardado = cal.get(Calendar.MONTH);
            Log.d("MESGUARDADO", String.valueOf(mesguardado));
        }
        Log.d("DIAGUARDADO", String.valueOf(diaguardado));
        if(diaguardado == 0){
            diaguardado = cal.get(Calendar.DAY_OF_MONTH);
            Log.d("DIAGUARDADO", String.valueOf(diaguardado));
        }
        ((DatePicker) getView().findViewById(R.id.datePicker1)).updateDate(anyoguardado, mesguardado, diaguardado);

        ponerColores();
    }

    private void ponerColores(){
        TextView text = (TextView) getView().findViewById(R.id.nombretext);
        text.setTextColor(colorletra);
        text = (TextView) getView().findViewById(R.id.lugartext);
        text.setTextColor(colorletra);
        text = (TextView) getView().findViewById(R.id.horainiciotext);
        text.setTextColor(colorletra);
        text = (TextView) getView().findViewById(R.id.horafintext);
        text.setTextColor(colorletra);
        text = (TextView) getView().findViewById(R.id.fechatext);
        text.setTextColor(colorletra);
        text = (EditText) getView().findViewById(R.id.nombre);
        text.setTextColor(colorletra);
        text = (EditText) getView().findViewById(R.id.lugar);
        text.setTextColor(colorletra);
        this.getView().setBackgroundColor(colorfondo);
    }

    public void onDestroy(){
        super.onDestroy();
        sharedPref.unregisterOnSharedPreferenceChangeListener(prefListener);
    }
}
