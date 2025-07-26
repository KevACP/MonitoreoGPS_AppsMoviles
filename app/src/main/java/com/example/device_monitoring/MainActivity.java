package com.example.device_monitoring;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private TextView statusTextView;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = findViewById(R.id.statusTextView);
        Button buttonViewGpsData = findViewById(R.id.buttonViewGpsData);
        Button buttonTestSensorApi = findViewById(R.id.buttonTestSensorApi);
        Button buttonTestStatusApi = findViewById(R.id.buttonTestStatusApi);

        // Configurar clics en los botones
        buttonViewGpsData.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GpsDataActivity.class);
            startActivity(intent);
        });

        buttonTestSensorApi.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ApiSensorActivity.class);
            startActivity(intent);
        });

        buttonTestStatusApi.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ApiStatusActivity.class);
            startActivity(intent);
        });

        // Verificar y solicitar permisos de ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startServices();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startServices();
            } else {
                statusTextView.setText("Permiso de ubicación denegado.");
            }
        }
    }

    private void startServices() {
        // Iniciar el servicio de recolección de datos
        Intent locationServiceIntent = new Intent(this, LocationService.class);
        startService(locationServiceIntent);

        // Iniciar el servidor HTTP
        Intent apiServerIntent = new Intent(this, ApiServer.class);
        startService(apiServerIntent);

        statusTextView.setText("Servicios iniciados: recolección de datos y servidor HTTP.");
    }
}