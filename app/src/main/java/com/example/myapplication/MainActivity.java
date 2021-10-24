package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity{

    TextView tvLatitude, tvLongitude;
    FusedLocationProviderClient fusedLocationProviderClient;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button1 = (Button) findViewById(R.id.button);
        Button button2 = (Button) findViewById(R.id.button2);
//        button1.setOnClickListener(this);
//        button2.setOnClickListener(this);
        button2 = findViewById(R.id.button2);
        tvLatitude = findViewById(R.id.tv_latitude);
        tvLongitude = findViewById(R.id.tv_longitude);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    getCurrentLocation();
                }else{
                    //When permission isn ot granted
                    //Request pemission
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},100);
                }
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openMainActivity2();
            }
        });

    }

    @SuppressLint("MissingSuperCall")
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 100 && grantResults.length>0 && (grantResults[0] + grantResults[1])==PackageManager.PERMISSION_GRANTED){
            //When Permission granted
            //Call method
            getCurrentLocation();

        }else{
          //  When permission are denied
          //Display Toast
            Toast.makeText(getApplicationContext(),"Permission Denied.", Toast.LENGTH_SHORT).show();
        }

    }

    public void openMainActivity2(){
//         Intent intent = new Intent(this, MainActivity2.class);
                Intent sendIntent = new Intent(); sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                sendIntent.setPackage("com.whatsapp");
//                startActivity(intent);
    }


    @SuppressLint("MissingPermission")
    private String getCurrentLocation(){
        LocationManager locationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE
        );
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    //Initialize Location
                    Location location = task.getResult();
                    //Check Location
                    if(location!=null){
                        //When Location result is not null
                        //Set Latitude
                        tvLatitude.setText(String.valueOf(location.getLatitude()));
                        //Set Longitude
                        tvLongitude.setText(String.valueOf(location.getLongitude()));


                    }else{
                        //When Location result is null
                        //Initial Location request
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);
                        //Initialize location call back
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                //Initialize location
                                Location location1 = locationResult.getLastLocation();
                                //Set Latitude
                                tvLatitude.setText(String.valueOf(location1.getLatitude()));
                                //Set longitude
                                tvLongitude.setText(String.valueOf(location1.getLongitude()));

                            }
                        };
                        //Request Location updates
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });
        }else{
            //When Location service is not enabled
            //Open Location setting
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

        return null;
    }

}




