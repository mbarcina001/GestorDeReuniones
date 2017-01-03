package com.example.mikel.gestorreuniones;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Identificarse extends Fragment implements DoHTTPRequest.AsyncResponse{

    private DoHTTPRequest.AsyncResponse mAsyncResponse;
    private Context mContext;
    private OnFragmentInteractionListener mListener;

    private int colorletra;
    private int colorfondo;
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;
    private SharedPreferences sharedPref;

    public Identificarse() {  }

    public static Identificarse newInstance() {
        Identificarse fragment = new Identificarse();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
                }
                if(key.equals("colorletrakey")){
                    colorletra=prefs.getInt("colorletrakey", 0xff000000);
                }
            }
        };
        sharedPref.registerOnSharedPreferenceChangeListener(prefListener);
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        mContext = getView().getContext();
        mAsyncResponse = this;

        Button RegisterButton = (Button) getView().findViewById(R.id.register_button);
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = ((EditText) getView().findViewById(R.id.email)).getText().toString();
                String password = ((EditText) getView().findViewById(R.id.password)).getText().toString();
                String[] datos = {mail, password};

                DoHTTPRequest http = new DoHTTPRequest(mContext, "REGISTRO", R.id.login_progress, datos);
                http.delegate = mAsyncResponse;
                http.execute();
            }
        });

        Button LoginButton = (Button) getView().findViewById(R.id.login_button);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = ((EditText) getView().findViewById(R.id.email)).getText().toString();
                String password = ((EditText) getView().findViewById(R.id.password)).getText().toString();
                String[] datos = {mail, password};

                DoHTTPRequest http = new DoHTTPRequest(mContext, "LOGIN", R.id.login_progress, datos);
                http.delegate = mAsyncResponse;
                http.execute();
                mListener.identificarse(mail);
            }
        });

        Button LogoutButton = (Button) getView().findViewById(R.id.logout_button);
        LogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.correo == null) {
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getView().getContext(), getResources().getString(R.string.not_logged_in), duration);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                } else {
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getView().getContext(), getResources().getString(R.string.succesfull_logout), duration);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                    mListener.identificarse(null);
                }
            }
        });
    }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                // Inflate the layout for this fragment
                return inflater.inflate(R.layout.fragment_identificarse, container, false);
            }

            public void processFinish(int output, String mReqId, String[] datos) {
                if (mReqId.equals("LOGIN")) {
                    if (output > 0) {
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(getView().getContext(), getResources().getString(R.string.correct_login), duration);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();
                    } else {
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(getView().getContext(), getResources().getString(R.string.incorrect_mail_password), duration);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();
                    }
                } else if (mReqId.equals("REGISTER") && (output == 1)) {
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getView().getContext(), getResources().getString(R.string.reunion_added), duration);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
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
                void identificarse(String correo);
            }

            public void onResume() {
                super.onResume();
                ponerColores();
            }

            private void ponerColores() {
                EditText text = (EditText) getView().findViewById(R.id.email);
                text.setTextColor(colorletra);
                text = (EditText) getView().findViewById(R.id.password);
                text.setTextColor(colorletra);
                this.getView().setBackgroundColor(colorfondo);
            }

            public void onSaveInstanceState(Bundle savedInstanceState) {
                savedInstanceState.putInt("ColorLetra", colorletra);
                savedInstanceState.putInt("ColorFondo", colorfondo);
            }

            public void onDestroy() {
                super.onDestroy();
                sharedPref.unregisterOnSharedPreferenceChangeListener(prefListener);
            }
        }
