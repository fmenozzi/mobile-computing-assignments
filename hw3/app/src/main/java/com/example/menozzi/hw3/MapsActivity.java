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
import com.google.android.gms.location.LocationSettingsRequest;
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

    private static final String TAG = "************";

    private static final long DESIRED_UPDATE_INTERVAL_MS = 2000;
    private static final long FASTEST_UPDATE_INTERVAL_MS = DESIRED_UPDATE_INTERVAL_MS/2;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;

    private static final float INITIAL_ZOOM_LEVEL = 17.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        buildGoogleApiClient();
        buildLocationRequest();
        buildLocationSettingsRequest();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void buildLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(DESIRED_UPDATE_INTERVAL_MS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        mLocationSettingsRequest = new LocationSettingsRequest.Builder()
                                    .addLocationRequest(mLocationRequest)
                                    .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
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
        Log.v(TAG, "CONNECTION SUCCESSFUL");

        // TODO: See if you can remove try-catch
        try {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location != null) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                Log.v(TAG, "Location: " + lat + " lat, " + lng + " lng");
            } else {
                Log.v(TAG, "Something went wrong");
            }
        } catch (SecurityException e) {
            Log.e(TAG, "TRY FAILED IN onConnected()");
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v(TAG, "CONNECTION SUSPENDED");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v(TAG, "CONNECTION FAILED");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v(TAG, "LOCATION CHANGED");
    }

    private void startLocationUpdates() {
        Log.v(TAG, "STARTING LOCATION UPDATES");
    }

    private void stopLocationUpdates() {
        Log.v(TAG, "STOPPING LOCATION UPDATES");
    }
}
