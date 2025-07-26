package com.example.device_monitoring;

import android.app.Service;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.IBinder;
import android.os.Build;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;

import java.io.IOException;
import java.util.List;
import fi.iki.elonen.NanoHTTPD;

public class ApiServer extends Service {

    private HttpServer server;
    private DatabaseHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DatabaseHelper(this);
        server = new HttpServer(8080);
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (server != null) {
            server.stop();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class HttpServer extends NanoHTTPD {

        public HttpServer(int port) {
            super(port);
        }

        @Override
        public Response serve(IHTTPSession session) {
            String uri = session.getUri();
            String authToken = session.getHeaders().get("authorization");

            if (authToken == null || !dbHelper.validateToken(authToken)) {
                return newFixedLengthResponse(Response.Status.UNAUTHORIZED, MIME_PLAINTEXT, "Autenticación fallida");
            }

            if (uri.equals("/api/sensor_data")) {
                String startTimeStr = session.getParameters().get("start_time") != null ?
                        session.getParameters().get("start_time").get(0) : "0";
                String endTimeStr = session.getParameters().get("end_time") != null ?
                        session.getParameters().get("end_time").get(0) : String.valueOf(System.currentTimeMillis());
                long startTime = Long.parseLong(startTimeStr);
                long endTime = Long.parseLong(endTimeStr);
                List<DatabaseHelper.SensorData> data = dbHelper.getSensorData(startTime, endTime);
                StringBuilder response = new StringBuilder("[");
                for (int i = 0; i < data.size(); i++) {
                    DatabaseHelper.SensorData d = data.get(i);
                    response.append(String.format("{\"latitude\":%f,\"longitude\":%f,\"timestamp\":%d,\"device_id\":\"%s\"}",
                            d.latitude, d.longitude, d.timestamp, d.deviceId));
                    if (i < data.size() - 1) response.append(",");
                }
                response.append("]");
                return newFixedLengthResponse(Response.Status.OK, "application/json", response.toString());
            } else if (uri.equals("/api/device_status")) {
                String batteryLevel = getBatteryLevel();
                String networkStatus = getNetworkStatus();
                String storage = getStorageInfo();
                String osVersion = Build.VERSION.RELEASE;
                String deviceModel = Build.MODEL;
                String response = String.format("{\"battery_level\":\"%s\",\"network_status\":\"%s\",\"storage\":\"%s\",\"os_version\":\"%s\",\"device_model\":\"%s\"}",
                        batteryLevel, networkStatus, storage, osVersion, deviceModel);
                return newFixedLengthResponse(Response.Status.OK, "application/json", response);
            } else {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Endpoint no encontrado");
            }
        }

        private String getBatteryLevel() {
            BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
            int batteryLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            return batteryLevel + "%";
        }

        private String getNetworkStatus() {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected() ? "Conectado" : "Desconectado";
        }

        private String getStorageInfo() {
            long freeBytes = Environment.getDataDirectory().getFreeSpace();
            long totalBytes = Environment.getDataDirectory().getTotalSpace();
            return String.format("Disponible: %d MB, Total: %d MB", freeBytes / (1024 * 1024), totalBytes / (1024 * 1024));
        }
    }
}