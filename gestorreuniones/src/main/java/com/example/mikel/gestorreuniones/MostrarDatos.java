package com.example.mikel.gestorreuniones;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MostrarDatos extends Fragment implements DoHTTPRequest.AsyncResponse{
    private String correo;

    private DoHTTPRequest.AsyncResponse mAsyncResponse;
    private Context mContext;

    public MostrarDatos() {  }

    public static MostrarDatos newInstance(String pCorreo) {
        MostrarDatos fragment = new MostrarDatos();
        Bundle args = new Bundle();
        args.putString("CORREO", pCorreo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            correo = getArguments().getString("MAIL");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mostrar_datos, container, false);
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        mContext = getView().getContext();
        mAsyncResponse = this;

        Log.d("MOSTRARDATOS", correo);
        String[] datos = {correo};

        Log.d("MainActivity", String.valueOf(R.id.login_progress));

        DoHTTPRequest http = new DoHTTPRequest(mContext, "GET_DATA", R.id.login_progress, datos);
        http.delegate = mAsyncResponse;
        http.execute();

        Button UpdateButton = (Button) getView().findViewById(R.id.updatedata);
        UpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] datos = {correo, ((EditText)getView().findViewById(R.id.nombre)).getText().toString(),
                        ((EditText)getView().findViewById(R.id.apellidos)).getText().toString(),
                        ((EditText)getView().findViewById(R.id.edad)).getText().toString(),
                        ((EditText)getView().findViewById(R.id.direccion)).getText().toString(),};
                DoHTTPRequest http = new DoHTTPRequest(mContext, "SET_DATA", R.id.login_progress, datos);
                http.delegate = mAsyncResponse;
                http.execute();
            }
        });
    }

    public void processFinish(int output, String mReqId, String[] datos) {
        if(mReqId.equals("GET_DATA")) {
            if(datos[0]!=null){
                ((EditText)getView().findViewById(R.id.nombre)).setText(datos[0]);
            }
            if(datos[1]!=null){
                ((EditText)getView().findViewById(R.id.apellidos)).setText(datos[1]);
            }
            if(datos[2]!=null){
                ((EditText)getView().findViewById(R.id.edad)).setText(datos[2]);
            }
            if(datos[3]!=null){
                ((EditText)getView().findViewById(R.id.direccion)).setText(datos[3]);
            }
        }else if(mReqId.equals("SET_DATA") && (output==1)){
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getView().getContext(), "Datos actualizados correctamente", duration);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        }
    }
}
