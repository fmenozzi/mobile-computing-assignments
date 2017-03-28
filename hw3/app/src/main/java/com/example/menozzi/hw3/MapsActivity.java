package com.example.menozzi.hw3;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback,
                   GoogleApiClient.ConnectionCallbacks,
                   GoogleApiClient.OnConnectionFailedListener,
                   LocationListener {

    private class LocationInfo {
        public LocationInfo(String label, LatLng coords, int resId) {
            this.label = label;
            this.coords = coords;
            this.resId = resId;
        }

        public String label;
        public LatLng coords;
        public int resId;
    }

    private static final String TAG = "************";

    private static final long DESIRED_UPDATE_INTERVAL_MS = 2000;
    private static final long FASTEST_UPDATE_INTERVAL_MS = DESIRED_UPDATE_INTERVAL_MS/2;

    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;

    private static final float INITIAL_ZOOM_LEVEL = 17.0f;

    private static final int ZONE_RADIUS_M = 50;
    private static final int STROKE_COLOR  = Color.argb(128, 0, 0, 255);
    private static final int STROKE_WIDTH  = 6;
    private static final int FILL_COLOR    = Color.argb(64, 0, 0, 255);

    private MediaPlayer mMediaPlayer;

    private LocationInfo[] mLocationInfos;

    private static final int BROOKS_BLDG = 0;
    private static final int POLK_PLACE  = 1;
    private static final int OLD_WELL    = 2;

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
        // Draw current location with blue dot
        // TODO: Get rid of try-catch
        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Log.e(TAG, "TRY FAILED IN onMapReady()");
        }

        // Create location info for each location
        mLocationInfos = new LocationInfo[3];
        mLocationInfos[BROOKS_BLDG] = new LocationInfo("Brooks Building Entrance",
                                                       new LatLng(35.909562, -79.053026),
                                                       R.raw.derezzed_glitch_mob);
        mLocationInfos[POLK_PLACE] = new LocationInfo("Polk Place",
                                                      new LatLng(35.910571, -79.050381),
                                                      R.raw.hark_the_sound);
        mLocationInfos[OLD_WELL] = new LocationInfo("Old Well",
                                                    new LatLng(35.912060, -79.051241),
                                                    R.raw.carolina_in_my_mind);

        // Add markers to map
        googleMap.addMarker(new MarkerOptions().position(mLocationInfos[BROOKS_BLDG].coords)
                                               .title(mLocationInfos[BROOKS_BLDG].label));
        googleMap.addMarker(new MarkerOptions().position(mLocationInfos[POLK_PLACE].coords)
                                               .title(mLocationInfos[POLK_PLACE].label));
        googleMap.addMarker(new MarkerOptions().position(mLocationInfos[OLD_WELL].coords)
                                               .title(mLocationInfos[OLD_WELL].label));

        // Draw circles around markers to specify music zones
        CircleOptions opts = new CircleOptions()
                                .radius(ZONE_RADIUS_M)
                                .strokeColor(STROKE_COLOR)
                                .strokeWidth(STROKE_WIDTH)
                                .fillColor(FILL_COLOR);
        googleMap.addCircle(opts.center(mLocationInfos[BROOKS_BLDG].coords));
        googleMap.addCircle(opts.center(mLocationInfos[POLK_PLACE].coords));
        googleMap.addCircle(opts.center(mLocationInfos[OLD_WELL].coords));

        // Calculate triangle centroid
        double minLat = Double.POSITIVE_INFINITY, maxLat = Double.NEGATIVE_INFINITY;
        double minLng = Double.POSITIVE_INFINITY, maxLng = Double.NEGATIVE_INFINITY;
        for (LocationInfo locInfo : mLocationInfos) {
            LatLng point = locInfo.coords;

            minLat = Math.min(minLat, point.latitude);
            maxLat = Math.max(maxLat, point.latitude);
            minLng = Math.min(minLng, point.longitude);
            maxLng = Math.max(maxLng, point.longitude);
        }
        LatLng centroid = new LatLng(minLat + (maxLat-minLat)/2, minLng + (maxLng-minLng)/2);

        // Center camera over centroid and zoom in
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centroid, INITIAL_ZOOM_LEVEL));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v(TAG, "CONNECTION SUCCESSFUL");
        startLocationUpdates();
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
        if (location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            Log.v(TAG, "LOCATION CHANGED: " + lat + ", " + lng);

            geofence(location);

        } else {
            Log.v(TAG, "NULL LOCATION IN onLocationChanged()");
        }
    }

    private void geofence(Location currentLocation) {
        // Calculate distances to each of the three key locations
    }

    private void startLocationUpdates() {
        Log.v(TAG, "STARTING LOCATION UPDATES");
        LocationServices.SettingsApi.checkLocationSettings(
                mGoogleApiClient,
                mLocationSettingsRequest
        ).setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        try {
                            LocationServices.FusedLocationApi.requestLocationUpdates(
                                    mGoogleApiClient, mLocationRequest, MapsActivity.this);
                        } catch (SecurityException e) {
                            Log.e(TAG, "TRY FAILED IN startLocationUpdates()");
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                "location settings ");
                        try {
                            // Show dialog and check result in onActivityResult()
                            status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        String errorMessage = "Fix location settings in Settings";
                        Log.e(TAG, errorMessage);
                        Toast.makeText(MapsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void stopLocationUpdates() {
        Log.v(TAG, "STOPPING LOCATION UPDATES");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    private void startPlayer(int resId) {
        mMediaPlayer = MediaPlayer.create(this, resId);
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.start();
    }

    private void stopAndRestartPlayer() {
        mMediaPlayer.stop();
        try {
            mMediaPlayer.prepare();
        } catch (IOException e) {
            Log.e(TAG, "FAILED TO PREPARE MEDIA PLAYER");
        }
    }
}
