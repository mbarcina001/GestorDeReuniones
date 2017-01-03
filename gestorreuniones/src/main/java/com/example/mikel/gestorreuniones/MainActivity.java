package com.example.mikel.gestorreuniones;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AnadirReunionFragment.OnFragmentInteractionListener,
        Consultar.OnFragmentInteractionListener,
        Eliminar.OnFragmentInteractionListener,
        DoHTTPRequest.AsyncResponse,
        Identificarse.OnFragmentInteractionListener{

    private Fragment fragment1;
    private Fragment fragment2;
    private SQLiteDatabase db;
    private double latitud;
    private double longitud;
    private LocationManager mManager;
    private String mejorproveedor;
    private int colorfondo;
    private int colorletra;
    private String clase;
    private SharedPreferences sharedPref;
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    private Context mContext;
    public static String correo;

    //GCM
    private final String SENDER_ID = "1031179396527";
    private GoogleCloudMessaging gcm;
    private DoHTTPRequest.AsyncResponse mAsyncResponse;
    private String regId;
    private static Context staticcontext;

    public static Context getAppContext() {
        return MainActivity.staticcontext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.staticcontext = getApplicationContext();

        //Se pone el layout y la appbar personalizada
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Se crea el DrawerLayout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer.openDrawer(GravityCompat.START);


        //Se crea el Gestor de base de datos y se obtiene la BBDD
        GestorDB manejador = new GestorDB(this, "Reunion", null, 1);
        db = manejador.getWritableDatabase();


        //Obtención de coordenadas
        //Se asegura que tenemos permisos
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            //Si no hay permisos, se avisa al usuario
            String[] PERMISSIONS_STORAGE = {
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
            int ACCESS_FINE_LOCATION = 1;
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    ACCESS_FINE_LOCATION
            );
        }

        mManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //Se establece el criterio de elección
        Criteria mCriteria = new Criteria();
        mCriteria.setPowerRequirement(1);
        mejorproveedor = mManager.getBestProvider(mCriteria, true);

        //Se obtiene la última localización conocida
        boolean activado = mManager.isProviderEnabled(mejorproveedor);
        if(activado) {
            Location pos = mManager.getLastKnownLocation(mejorproveedor);
            if(pos!=null) {
                latitud = pos.getLatitude();
                longitud = pos.getLongitude();
            }else{
                mManager.requestLocationUpdates(mejorproveedor, 1000, 0, new LocationListener() {

                    public void onLocationChanged(Location location) {}

                    public void onProviderDisabled(String provider) {}

                    public void onProviderEnabled(String provider) {}

                    public void onStatusChanged(String provider, int status,
                                                Bundle extras) {}
                });
            }
        }

        //Creación del objeto SharedPreferences para obtener las preferencias del usuario
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //Se registra el Listener de las Preferencias
        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener(){
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key){
            if(key.equals("colorfondokey")){
                colorfondo=prefs.getInt("colorfondokey", 0xfff3f3f3);
                View v = findViewById(R.id.main_layout);
                v.setBackgroundColor(colorfondo);
            }else{
                colorletra=prefs.getInt("colorletrakey", 0xff000000);
            }
            }
        };
        sharedPref.registerOnSharedPreferenceChangeListener(prefListener);

        //Se crean los fragmentos que se van a mostrar.
        colorfondo = sharedPref.getInt("colorfondokey", 0xfff3f3f3);
        colorletra = sharedPref.getInt("colorletrakey", 0xff000000);
        Class fragmentClass = Identificarse.class;
        Bundle args;

        //Creación del primer fragment
        if(savedInstanceState!=null){
            //Si hay un fragment guardado, se recupera
            fragment1=getSupportFragmentManager().getFragment(savedInstanceState, "fragment");
            correo = savedInstanceState.getString("Correo");
        }else {
            //En caso contrario se crea uno nuevo
            try {
                fragment1 = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            args = new Bundle();
            args.putInt("color", colorfondo);
            args.putInt("colorletra", colorletra);
            fragment1.setArguments(args);
        }
        //Se coloca el fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment1, "FragmentActual").commit();

        //Creación del segundo fragment
        if(fragment2==null) {
            fragmentClass = Consultar.class;
            try {
                fragment2 = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            args = new Bundle();
            args.putInt("color", colorfondo);
            args.putInt("colorletra",colorletra);
            fragment2.setArguments(args);
        }
        //Se coloca el segundo fragment
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container1, fragment2).commit();

        //Se asegura que el segundo fragment solo se muestra en horizontal
        int orientation = this.getResources().getConfiguration().orientation;
        if(orientation==1){
            //Vertical
            FrameLayout layout = (FrameLayout)findViewById(R.id.fragment_container1);
            layout.setVisibility(View.GONE);
        }else if(orientation==2){
            //horizontal
            FrameLayout layout = (FrameLayout)findViewById(R.id.fragment_container1);
            layout.setVisibility(View.VISIBLE);
        }

        //Se cambiar el color de fondo de la actividad por el elegido por el usuario.
        View v = findViewById(R.id.main_layout);
        v.setBackgroundColor(colorfondo);

        //NUEVO: GCM
        //Se inicia el AsyncResponse y el Context
        mAsyncResponse = this;
        mContext = getApplicationContext();

        //Comprobar Play Services en el dispositivo
        if (checkPlayServices()) {
            regId = getRegistrationId(mContext);
            if (regId.isEmpty()) {
                registerInBackground();
            }
        }
    }

    //Comprobar Play Services en el dispositivo también en el onResume
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    //NUEVO: GCM

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("MainActivity", "This device is not supported");
                finish();
            }
            return false;
        }
        return true;
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(mContext);
                    }
                    regId = gcm.register(SENDER_ID);
                    msg = "Dispositivo registrado, id=" + regId;

                    //Guardar en bd en galan
                    sendRegistrationIdToBackend();

                    //Guardar el id en preferencias
                    storeRegistrationId(mContext, regId);
                } catch (IOException ex) {
                    msg = "Error: " + ex.getMessage();
                }
                return msg;
            }

            protected void onPostExecute(String msg) {
            }
        }.execute(null, null, null);
    }

    private String getRegistrationId(Context pContext) {
        SharedPreferences prefs =
                getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);

        return prefs.getString("id", "");
    }

    private void sendRegistrationIdToBackend() {
        String[] datos = {regId};
        DoHTTPRequest http = new DoHTTPRequest(mContext, "REGISTRO_GCM", -1, datos);
        http.delegate = mAsyncResponse;
        http.execute();
    }

    private void storeRegistrationId(Context pContext, String pRegId){
        SharedPreferences prefs =
                getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("id", pRegId);
        editor.apply();
    }

    public void processFinish(int output, String mReqId, String[] datos) {
        Toast toast1;
        switch(mReqId){
            case "REGISTRO_GCM":
                if(output==1){
                    toast1 = Toast.makeText(getApplicationContext(),
                            "Registrado correctamente", Toast.LENGTH_SHORT);
                }else{
                    toast1 = Toast.makeText(getApplicationContext(),
                            "Ha ocurrido un error", Toast.LENGTH_SHORT);
                }
                toast1.show();
        }
    }

    //------------------------------------------------------------------

    @Override
    public void onBackPressed() {
        //Sobreescribe el método que maneja el botón Back
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            //Si el navigation drawer está abierto lo cierra
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //En caso contrario, se efectua el método con normalidad
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla el menu y añade items a la appbar
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //Se llama cuando se selecciona un item del Navigation Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Class fragmentClass;

        //En función del item seleccionado, la clase del fragmento a crear será una u otra
        switch(item.getItemId()) {
            case R.id.nav_anadir:
                fragmentClass = AnadirReunionFragment.class;
                clase="Anadir";
                break;
            case R.id.nav_consultar:
                fragmentClass = Consultar.class;
                clase="Consultar";
                break;
            case R.id.nav_eliminar:
                fragmentClass = Eliminar.class;
                clase="Eliminar";
                break;
            default:
                fragmentClass = AnadirReunionFragment.class;
                clase="Anadir";
        }

        //Se instancia el nuevo fragmento
        try {
            fragment1 = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Se añaden a los argumentos los colores de letra y fondo
        Bundle args = new Bundle();
        args.putInt("color", colorfondo);
        args.putInt("colorletra", colorletra);
        fragment1.setArguments(args);

        //Se reemplaza el fragmento actual por el nuevo
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment1, "FragmentActual").commit();

        //Se añade el título y se cierra el Navigation Drawer
        setTitle(item.getTitle());
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Se llama cuando se selecciona un item de la appbar
        Intent intent;
        Class fragmentClass;
        Bundle args;
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (item.getItemId()){
            case R.id.showdata:
                //NUEVO: En caso de pulsar el botón de mostrar datos
                //Se muestran los datos para el usuario si este está identificado
                //También se permite cambiarlos
                if(correo!=null) {
                    fragmentClass = MostrarDatos.class;
                    clase = "MostrarDatos";

                    //Se instancia el nuevo fragmento
                    try {
                        fragment1 = (Fragment) fragmentClass.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    args = new Bundle();
                    args.putInt("color", colorfondo);
                    args.putInt("colorletra", colorletra);
                    args.putString("MAIL", correo);
                    fragment1.setArguments(args);
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment1, "FragmentActual").commit();
                    setTitle(item.getTitle());
                }else{
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(this, getResources().getString(R.string.not_registered), duration);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
                break;
            case R.id.identify:
                //NUEVO: En caso de pulsar el botón de identificarse
                //Se muestra el fragment para identificarse
                fragmentClass = Identificarse.class;
                clase="Identificarse";

                //Se instancia el nuevo fragmento
                try {
                    fragment1 = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                args = new Bundle();
                args.putInt("color", colorfondo);
                args.putInt("colorletra", colorletra);
                fragment1.setArguments(args);
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment1, "FragmentActual").commit();
                setTitle(item.getTitle());
                break;
            case R.id.show_coords:
                //En caso de pulsar mostrar coordenadas,
                //Se asegura que tenemos permisos
                int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    //Si no hay permisos, se avisa al usuario
                    String[] PERMISSIONS_STORAGE = {
                            Manifest.permission.ACCESS_FINE_LOCATION
                    };
                    int ACCESS_FINE_LOCATION = 1;
                    ActivityCompat.requestPermissions(
                            this,
                            PERMISSIONS_STORAGE,
                            ACCESS_FINE_LOCATION
                    );
                }

                boolean activado = mManager.isProviderEnabled(mejorproveedor);
                if(activado) {
                    //Si es proveedor está activo
                    //Se obtiene la última localización conocida
                    Location pos = mManager.getLastKnownLocation(mejorproveedor);
                    if(pos!=null) {
                        latitud = pos.getLatitude();
                        longitud = pos.getLongitude();
                    }else{
                        mManager.requestLocationUpdates(mejorproveedor, 1000, 0, new LocationListener() {

                            public void onLocationChanged(Location location) {}

                            public void onProviderDisabled(String provider) {}

                            public void onProviderEnabled(String provider) {}

                            public void onStatusChanged(String provider, int status,
                                                        Bundle extras) {}
                        });
                    }

                    //Se crea un Dialog personalizado que muestra las coordenadas y los botones de Ok y Mostrar mapa
                    final Dialog dialog = new Dialog(this);
                    dialog.setContentView(R.layout.fragment_dialog_coordenadas);
                    dialog.setTitle(getResources().getString(R.string.coordenadas));

                    //Se añaden las coordenadas al TextView
                    TextView text = (TextView) dialog.findViewById(R.id.text);
                    String coords = getResources().getString(R.string.coordenadas) + ":\n"
                            + getResources().getString(R.string.latitud) + ": " + latitud + "\n"
                            + getResources().getString(R.string.longitud) + ": " + longitud;
                    text.setText(coords);

                    //Se añade el botón de Ok
                    Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Al pulsar Ok se cierra el dialog
                            dialog.dismiss();
                        }
                    });

                    //Se añade el botón de Mostrar Mapa
                    Button dialogButton2 = (Button) dialog.findViewById(R.id.dialogButtonMap);
                    dialogButton2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Al pulsar Mostrar Mapa se lanza la activity MapActivity que muestra la posición en un mapa
                            dialog.dismiss();
                            Intent intent;
                            intent = new Intent(MainActivity.this, MapActivity.class);
                            intent.putExtra("latitud", latitud);
                            intent.putExtra("longitud", longitud);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }else{
                    //Si el proveedor no está activo, se lanza un intent para que el usuario lo active
                    Intent i= new Intent (android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(i);
                }
                break;
            case R.id.show_task_list:
                //En caso de pulsar la opcíon mostrar lista de notas, se crea un intent que llama a la actividad Notas
                intent = new Intent(MainActivity.this, Notas.class);
                intent.putExtra("colorfondo",colorfondo);
                intent.putExtra("colorletra", colorletra);
                startActivity(intent);
                break;
            case R.id.action_settings:
                //En caso de pulsar la opción Preferencias, se crea un intent que llama a la actiidad Preferencias
                startActivity(new Intent(getApplicationContext(), Preferencias.class));
                break;
        }
        return true;
    }

    public void anadirReunion(String nombre, int horainicio, int minutoinicio, int horafin,
                                     int minutofin, int dia, int mes, int anyo, String fecha,
                                        String lugar, boolean avisar) {
        anadirReunionStatic(nombre, horainicio, minutoinicio, horafin, minutofin, dia, mes,
                anyo, fecha, lugar, db, mContext, avisar);

        //Si la orientación es horizontal, se actualiza la lista de reuniones porque se está mostrando al usuario
        int orientation = this.getResources().getConfiguration().orientation;
        if(orientation==2){
            //horizontal
            Class fragmentClass = Consultar.class;
            try {
                fragment2 = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container1, fragment2).commit();
        }

        //Se muestra un Toast al usuario para confirmar que se ha añadido la reunión
        Context context = this;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, getResources().getString(R.string.reunion_added), duration);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    public static void anadirReunionStatic(String nombre, int horainicio, int minutoinicio, int horafin,
                              int minutofin, int dia, int mes, int anyo, String fecha, String lugar,
                              SQLiteDatabase db, Context mContext, boolean avisar) {
        //Método para añadir las reuniones
        //Es llamado desde AnadirReunionFragment
        AlarmManager alarmManager1 = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);

        Cursor c = db.rawQuery("SELECT COUNT(*) FROM Reunion WHERE nombre='"+nombre+"' AND horainicio="+horainicio+
                " AND minutoinicio="+minutoinicio+" AND horafin="+horafin+" AND minutofin="+minutofin+
                " AND dia="+dia+" AND mes="+mes+" AND anyo="+anyo+" AND lugar='"+lugar+"';", null);
        c.moveToNext();

        Log.d("nombre", nombre);
        Log.d("horainicio", String.valueOf(horainicio));
        Log.d("minutoinicio", String.valueOf(minutoinicio));
        Log.d("horafin", String.valueOf(horafin));
        Log.d("minutofin", String.valueOf(minutofin));
        Log.d("dia", String.valueOf(dia));
        Log.d("mes", String.valueOf(mes));
        Log.d("anyo", String.valueOf(anyo));
        Log.d("lugar", lugar);

        int cuenta = c.getInt(0);
        if(cuenta>0){
            if(avisar){
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(mContext, mContext.getResources().getString(R.string.repeated_reunion), duration);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }
        }else {
            String s = "Insert Into Reunion('nombre', 'horainicio', 'minutoinicio', 'horafin', 'minutofin'," +
                    " 'dia', 'mes', 'anyo', 'fecha', 'lugar') VALUES ('" + nombre + "', " + horainicio + ", " + minutoinicio + ", "
                    + horafin + ", " + minutofin + ", " + dia + ", " + mes + ", " + anyo + ", '" + fecha + "', '" + lugar + "');";
            db.execSQL(s);
            Log.d("ANADIR", s);
            //-----------------------------------------------------
            //Se calcula el momento en el que tiene que sonar la alarma (30 min antes)
            //Se resta uno al mes porque el timepicker devuelve mes+1
            if (horainicio > 0) {
                if (minutoinicio >= 30) {
                    minutoinicio -= 30;
                } else {
                    horainicio -= 1;
                    minutoinicio = 60 - 30 + minutoinicio;
                }
            } else {
                if (minutoinicio >= 30) {
                    minutoinicio -= 30;
                } else {
                    horainicio = 23;
                    minutoinicio = 60 - 30 + minutoinicio;
                    if (dia != 1) {
                        dia -= 1;
                    } else {
                        if (mes == 0) {
                            mes = 11;
                            anyo -= 1;
                        } else {
                            mes -= 1;
                            if ((mes == 2) || (mes == 4) || (mes == 8) || (mes == 9) || (mes == 11)) {
                                dia = 31;
                            } else if ((mes == 5) || (mes == 7) || (mes == 10) || (mes == 12)) {
                                dia = 30;
                            } else {
                                if (((anyo % 4 == 0) && (anyo % 100 != 0) || (anyo % 400 == 0))) {
                                    dia = 29;
                                } else {
                                    dia = 28;
                                }
                            }
                        }
                    }
                }
            }
            //-----------------------------------------------------

            //Se setea el calendario
            Calendar cal = Calendar.getInstance();
            cal.clear();
            cal.set(Calendar.DAY_OF_MONTH, dia);
            cal.set(Calendar.MONTH, mes);
            cal.set(Calendar.YEAR, anyo);
            cal.set(Calendar.HOUR_OF_DAY, horainicio);
            cal.set(Calendar.MINUTE, minutoinicio);
            cal.set(Calendar.SECOND, 0);

            //Se crea un AlarmManager para que lance un broadcast 30 minutos antes de una reunión.
            Intent myIntent1 = new Intent(mContext, Receiver.class);
            myIntent1.setAction("miavisopersonalizado");
            myIntent1.putExtra("NombreReunion", nombre);
            myIntent1.putExtra("HoraInicio", horainicio);
            myIntent1.putExtra("MinutoInicio", minutoinicio);
            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(mContext, 0, myIntent1, 0);
            alarmManager1.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent1);
        }
    }

    public void eliminar(Integer pId){
        //Se elimina una reunión
        //Es llamado desde el fragment Eliminar
        String s = "DELETE FROM Reunion WHERE Codigo="+pId+";";
        db.execSQL(s);

        //Si la orientación es horizontal, se actualiza la lista de reuniones porque se está mostrando al usuario
        int orientation = this.getResources().getConfiguration().orientation;
        if(orientation==2){
            //horizontal
            Class fragmentClass = Consultar.class;
            try {
                fragment2 = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container1, fragment2).commit();
        }
    }

    public void consultar(int pId){
        //Recibe el id de una Reunión y realiza una consulta a la BBDD para mostrarla en FragmentReunion
        Cursor c = db.rawQuery("Select * FROM Reunion Where codigo='"+pId+"';", null);
        Bundle bundle = new Bundle();

        //Se obtienen los datos a mostrar de la reunión
        c.moveToNext();
        bundle.putString("nombre", c.getString(1));
        bundle.putInt("horainicio", c.getInt(2));
        bundle.putInt("minutoinicio", c.getInt(3));
        bundle.putInt("horafin", c.getInt(4));
        bundle.putInt("minutofin", c.getInt(5));
        bundle.putInt("dia", c.getInt(6));
        bundle.putInt("mes", c.getInt(7));
        bundle.putInt("anyo", c.getInt(8));
        bundle.putString("lugar", c.getString(10));

        Log.d("nombre", c.getString(1));
        Log.d("horainicio", String.valueOf(c.getInt(2)));
        Log.d("minutoinicio", String.valueOf(c.getInt(3)));
        Log.d("horafin", String.valueOf(c.getInt(4)));
        Log.d("minutofin", String.valueOf(c.getInt(5)));
        Log.d("dia", String.valueOf(c.getInt(6)));
        Log.d("mes", String.valueOf(c.getInt(7)));
        Log.d("anyo", String.valueOf(c.getInt(8)));
        Log.d("lugar", c.getString(10));

        c.close();

        //Se instancia un nuevo FragmentReunion
        clase="Reunion";
        Class fragmentClass = FragmentReunion.class;
        try {
            fragment1 = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Se añaden el color de fondo y el de letra a los argumentos
        bundle.putInt("color", colorfondo);
        bundle.putInt("colorletra",colorletra);
        fragment1.setArguments(bundle);

        //Se reemplaza el actual Fragment por el FragmentReunion
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment1, "FragmentActual").commit();

        setTitle("Reunion");
    }

    public void identificarse(String pMail){
        correo = pMail;
    }

    protected void onSaveInstanceState(Bundle savedInstanceState){
        //Se guarda el estado de los fragmentos
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("Correo", correo);
        savedInstanceState.putString("clase", clase);
        getSupportFragmentManager().putFragment(savedInstanceState, "fragment", fragment1);
    }

    public void onDestroy(){
        //Se elimina el listener
        super.onDestroy();
        sharedPref.unregisterOnSharedPreferenceChangeListener(prefListener);
    }
}
