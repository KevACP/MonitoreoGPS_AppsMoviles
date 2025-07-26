package com.example.device_monitoring;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiSensorActivity extends AppCompatActivity {

    private EditText editTextToken, editTextStartTime, editTextEndTime;
    private TextView textViewResponse;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_sensor);

        editTextToken = findViewById(R.id.editTextToken);
        editTextStartTime = findViewById(R.id.editTextStartTime);
        editTextEndTime = findViewById(R.id.editTextEndTime);
        textViewResponse = findViewById(R.id.textViewResponse);
        Button buttonSendRequest = findViewById(R.id.buttonSendRequest);

        client = new OkHttpClient();

        buttonSendRequest.setOnClickListener(v -> {
            String token = editTextToken.getText().toString();
            String startTime = editTextStartTime.getText().toString();
            String endTime = editTextEndTime.getText().toString();
            sendRequest(token, startTime, endTime);
        });
    }

    private void sendRequest(String token, String startTime, String endTime) {
        String url = "http://localhost:8080/api/sensor_data?start_time=" + startTime + "&end_time=" + endTime;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> textViewResponse.setText("Error: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                runOnUiThread(() -> textViewResponse.setText(responseBody));
            }
        });
    }
}