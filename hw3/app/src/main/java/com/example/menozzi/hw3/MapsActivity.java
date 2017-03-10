package com.example.menozzi.hw3;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private static final float INITIAL_ZOOM_LEVEL = 17.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Create three markers
        LatLng brooksBldg = new LatLng(35.909562, -79.053026);
        LatLng polkPlace = new LatLng(35.910571, -79.050381);
        LatLng oldWell = new LatLng(35.912060, -79.051241);

        // Add markers to map
        mMap.addMarker(new MarkerOptions().position(brooksBldg).title("Brooks Building Entrance"));
        mMap.addMarker(new MarkerOptions().position(polkPlace).title("Polk Place"));
        mMap.addMarker(new MarkerOptions().position(oldWell).title("Old Well"));

        // Center camera over Brooks Building and zoom in
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(brooksBldg, INITIAL_ZOOM_LEVEL));
    }
}
