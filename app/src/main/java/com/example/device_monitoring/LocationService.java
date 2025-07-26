package com.example.device_monitoring;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import java.util.UUID;

public class LocationService extends Service {

    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseHelper dbHelper;
    private Handler handler;
    private static final long INTERVAL = 30 * 1000; // 30 segundos
    private String deviceId;

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        dbHelper = new DatabaseHelper(this);
        handler = new Handler(Looper.getMainLooper());
        deviceId = UUID.randomUUID().toString(); // ID único del dispositivo
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startLocationUpdates();
        return START_STICKY;
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(INTERVAL)
                .setFastestInterval(INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        for (Location location : locationResult.getLocations()) {
                            dbHelper.insertSensorData(
                                    location.getLatitude(),
                                    location.getLongitude(),
                                    System.currentTimeMillis(),
                                    deviceId
                            );
                        }
                    }
                }
            }, Looper.getMainLooper());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}