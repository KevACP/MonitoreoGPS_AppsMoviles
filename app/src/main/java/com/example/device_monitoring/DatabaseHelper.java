package com.example.device_monitoring;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DeviceMonitoring.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_SENSOR_DATA = "sensor_data";
    private static final String TABLE_CREDENTIALS = "credentials";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_DEVICE_ID = "device_id";
    private static final String COLUMN_TOKEN = "token";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // Insertar un token de ejemplo solo si no existe
        initializeDefaultToken();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSensorDataTable = "CREATE TABLE " + TABLE_SENSOR_DATA + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LATITUDE + " REAL, " +
                COLUMN_LONGITUDE + " REAL, " +
                COLUMN_TIMESTAMP + " INTEGER, " +
                COLUMN_DEVICE_ID + " TEXT)";
        db.execSQL(createSensorDataTable);

        String createCredentialsTable = "CREATE TABLE " + TABLE_CREDENTIALS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TOKEN + " TEXT)";
        db.execSQL(createCredentialsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSOR_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CREDENTIALS);
        onCreate(db);
    }

    private void initializeDefaultToken() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Verificar si el token ya existe para evitar duplicados
        String query = "SELECT * FROM " + TABLE_CREDENTIALS + " WHERE " + COLUMN_TOKEN + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{"example-token-123"});
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TOKEN, "example-token-123");
            db.insert(TABLE_CREDENTIALS, null, values);
        }
        cursor.close();
    }

    public void insertSensorData(double latitude, double longitude, long timestamp, String deviceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_TIMESTAMP, timestamp);
        values.put(COLUMN_DEVICE_ID, deviceId);
        db.insert(TABLE_SENSOR_DATA, null, values);
    }

    public List<SensorData> getSensorData(long startTime, long endTime) {
        List<SensorData> sensorDataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_TIMESTAMP + " >= ? AND " + COLUMN_TIMESTAMP + " <= ?";
        String[] selectionArgs = {String.valueOf(startTime), String.valueOf(endTime)};
        Cursor cursor = db.query(TABLE_SENSOR_DATA, null, selection, selectionArgs, null, null, COLUMN_TIMESTAMP + " DESC");

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE));
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));
                String deviceId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEVICE_ID));
                sensorDataList.add(new SensorData(id, latitude, longitude, timestamp, deviceId));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return sensorDataList;
    }

    public boolean validateToken(String token) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CREDENTIALS + " WHERE " + COLUMN_TOKEN + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{token});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    public static class SensorData {
        public long id;
        public double latitude;
        public double longitude;
        public long timestamp;
        public String deviceId;

        public SensorData(long id, double latitude, double longitude, long timestamp, String deviceId) {
            this.id = id;
            this.latitude = latitude;
            this.longitude = longitude;
            this.timestamp = timestamp;
            this.deviceId = deviceId;
        }
    }
}