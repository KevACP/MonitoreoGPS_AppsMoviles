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

public class ApiStatusActivity extends AppCompatActivity {

    private EditText editTextToken;
    private TextView textViewResponse;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_status);

        editTextToken = findViewById(R.id.editTextToken);
        textViewResponse = findViewById(R.id.textViewResponse);
        Button buttonSendRequest = findViewById(R.id.buttonSendRequest);

        client = new OkHttpClient();

        buttonSendRequest.setOnClickListener(v -> {
            String token = editTextToken.getText().toString();
            sendRequest(token);
        });
    }

    private void sendRequest(String token) {
        String url = "http://localhost:8080/api/device_status";
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