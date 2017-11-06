package com.android.vestergaard.weatherapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    public boolean mBound;
    public BoundWeatherService mService;
    private final String DATA_READY_NOTIFICATION = "Weather.Data.Ready";
    private final String WEATHER_DATA_INTENT_KEY = "cityWeather";
    private CityWeatherAdapter cityWeatherAdapter;
    ArrayList<CityWeatherData> theCities = new ArrayList();
    public static final String CHANNEL_ID = "weather_app_channel";

    private final int CITY_WEATHER_DETAILS_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 26)
        {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // The id of the channel.
            String id = CHANNEL_ID;
            // The user-visible name of the channel.
            CharSequence name = getString(R.string.AppName);
            // The user-visible description of the channel.
            String description = getString(R.string.NotificationChannelDescription);

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

            // Configure the notification channel.
            mChannel.setDescription(description);

            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.enableLights(true);
            mNotificationManager.createNotificationChannel(mChannel);

        }

        Log.d("Weather", "Binding BoundWeatherService...");
        Intent bindIntent = new Intent(this, BoundWeatherService.class);
        startService(bindIntent);
        bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);

        SetupEventListeners();

        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver, new IntentFilter(DATA_READY_NOTIFICATION));
    }

    private void SetupEventListeners()
    {
        ListView cityWeatherDataListView = (ListView) findViewById(R.id.cityWeatherDataList);
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

        Button btnRefresh = (Button)findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Weather", "Refreshing weather data!");
                if(mBound){
                    mService.ForceRefresh();
                }
            }
        });

        Button btnAdd = (Button) findViewById(R.id.btnAddCity);
        final EditText txtCityName = (EditText) findViewById(R.id.cityNameInput);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBound){
                    mService.AddCity(txtCityName.getText().toString());
                    txtCityName.setText("");
                }
            }
        });
    }

    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            theCities = mService.getAllCitiesWeather();

            cityWeatherAdapter = new CityWeatherAdapter(getApplicationContext(), R.layout.city_weather_list_item,
                    theCities);

            ListView cityWeatherDataListView = (ListView) findViewById(R.id.cityWeatherDataList);
            cityWeatherDataListView.setAdapter(cityWeatherAdapter);
        }
    };

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CITY_WEATHER_DETAILS_REQUEST_CODE)
        {
            if (resultCode == CityWeatherDetailsActivity.REMOVE_RESULT_CODE)
            {
                Gson gson = new Gson();
                CityWeatherData cityData = gson.fromJson(data.getStringExtra(WEATHER_DATA_INTENT_KEY), CityWeatherData.class);
                Log.d("Weather", "User wanted to remove city " + cityData.CityName);
                if(mBound){
                    mService.RemoveCity(cityData.CityName);
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

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
