package com.example.device_monitoring;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GpsDataActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_data);

        recyclerView = findViewById(R.id.recyclerViewGpsData);
        dbHelper = new DatabaseHelper(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<DatabaseHelper.SensorData> sensorData = dbHelper.getSensorData(0, System.currentTimeMillis());
        recyclerView.setAdapter(new GpsDataAdapter(sensorData));
    }

    private class GpsDataAdapter extends RecyclerView.Adapter<GpsDataAdapter.ViewHolder> {

        private final List<DatabaseHelper.SensorData> data;

        public GpsDataAdapter(List<DatabaseHelper.SensorData> data) {
            this.data = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_gps_data, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            DatabaseHelper.SensorData item = data.get(position);
            holder.textViewLatitude.setText("Latitud: " + item.latitude);
            holder.textViewLongitude.setText("Longitud: " + item.longitude);
            holder.textViewTimestamp.setText("Timestamp: " + formatTimestamp(item.timestamp));
            holder.textViewDeviceId.setText("Device ID: " + item.deviceId);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        private String formatTimestamp(long timestamp) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textViewLatitude, textViewLongitude, textViewTimestamp, textViewDeviceId;

            ViewHolder(View itemView) {
                super(itemView);
                textViewLatitude = itemView.findViewById(R.id.textViewLatitude);
                textViewLongitude = itemView.findViewById(R.id.textViewLongitude);
                textViewTimestamp = itemView.findViewById(R.id.textViewTimestamp);
                textViewDeviceId = itemView.findViewById(R.id.textViewDeviceId);
            }
        }
    }
};