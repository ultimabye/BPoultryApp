package com.ultimabyte.bpoultry;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://YOUR_API_ENDPOINT";

    private Spinner dataList;
    private String userId;
    private String accessToken;

    private AuthApiService authApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataList = findViewById(R.id.spinnerOptions);

        // Retrieve user information from the login activity
        userId = getIntent().getStringExtra("userId");
        accessToken = getIntent().getStringExtra("accessToken");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        authApiService = retrofit.create(AuthApiService.class);

        // Call method to fetch data
        fetchData();
    }

    private void fetchData() {
        Call<DataResponse> call = authApiService.getData("Bearer " + accessToken, userId);
        call.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                if (response.isSuccessful()) {
                    DataResponse dataResponse = response.body();
                    if (dataResponse != null) {
                        String data = dataResponse.getData();
                        // Update UI with the fetched data

                    }
                } else {
                    Toast.makeText(MainActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Data fetch error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
