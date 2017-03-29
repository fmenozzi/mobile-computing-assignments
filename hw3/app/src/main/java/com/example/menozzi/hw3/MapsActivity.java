package com.example.menozzi.hw3;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback,
                   GoogleApiClient.ConnectionCallbacks,
                   GoogleApiClient.OnConnectionFailedListener,
                   LocationListener {

    private class LocationInfo {
        LocationInfo(String label, LatLng coords, int resId) {
            this.label = label;
            this.coords = coords;
            this.resId = resId;

            // TODO: What is this provider string?
            this.location = new Location("bogus");
            location.setLatitude(coords.latitude);
            location.setLongitude(coords.longitude);
        }

        String label;
        LatLng coords;
        Location location;
        int resId;
    }

    private class GeocoderTask extends AsyncTask<LocationInfo, Void, List<String>> {
        private GoogleMap mGoogleMap;

        GeocoderTask(GoogleMap googleMap) {
            mGoogleMap = googleMap;
        }

        @Override
        protected List<String> doInBackground(LocationInfo... params) {
            try {
                Geocoder g = new Geocoder(MapsActivity.this, Locale.getDefault());
                List<String> addresses = new ArrayList<>(3);
                for (LocationInfo locInfo : params) {
                    List<Address> l = g.getFromLocation(locInfo.coords.latitude, locInfo.coords.longitude, 1);
                    addresses.add(l.get(0).getAddressLine(0));
                }
                return addresses;
            } catch (Exception e) {
                Log.e(TAG, "Exception thrown in GeocoderTask.doInBackground()");
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<String> addresses) {
            for (int i = 0; i < mLocationInfos.length; i++) {
                LocationInfo locInfo = mLocationInfos[i];
                locInfo.label = locInfo.label.replace("...", ": ") + addresses.get(i);
            }
            drawMarkersAndCircles(mGoogleMap);
        }
    }

    private static final String TAG = "************";

    private static final int PERMISSION_REQUEST_CODE = 1;

    private static final long DESIRED_UPDATE_INTERVAL_MS = 2000;
    private static final long FASTEST_UPDATE_INTERVAL_MS = DESIRED_UPDATE_INTERVAL_MS/2;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private static final float INITIAL_ZOOM_LEVEL = 17.0f;

    private static final int ZONE_RADIUS_M = 50;
    private static final int STROKE_COLOR  = Color.argb(128, 0, 0, 255);
    private static final int STROKE_WIDTH  = 6;
    private static final int FILL_COLOR    = Color.argb(64, 0, 0, 255);

    private MediaPlayer mMediaPlayer;
    private boolean mMusicPlaying;

    private LocationInfo[] mLocationInfos;

    private static final int BROOKS_BLDG = 0;
    private static final int POLK_PLACE  = 1;
    private static final int OLD_WELL    = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Request permissions
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                            PERMISSION_REQUEST_CODE);

        buildGoogleApiClient();
        buildLocationRequest();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // Exit app if we don't have location permissions
                finish();
            }
        }
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
        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Log.e(TAG, "Somehow failed to get permission before calling onMapReady()");
        }

        // Create location info for each location
        mLocationInfos = new LocationInfo[3];
        mLocationInfos[BROOKS_BLDG] = new LocationInfo("Brooks Building Entrance...",
                                                       new LatLng(35.909562, -79.053026),
                                                       R.raw.cake_by_the_ocean);
        mLocationInfos[POLK_PLACE] = new LocationInfo("Polk Place...",
                                                      new LatLng(35.910571, -79.050381),
                                                      R.raw.waves);
        mLocationInfos[OLD_WELL] = new LocationInfo("Old Well...",
                                                    new LatLng(35.912060, -79.051241),
                                                    R.raw.carolina_in_my_mind);

        drawMarkersAndCircles(googleMap);

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

        // Get addresses via geocoder and re-draw map when done
        new GeocoderTask(googleMap).execute(mLocationInfos[BROOKS_BLDG],
                                            mLocationInfos[POLK_PLACE],
                                            mLocationInfos[OLD_WELL]);
    }

    private void drawMarkersAndCircles(GoogleMap googleMap) {
        googleMap.clear();

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
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connection successful");
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Connection failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            geofence(location);
        } else {
            Log.v(TAG, "Null location in onLocationChanged()");
        }
    }

    private void geofence(Location currentLocation) {
        // Calculate distances to each of the three key locations
        float[] distances = new float[3];
        distances[BROOKS_BLDG] = currentLocation.distanceTo(mLocationInfos[BROOKS_BLDG].location);
        distances[POLK_PLACE] = currentLocation.distanceTo(mLocationInfos[POLK_PLACE].location);
        distances[OLD_WELL] = currentLocation.distanceTo(mLocationInfos[OLD_WELL].location);

        // Determine whether to start or stop music based on "geofence"
        if (distances[BROOKS_BLDG] <= ZONE_RADIUS_M) {
            if (!mMusicPlaying) {
                startPlayer(mLocationInfos[BROOKS_BLDG].resId);
            }
        } else if (distances[POLK_PLACE] <= ZONE_RADIUS_M) {
            if (!mMusicPlaying) {
                startPlayer(mLocationInfos[POLK_PLACE].resId);
            }
        } else if (distances[OLD_WELL] <= ZONE_RADIUS_M) {
            if (!mMusicPlaying) {
                startPlayer(mLocationInfos[OLD_WELL].resId);
            }
        } else if (mMusicPlaying) {
            stopAndRestartPlayer();
        }
    }

    private void startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, MapsActivity.this);
        } catch (SecurityException e) {
            Log.e(TAG, "Somehow failed to get permission before calling startLocationUpdates()");
        }
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private void startPlayer(int resId) {
        mMediaPlayer = MediaPlayer.create(this, resId);
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();

        mMusicPlaying = true;
    }

    private void stopAndRestartPlayer() {
        mMediaPlayer.stop();
        try {
            mMediaPlayer.prepare();
        } catch (IOException e) {
            Log.e(TAG, "Failed to prepare media player");
        }

        mMusicPlaying = false;
    }
}
