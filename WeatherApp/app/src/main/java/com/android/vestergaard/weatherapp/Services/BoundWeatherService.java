package com.android.vestergaard.weatherapp.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.android.vestergaard.weatherapp.Models.CityWeatherData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BoundWeatherService extends Service {
    private final IBinder binder = new WeatherServiceBinder();

    private OpenWeatherApiService weatherApiService;

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Weather", "BoundWeatherService OnBind Called!");
        return binder;
    }

    /* Methods For The Components */
    public CityWeatherData getCurrentWeather(String city){
        Log.d("Weather", "Getting current weather data");

        Call<CityWeatherData> bla = weatherApiService.getCityWeatherData("Aarhus");

        try
        {
            bla.enqueue(new Callback<CityWeatherData>() {
                @Override
                public void onResponse(Call<CityWeatherData> call, Response<CityWeatherData> response) {
                    Log.d("Weather", "Weather Data Received!");
                    Log.d("Weather", "City Name: " + response.body().name);
                    Log.d("Weather", "Temperature: " + response.body().main.temp);
                }

                @Override
                public void onFailure(Call<CityWeatherData> call, Throwable t) {
                    Log.d("Weather", "Failed to get weather data");
                }
            });
        } catch (Exception e)
        {
            Log.d("Weather", "Exception: " + e.toString());
            Log.d("Weather", "Exception occurred in BoundWeatherService: " + e.getMessage());
        }

        return null;
    }

    public List<CityWeatherData> getAllCitiesWeather(){
        return null;
    }

    public void AddCity(String cityName){

    }

    public void RemoveCity(String cityName){

    }

    public void ForceRefresh(){

    }

    /* Binder Method */
    public class WeatherServiceBinder extends Binder
    {
        public BoundWeatherService getService(){
            return BoundWeatherService.this;
        }
    }

    /* LifeCycle Methods */
    @Override
    public void onCreate() {
        super.onCreate();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        weatherApiService = retrofit.create(OpenWeatherApiService.class);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
