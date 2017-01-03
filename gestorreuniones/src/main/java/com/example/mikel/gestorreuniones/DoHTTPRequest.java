package com.example.mikel.gestorreuniones;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

public class DoHTTPRequest extends AsyncTask<String, Void, String> {

    public AsyncResponse delegate=null;

    // Aldagai orokorrak:
    private static final String TAG = "DoHTTPRequest";

    private Context mContext;
    private String mReqId;
    private String param = "";
    private ProgressBar mProgressBar = null;
    private int mProgressBarId;
    private HttpURLConnection urlConnection = null;
    private String errorMessage = "";

    // Constructor:
    public DoHTTPRequest(Context context, String reqId, int progressBarId, String [] datos) {

        mContext = context;
        mReqId = reqId;
        mProgressBarId = progressBarId;
        errorMessage = "";

        Log.d(TAG, mReqId);

        // Datos, en formato para enviar
        switch(mReqId) {
            case "LOGIN":
            case "REGISTRO":
                try {
                    param = "Mail=" + URLEncoder.encode(datos[0], "UTF-8");
                    param += "&Password=" + URLEncoder.encode(datos[1], "UTF-8");
                    Log.d(TAG, param);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case "REGISTRO_GCM":
                try {
                    param = "ID=" + URLEncoder.encode(datos[0], "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case "GET_DATA":
                try {
                    param = "mail=" + URLEncoder.encode(datos[0], "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case "SET_DATA":
                try {
                    param = "mail=" + URLEncoder.encode(datos[0], "UTF-8");
                    param += "&nombre=" + URLEncoder.encode(datos[1], "UTF-8");
                    param += "&apellidos=" + URLEncoder.encode(datos[2], "UTF-8");
                    param += "&edad=" + URLEncoder.encode(datos[3], "UTF-8");
                    param += "&ubicacion=" + URLEncoder.encode(datos[4], "UTF-8");
                    Log.d(TAG, param);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (mProgressBarId != -1) {
            mProgressBar = (ProgressBar) ((Activity) mContext).findViewById(mProgressBarId);
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String st = "";
        switch(mReqId) {
            case "LOGIN": st = "http://galan.ehu.eus/mbarcina001/WEB/login.php"; break;
            case "REGISTRO": st = "http://galan.ehu.eus/mbarcina001/WEB/registro.php"; break;
            case "REGISTRO_GCM": st = "http://galan.ehu.eus/mbarcina001/WEB/registrogcm.php"; break;
            case "GET_DATA": st = "http://galan.ehu.eus/mbarcina001/WEB/getdata.php"; break;
            case "SET_DATA": st = "http://galan.ehu.eus/mbarcina001/WEB/setdata.php"; break;
            default: break;
        }

        // Begiratu internetera konektatu daitekeen:
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (!(netInfo != null && netInfo.isConnected())) {
            errorMessage = "No Internet Connection";
            return errorMessage;
        }

        // Ajax eskaera sortu:
        String targetURLstr = st;
        Log.d("DoHTTPRequest", targetURLstr);
        InputStream inputStream;
        try {
            // java.net.URL objektu bat sortu:
            URL targetURL = new URL(targetURLstr);
            urlConnection = (HttpURLConnection) targetURL.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Accept-Language", Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry());
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setDoOutput(true);

            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(param);
            out.close();

            int statusCode = urlConnection.getResponseCode();
            Log.d(TAG, String.valueOf(statusCode));

            /* 200 represents HTTP OK */
            if (statusCode == 200) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line;
                String result = "";
                while((line = bufferedReader.readLine()) != null){
                    result += line;
                }
                // Stream-a itxi:
                inputStream.close();
                String response = result;
                Log.d(TAG, result);
                return response;
            }
            else{
                errorMessage = "Error connecting to server";
                urlConnection.disconnect();
                return errorMessage;
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Error connecting to Internet";
            return errorMessage;
        } finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }
    }

    @Override
    protected void onPostExecute(final String result) {

        Log.d(TAG, result);
        switch(mReqId) {
            case "LOGIN":
                try {
                    JSONParser parser = new JSONParser();
                    JSONArray array = (JSONArray) parser.parse(result);
                    int count = 0;

                    for (Object element : array) {
                        JSONObject slide = (JSONObject) element;
                        count = Integer.parseInt((String) slide.get("COUNT(*)"));
                        Log.d("COUNT", String.valueOf(count));
                    }
                    delegate.processFinish(count, mReqId, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "REGISTRO":
                if (result.equals("Registrado correctamente")){
                    delegate.processFinish(1, "REGISTRO", null);
                }else{
                    delegate.processFinish(0, "REGISTRO", null);
                }
                break;
            case "REGISTRO_GCM":
                if (result.equals("Registrado correctamente")){
                    delegate.processFinish(1, "REGISTRO_GCM", null);
                }else{
                    delegate.processFinish(0, "REGISTRO_GCM", null);
                }
                break;
            case "GET_DATA":
                try {
                    JSONParser parser = new JSONParser();
                    JSONArray array = (JSONArray) parser.parse(result);

                    for (Object element : array) {
                        JSONObject slide = (JSONObject) element;
                        String[] datos = {String.valueOf(slide.get("nombre")), String.valueOf(slide.get("apellidos")),
                                String.valueOf(slide.get("edad")), String.valueOf(slide.get("ubicacion"))};
                        delegate.processFinish(1, "GET_DATA", datos);
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }
                break;
            case "SET_DATA":
                if (result.equals("Registrado correctamente")){
                    delegate.processFinish(1, "SET_DATA", null);
                }else{
                    delegate.processFinish(0, "SET_DATA", null);
                }
                break;
        }

        // Progreso-barra desagerrarazi:
        if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
    }

    @Override
    protected void onCancelled() {
        // Progreso-barra desagerrarazi:
        if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
    }

    // Interfaze metodoa:
    public interface AsyncResponse {
        void processFinish(int output, String mReqId, String[] datos);
    }

}
