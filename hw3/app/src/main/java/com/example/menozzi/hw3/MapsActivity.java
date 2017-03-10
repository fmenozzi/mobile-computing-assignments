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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Create three markers
        LatLng[] points = new LatLng[] {
                new LatLng(35.909562, -79.053026),
                new LatLng(35.910571, -79.050381),
                new LatLng(35.912060, -79.051241),
        };

        // Add markers to map
        mMap.addMarker(new MarkerOptions().position(points[0]).title("Brooks Building Entrance"));
        mMap.addMarker(new MarkerOptions().position(points[1]).title("Polk Place"));
        mMap.addMarker(new MarkerOptions().position(points[2]).title("Old Well"));

        // Calculate triangle centroid
        double minLat = Double.POSITIVE_INFINITY, maxLat = Double.NEGATIVE_INFINITY;
        double minLng = Double.POSITIVE_INFINITY, maxLng = Double.NEGATIVE_INFINITY;
        for (LatLng point : points) {
            minLat = Math.min(minLat, point.latitude);
            maxLat = Math.max(maxLat, point.latitude);
            minLng = Math.min(minLng, point.longitude);
            maxLng = Math.max(maxLng, point.longitude);
        }
        LatLng centroid = new LatLng(minLat + (maxLat-minLat)/2, minLng + (maxLng-minLng)/2);

        // Center camera over centroid and zoom in
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centroid, INITIAL_ZOOM_LEVEL));
    }
}
