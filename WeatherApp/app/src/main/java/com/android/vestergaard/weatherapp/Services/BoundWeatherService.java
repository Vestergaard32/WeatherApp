package com.android.vestergaard.weatherapp.Services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.vestergaard.weatherapp.Models.Cities;
import com.android.vestergaard.weatherapp.Models.CityWeatherData;
import com.android.vestergaard.weatherapp.Repositories.SharedPreferenceRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BoundWeatherService extends Service {
    private final IBinder binder = new WeatherServiceBinder();
    private final String DATA_READY_NOTIFICATION = "Weather.Data.Ready";
    private OpenWeatherApiService weatherApiService;
    private SharedPreferenceRepository repository;

    /* Methods For The Components */
    public void getCurrentWeather(final String city){
        Log.d("Weather", "Getting current weather data");
        Call<CityWeatherData> bla = weatherApiService.getCityWeatherData(city);
        try
        {
            repository.SaveCityWeatherData(city, bla.execute().body());
        } catch (Exception e)
        {
            Log.d("Weather", "Exception: " + e.toString());
            Log.d("Weather", "Exception occurred in BoundWeatherService: " + e.getMessage());
        }
    }

    public List<CityWeatherData> getAllCitiesWeather(){
        Cities cities = repository.GetCities();
        ArrayList<CityWeatherData> data = new ArrayList<>();
        for (String city:cities.Cities) {
            CityWeatherData weatherData = repository.GetCityWeatherData(city);
            data.add(weatherData);
        }
        return data;
    }

    public void AddCity(String cityName){
        repository.SaveCity(cityName);
        ForceRefresh();
    }

    public void RemoveCity(String cityName){
        repository.RemoveCity(cityName);
    }

    public void ForceRefresh(){
        AsyncTask<String, String, String> asyncTask = new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String[] strings) {
                Cities cities = repository.GetCities();
                for (String city:cities.Cities) {
                    getCurrentWeather(city);
                    Log.d("TaskStuff", city + " has been refreshed");
                }
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                Intent broadcastIntent = new Intent(DATA_READY_NOTIFICATION);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
            }
        };
        asyncTask.execute("yolo");
    }

    /* Binder Method */
    public class WeatherServiceBinder extends Binder
    {
        public BoundWeatherService getService(){
            return BoundWeatherService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Weather", "BoundWeatherService OnBind Called!");
        return binder;
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
        repository = new SharedPreferenceRepository(getApplicationContext());
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
