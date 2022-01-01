package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MainActivity extends AppCompatActivity{

    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSIONS_FINE_LOCATION = 90;
    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address, new_waypoint, tv_labelCrumbCounter ;
    Switch sw_locationsupdates, sw_gps;
    Button button_newway_point, button_showway_point_list, btn_showmap, btn_speedometer, btn_drowsiness;
    boolean updateOn = false;

    Location currentLocation;

    List<Location> savedLocations;


    LocationCallback locationCallBack;

    public FusedLocationProviderClient fusedLocationProviderClient;
    private Location location;
    String tracked = "location is tracking";
    String not_tracked = "location is not tracking";
    LocationRequest locationRequest = LocationRequest.create();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);
        sw_locationsupdates = findViewById(R.id.sw_locationsupdates);
        sw_gps = findViewById(R.id.sw_gps);
        button_showway_point_list = findViewById(R.id.show_way_point);
        button_newway_point = findViewById(R.id.button);
        new_waypoint = findViewById(R.id.tvcountOfCrumbs);
        tv_labelCrumbCounter = findViewById(R.id.tv_labelCrumbCounter);
        btn_showmap = findViewById(R.id.btn_showmap);
        btn_speedometer = findViewById(R.id.speedometer);
        btn_drowsiness = findViewById(R.id.drowsiness);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        LocationRequest locationRequest = LocationRequest.create();


        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);

        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location location = locationResult.getLastLocation();
                upadateUIValues(location);
            }
        };


        button_newway_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Myapplication myapplication = (Myapplication)getApplicationContext();
                savedLocations = myapplication.getMylocations();
                savedLocations.add(currentLocation);
            }
        });

        button_showway_point_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ShowSavedLocationList.class);
                startActivity(i);
            }
        });

        btn_showmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,MapsActivity.class);
                startActivity(i);
            }
        });

        btn_speedometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,MapsActivity.class);
                startActivity(i);
            }
        });

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_gps.isChecked()) {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("using GPS sensors");

                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("using towers + WIFI");
                }
            }
        });

        sw_locationsupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_locationsupdates.isChecked()) {
                    startLocationUpdates();


                } else {
                    stopLocationUpdates();
                }
            }
        });


        updateGPS();

    } // end of oncreate method




    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        tv_updates.setText(tracked);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        updateGPS();
    }

    @SuppressLint("MissingPermission")
    private void stopLocationUpdates() {
        tv_updates.setText(not_tracked);
        tv_speed.setText(not_tracked);
        tv_accuracy.setText(not_tracked);
        tv_lon.setText(not_tracked);
        tv_lat.setText(not_tracked);
        tv_address.setText(not_tracked);
        tv_sensor.setText(not_tracked);
        tv_altitude.setText(not_tracked);


        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSIONS_FINE_LOCATION:
            if(grantResults [0]==PackageManager.PERMISSION_GRANTED) {
                updateGPS();

            }
            else {
                Toast.makeText(this, "This app requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show();
                finish();
            }

    }
}

    private void updateGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    upadateUIValues(location);
                    currentLocation= location;

                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }




    }

    private void upadateUIValues(Location location) {
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));

        if(location.hasAltitude()){
            tv_altitude.setText(String.valueOf(location.getAltitude()));

        }else{
            tv_altitude.setText("not available");
        }

        if(location.hasSpeed()){
           tv_speed.setText(String.valueOf(location.getSpeed()));




        }else{
            tv_speed.setText("not available");
        }
        Geocoder geocoder = new Geocoder(MainActivity.this);

        try{
           List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            tv_address.setText(addresses.get(0).getAddressLine(0));

        }catch (Exception e){
            tv_address.setText("unable to get stereet address");

        }

        Myapplication myapplication = (Myapplication)getApplicationContext();
        savedLocations = myapplication.getMylocations();

        new_waypoint.setText(Integer.toString(savedLocations.size()));

    }


}