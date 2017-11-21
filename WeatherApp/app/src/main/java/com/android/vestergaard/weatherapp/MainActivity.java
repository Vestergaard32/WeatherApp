package com.android.vestergaard.weatherapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.vestergaard.weatherapp.Adapters.CityWeatherAdapter;
import com.android.vestergaard.weatherapp.Models.CityWeatherData;
import com.android.vestergaard.weatherapp.Services.BoundWeatherService;
import com.google.gson.Gson;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public boolean mBound;
    public BoundWeatherService mService;
    private final String WEATHER_DATA_INTENT_KEY = "cityWeather";
    private CityWeatherAdapter cityWeatherAdapter;
    ArrayList<CityWeatherData> theCities = new ArrayList();

    private final int CITY_WEATHER_DETAILS_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("Weather", "Binding BoundWeatherService...");
        Intent bindIntent = new Intent(this, BoundWeatherService.class);
        startService(bindIntent);
        bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);

        SetupEventListeners();

        IntentFilter broadcastFilter = new IntentFilter();
        broadcastFilter.addAction(BoundWeatherService.DATA_READY_BROADCAST);
        broadcastFilter.addAction(BoundWeatherService.CITY_NOT_FOUND_BROADCAST);

        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver, broadcastFilter);
    }

    private void SetupEventListeners(){
        // Set up on item click event listener for the list view
        ListView cityWeatherDataListView = findViewById(R.id.cityWeatherDataList);
        cityWeatherDataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent cityWeatherDetailsIntent = new Intent(MainActivity.this, CityWeatherDetailsActivity.class);
                CityWeatherData selectedCityWeatherData = (CityWeatherData)adapterView.getItemAtPosition(i);

                Gson gson = new Gson();
                cityWeatherDetailsIntent.putExtra(WEATHER_DATA_INTENT_KEY, gson.toJson(selectedCityWeatherData));

                startActivityForResult(cityWeatherDetailsIntent, CITY_WEATHER_DETAILS_REQUEST_CODE);
            }
        });

        // Set up click event listener for refresh weather data button
        Button btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Weather", "Refreshing weather data!");
                if(mBound){
                    mService.ForceRefresh();
                }
            }
        });

        // Set up click event listener for add city button
        Button btnAdd = findViewById(R.id.btnAddCity);
        final EditText txtCityName = findViewById(R.id.cityNameInput);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBound && !txtCityName.getText().toString().equals("")){
                    mService.AddCity(txtCityName.getText().toString());
                    txtCityName.setText("");
                }
            }
        });
    }

    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("Weather", "Broadcast action was: " + intent.getAction());

            if (intent.getAction() == BoundWeatherService.DATA_READY_BROADCAST)
            {
                theCities = mService.getAllCitiesWeather();

                cityWeatherAdapter = new CityWeatherAdapter(getApplicationContext(), R.layout.city_weather_list_item,
                        theCities);

                ListView cityWeatherDataListView = findViewById(R.id.cityWeatherDataList);
                cityWeatherDataListView.setAdapter(cityWeatherAdapter);
            } else if (intent.getAction() == BoundWeatherService.CITY_NOT_FOUND_BROADCAST)
            {
                String cityName = intent.getStringExtra("CityName");

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                                                .setCancelable(false)
                                                .setTitle(getString(R.string.CityNotFoundTitle, cityName))
                                                .setMessage(getString(R.string.CityNotFoundMessage, cityName))
                                                .setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                    }
                                                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            Log.d("Weather", "Unbinding service...");
            unbindService(mConnection);
            mBound = false;
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dataReceiver);
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

            // Populate city list view by creating a new adapter with fetched cities
            theCities = mService.getAllCitiesWeather();
            cityWeatherAdapter = new CityWeatherAdapter(getApplicationContext(), R.layout.city_weather_list_item,
                    theCities);

            ListView cityWeatherDataListView = (ListView) findViewById(R.id.cityWeatherDataList);
            cityWeatherDataListView.setAdapter(cityWeatherAdapter);
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.d("Weather", "Service Disconnected :(");
            mBound = false;
        }
    };
}
