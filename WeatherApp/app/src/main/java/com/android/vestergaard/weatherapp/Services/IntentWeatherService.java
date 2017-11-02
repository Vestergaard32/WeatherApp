package com.android.vestergaard.weatherapp.Services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.android.vestergaard.weatherapp.Repositories.SharedPreferenceRepository;

public class IntentWeatherService extends IntentService {
    private OpenWeatherApiService weatherApiService;
    private SharedPreferenceRepository repository;

    public IntentWeatherService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        repository = new SharedPreferenceRepository(getApplicationContext());
        weatherApiService = retrofit.create(OpenWeatherApiService.class);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
