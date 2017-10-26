package com.android.vestergaard.weatherapp.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.vestergaard.weatherapp.Models.CityWeatherData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.google.gson.Gson;

/**
 * Created by David on 26/10/2017.
 */

public class BoundWeatherService extends Service {
    private final IBinder binder = new WeatherServiceBinder();

    private OpenWeatherApiService weatherApiService;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /* Methods For The Components */
    public CityWeatherData getCurrentWeather(String city){
        CityWeatherData bla = weatherApiService.getCityWeatherData("Aarhus");
        Log.d("Bound Weather Service", bla.name);
        return bla;
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
        BoundWeatherService getService(){
            return BoundWeatherService.this;
        }
    }

    /* LifeCycle Methods */
    @Override
    public void onCreate() {
        super.onCreate();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("api.openweathermap.org/data/2.5/")
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
