package com.example.mikel.gestorreuniones;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Notas extends AppCompatActivity{

    private static final String TAG = "Notas";

    private int colorletra;
    private int colorfondo;
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;
    private SharedPreferences sharedPref;

    //TTS
    private TextToSpeech myTTS;

    //ASR
    private static final int REQUEST_CODE = 777;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Se añade la toolbar
        setContentView(R.layout.activity_notas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        //Se añade el botón Up a la toolbar.
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        //Se añade el listener de preferencias
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener(){
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key){
                if(key.equals("colorfondokey")){
                    colorfondo=prefs.getInt("colorfondokey", 0xfff3f3f3);
                    Log.d("Consultar-ColorFondo", String.valueOf(colorfondo));
                }
                if(key.equals("colorletrakey")){
                    colorletra=prefs.getInt("colorletrakey", 0xff000000);
                    Log.d("Consultar-ColorLetra", String.valueOf(colorletra));
                }
                View v = findViewById(R.id.layoutnotas);
                EditText text = (EditText) findViewById(R.id.notas);
                text.setTextColor(colorletra);
                v.setBackgroundColor(colorfondo);
            }
        };
        sharedPref.registerOnSharedPreferenceChangeListener(prefListener);

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            String[] PERMISSIONS_STORAGE = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            int REQUEST_EXTERNAL_STORAGE = 1;
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        Log.d(TAG, "=> LECTURA DE UN FICHERO (EXTERNO) EN MEMORIA EXTERNA:");
        InputStream fich3 = null;

        String texto = "";

        try {
            File path = Environment.getExternalStorageDirectory();
            File f = new File(path.getAbsolutePath(), "notas.txt");
            fich3 = new FileInputStream(f);
            BufferedReader buff = new BufferedReader(new InputStreamReader(fich3));
            String line;
            while ((line = buff.readLine()) != null) {
                Log.d(TAG, line);
                texto = texto + line;
                texto = texto + "\n";
            }
            ((EditText) findViewById(R.id.notas)).setText(texto);
            fich3.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "=> CREACIÓN DEL DICCIONARIO PERSONALIZADO");
            try {
                File path = Environment.getExternalStorageDirectory();
                File f = new File(path.getAbsolutePath(), "notas.txt");
                OutputStreamWriter fich_2 = new OutputStreamWriter(new FileOutputStream(f));
                fich_2.close();
            } catch (FileNotFoundException e2) {
                e2.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (savedInstanceState == null) {
            Intent i = getIntent();
            this.colorletra = i.getIntExtra("colorletra", 0);
            this.colorfondo = i.getIntExtra("colorfondo", 0);
        }else{
            this.colorletra = savedInstanceState.getInt("ColorLetra");
            this.colorfondo = savedInstanceState.getInt("ColorFondo");
        }
        View v = findViewById(R.id.layoutnotas);
        EditText text = (EditText) findViewById(R.id.notas);
        text.setTextColor(colorletra);
        v.setBackgroundColor(colorfondo);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("ColorLetra", colorletra);
        savedInstanceState.putInt("ColorFondo", colorfondo);
    }

    public void onResume(){
        super.onResume();
        myTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    //Por ahora solo hablará en español
                    myTTS.setLanguage(new Locale("spa", "ESP"));
                }
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notas, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.read_content:
                String toSpeak = ((EditText) findViewById(R.id.notas)).getText().toString();
                myTTS.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.write_content:
                PackageManager pm = getPackageManager();
                List<ResolveInfo> activities = pm.queryIntentActivities(
                        new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
                if (activities.size() == 0){
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_voice_recogniser), Toast.LENGTH_SHORT).show();
                }else{
                    startVoiceRecognitionActivity();
                }
                break;
            case R.id.action_settings:
                //En caso de pulsar la opción Preferencias, se crea un intent que llama a la actiidad Preferencias
                startActivity(new Intent(getApplicationContext(), Preferencias.class));
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return true;
    }

    public void onPause() {
        super.onPause();
        Log.d(TAG, "=> TRANSP. 16: ESCRITURA DE UN FICHERO (EXTERNO) EN MEMORIA EXTERNA:");
        OutputStreamWriter fich_2;
        try {
            File path = Environment.getExternalStorageDirectory();
            File f = new File(path.getAbsolutePath(), "notas.txt");
            fich_2 = new OutputStreamWriter(new FileOutputStream(f));

            try {
                String texto = ((EditText) findViewById(R.id.notas)).getText().toString();
                fich_2.write(texto);
            } catch (Exception e) {
                e.printStackTrace();
            }

            fich_2.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onStop() {
        super.onStop();
        if(myTTS !=null){
            myTTS.stop();
            myTTS.shutdown();
        }
    }

    private void startVoiceRecognitionActivity(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                getClass().getPackage().getName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getResources().getString(R.string.say_a_word));
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            // Populate the wordsList with the String values the recognition engine thought it heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            if(matches.size()>0) {
                ((EditText) findViewById(R.id.notas)).append(matches.get(0).toString());
            }else{
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_voice_recognised), Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
