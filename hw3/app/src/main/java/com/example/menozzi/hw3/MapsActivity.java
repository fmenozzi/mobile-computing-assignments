package com.example.menozzi.hw3;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback,
                   GoogleApiClient.ConnectionCallbacks,
                   GoogleApiClient.OnConnectionFailedListener,
                   LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mClient;
    private LocationRequest mReq;

    private static final float INITIAL_ZOOM_LEVEL = 17.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Build Google API client
        if (mClient == null) {
            mClient = new GoogleApiClient.Builder(this)
                                         .addConnectionCallbacks(this)
                                         .addOnConnectionFailedListener(this)
                                         .addApi(LocationServices.API)
                                         .build();
        }

        // Build LocationRequest object
        if (mReq == null) {
            mReq = new LocationRequest();
            mReq.setInterval(10000);
            mReq.setFastestInterval(5000);
            mReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    @Override
    protected void onStart() {
        mClient.connect();
        if (mMap != null) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mClient, mReq, this);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        mClient.disconnect();
        LocationServices.FusedLocationApi.removeLocationUpdates(mClient, this);
        super.onStop();
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v("************", "CONNECTION SUCCESSFUL");
        try {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mClient);
            if (location != null) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                Log.v("CONNECTED: ", "Location: " + lat + " lat, " + lng + " lng");
            } else {
                Log.v("NOT CONNECTED", "Something went wrong");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("************", "CONNECTION SUSPENDED");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("************", "CONNECTION FAILED");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v("************", "LOCATION CHANGED");
        if (location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            Log.v("***LOCATION CHANGED***", "Loc: " + lat + " lat, " + lng + " lng");
        } else {
            Log.v("***LOCATION CHANGED***", "NULL LOCATION");
        }
    }
}
