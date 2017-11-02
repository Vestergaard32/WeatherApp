package com.android.vestergaard.weatherapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.vestergaard.weatherapp.Models.CityWeatherAdapter;
import com.android.vestergaard.weatherapp.Models.CityWeatherData;
import com.android.vestergaard.weatherapp.Models.WeatherData;
import com.android.vestergaard.weatherapp.Repositories.SharedPreferenceRepository;
import com.android.vestergaard.weatherapp.Services.BoundWeatherService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public boolean mBound;
    public BoundWeatherService mService;

    /*
        Java has HashMap as a dictionary, implemented by the Map interface
     */
    private Map<String, CityWeatherData> cityWeatherData;
    private CityWeatherAdapter cityWeatherAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CityWeatherData cityWeatherData1 = new CityWeatherData();
        cityWeatherData1.WeatherData = new WeatherData();

        cityWeatherData1.CityName = "Aarhus";
        cityWeatherData1.WeatherData.Temperature = 25.28;
        cityWeatherData1.WeatherData.Humidity = 13;

        CityWeatherData cityWeatherData2 = new CityWeatherData();
        cityWeatherData2.WeatherData = new WeatherData();

        cityWeatherData2.CityName = "Copenhagen";
        cityWeatherData2.WeatherData.Temperature = 35.25;
        cityWeatherData2.WeatherData.Humidity = 22;

        cityWeatherData = new HashMap<String, CityWeatherData>();
        cityWeatherData.put("key1", cityWeatherData1);
        cityWeatherData.put("key2", cityWeatherData2);


        Collection<CityWeatherData> cityWeatherDataSet = cityWeatherData.values();
        ArrayList<CityWeatherData> theCities = new ArrayList<CityWeatherData>(cityWeatherDataSet);

        cityWeatherAdapter = new CityWeatherAdapter(this, R.layout.city_weather_list_item,
                theCities);

        ListView cityWeatherDataListView = (ListView) findViewById(R.id.cityWeatherDataList);
        cityWeatherDataListView.setAdapter(cityWeatherAdapter);

        SharedPreferenceRepository rep = new SharedPreferenceRepository(this);
        rep.SaveCityWeatherData(cityWeatherData1);

        CityWeatherData brandNewCityWeatherData = rep.GetCityWeatherData(cityWeatherData1.CityName);

        Log.d("Weather", "SAVED WEATHER CITY: " + brandNewCityWeatherData.CityName);

        cityWeatherDataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent cityWeatherDetailsIntent = new Intent(MainActivity.this, CityWeatherDetailsActivity.class);
                CityWeatherData selectedCityWeatherData = (CityWeatherData)adapterView.getItemAtPosition(i);

                Gson gson = new Gson();
                cityWeatherDetailsIntent.putExtra("cityWeather", gson.toJson(selectedCityWeatherData));

                startActivity(cityWeatherDetailsIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("Weather", "Binding BoundWeatherService...");
        Intent bindIntent = new Intent(this, BoundWeatherService.class);
        bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBound) {
            Log.d("Weather", "Unbinding service...");
            unbindService(mConnection);
            mBound = false;
        }
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d("Weather", "Service is connected! :D");

            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BoundWeatherService.WeatherServiceBinder binder = (BoundWeatherService.WeatherServiceBinder) service;
            mService = binder.getService();
            mBound = true;

            mService.getCurrentWeather("Aarhus");
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.d("Weather", "Service Disconnected :(");
            mBound = false;
        }
    };
}
