package io.nlopez.smartlocation.sample;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.Geofence;

import java.util.ArrayList;
import java.util.List;

import io.nlopez.smartlocation.LocationZ;
import io.nlopez.smartlocation.location.geofencing.utils.TransitionGeofence;
import io.nlopez.smartlocation.location.listener.OnActivityUpdatedListener;
import io.nlopez.smartlocation.location.listener.OnGeofencingTransitionListener;
import io.nlopez.smartlocation.location.listener.OnLocationUpdatedListener;
import io.nlopez.smartlocation.location.listener.OnReverseGeocodingListener;
import io.nlopez.smartlocation.location.provider.LocationGooglePlayServicesListener;
import io.nlopez.smartlocation.location.util.GeofenceModel;

public class MainActivity extends Activity implements OnLocationUpdatedListener, OnActivityUpdatedListener, OnGeofencingTransitionListener {

    private TextView locationText;
    private TextView activityText;
    private TextView geofenceText;

    private LocationGooglePlayServicesListener provider;

    private static final int LOCATION_PERMISSION_ID = 1001;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        // Bind event clicks
        Button startLocation = (Button) findViewById(R.id.start_location);
        startLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Location permission not granted
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
                    return;
                }
                startLocation();
            }
        });

        Button stopLocation = (Button) findViewById(R.id.stop_location);
        stopLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopLocation();
            }
        });

        // bind textviews
        locationText = (TextView) findViewById(R.id.location_text);
        activityText = (TextView) findViewById(R.id.activity_text);
        geofenceText = (TextView) findViewById(R.id.geofence_text);

        // Keep the screen always on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        showLast();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocation();
        }
    }

    private void showLast() {
        Location lastLocation = LocationZ.with(this).location().getLastLocation();
        if (lastLocation != null) {
            locationText.setText(
                    String.format("[From Cache] Latitude %.6f, Longitude %.6f",
                            lastLocation.getLatitude(),
                            lastLocation.getLongitude())
            );
        }

        DetectedActivity detectedActivity = LocationZ.with(this).activity().getLastActivity();
        if (detectedActivity != null) {
            activityText.setText(
                    String.format("[From Cache] Activity %s with %d%% confidence",
                            getNameFromType(detectedActivity),
                            detectedActivity.getConfidence())
            );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (provider != null) {
            provider.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startLocation() {

        provider = new LocationGooglePlayServicesListener();
        provider.setCheckLocationSettings(true);

        LocationZ locationZ = new LocationZ.Builder(this).logging(true).build();

        locationZ.location(provider).start(this);
        locationZ.activity().start(this);

        // Create some geofences
        GeofenceModel mestalla = new GeofenceModel.Builder("1").setTransition(Geofence.GEOFENCE_TRANSITION_ENTER).setLatitude(39.47453120000001).setLongitude(-0.358065799999963).setRadius(500).build();
        locationZ.geofencing().add(mestalla).start(this);
    }

    private void stopLocation() {
        LocationZ.with(this).location().stop();
        locationText.setText("Location stopped!");

        LocationZ.with(this).activity().stop();
        activityText.setText("Activity Recognition stopped!");

        LocationZ.with(this).geofencing().stop();
        geofenceText.setText("Geofencing stopped!");
    }

    private void showLocation(Location location) {
        if (location != null) {
            final String text = String.format("Latitude %.6f, Longitude %.6f",
                    location.getLatitude(),
                    location.getLongitude());
            locationText.setText(text);

            // We are going to get the address for the current position
            LocationZ.with(this).geocoding().reverse(location, new OnReverseGeocodingListener() {
                @Override
                public void onAddressResolved(Location original, List<Address> results) {
                    if (results.size() > 0) {
                        Address result = results.get(0);
                        StringBuilder builder = new StringBuilder(text);
                        builder.append("\n[Reverse Geocoding] ");
                        List<String> addressElements = new ArrayList<>();
                        for (int i = 0; i <= result.getMaxAddressLineIndex(); i++) {
                            addressElements.add(result.getAddressLine(i));
                        }
                        builder.append(TextUtils.join(", ", addressElements));
                        locationText.setText(builder.toString());
                    }
                }
            });
        } else {
            locationText.setText("Null location");
        }
    }

    private void showActivity(DetectedActivity detectedActivity) {
        if (detectedActivity != null) {
            activityText.setText(
                    String.format("Activity %s with %d%% confidence",
                            getNameFromType(detectedActivity),
                            detectedActivity.getConfidence())
            );
        } else {
            activityText.setText("Null activity");
        }
    }

    private void showGeofence(Geofence geofence, int transitionType) {
        if (geofence != null) {
            geofenceText.setText("Transition " + getTransitionNameFromType(transitionType) + " for Geofence with id = " + geofence.getRequestId());
        } else {
            geofenceText.setText("Null geofence");
        }
    }

    @Override
    public void onLocationUpdated(Location location) {
        showLocation(location);
    }

    @Override
    public void onActivityUpdated(DetectedActivity detectedActivity) {
        showActivity(detectedActivity);
    }

    @Override
    public void onGeofenceTransition(TransitionGeofence geofence) {
        showGeofence(geofence.getGeofenceModel().toGeofence(), geofence.getTransitionType());
    }

    private String getNameFromType(DetectedActivity activityType) {
        switch (activityType.getType()) {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.TILTING:
                return "tilting";
            default:
                return "unknown";
        }
    }

    private String getTransitionNameFromType(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "enter";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "exit";
            default:
                return "dwell";
        }
    }
}
