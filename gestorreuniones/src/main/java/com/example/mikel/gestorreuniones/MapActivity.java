package com.example.mikel.gestorreuniones;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private double latitud;
    private double longitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        latitud = intent.getDoubleExtra("latitud", 0.0);
        longitud = intent.getDoubleExtra("longitud", 0.0);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng loc = new LatLng(latitud, longitud);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 13));

        map.addMarker(new MarkerOptions()
                .title(getResources().getString(R.string.locactual))
                .position(loc));
    }
}
