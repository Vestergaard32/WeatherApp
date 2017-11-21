package com.android.vestergaard.weatherapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.vestergaard.weatherapp.Adapters.CityWeatherAdapter;
import com.android.vestergaard.weatherapp.Models.CityWeatherData;
import com.android.vestergaard.weatherapp.Models.WeatherDescription;
import com.android.vestergaard.weatherapp.Services.BoundWeatherService;
import com.google.gson.Gson;

public class CityWeatherDetailsActivity extends AppCompatActivity {
    public static final int OK_RESULT_CODE = 100;
    public static final int REMOVE_RESULT_CODE = 101;
    Intent cityWeatherDetailsIntent;
    CityWeatherData WeatherData;
    public boolean mBound;
    public BoundWeatherService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_weather_details);

        // Bind to WeatherService
        Intent bindIntent = new Intent(this, BoundWeatherService.class);
        startService(bindIntent);
        bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);

        // Get city weather data intent
        cityWeatherDetailsIntent = getIntent();

        // Deserialize city weather data and update UI labels with it
        Gson gson = new Gson();
        CityWeatherData cityWeatherData = gson.fromJson(cityWeatherDetailsIntent.getStringExtra("cityWeather"), CityWeatherData.class);
        WeatherData = cityWeatherData;
        Log.d("Weather", "Timestamp: " + cityWeatherData.RetrievalDate);
        SetCityWeatherData(cityWeatherData);

        SetupEventListeners();

        IntentFilter broadcastFilter = new IntentFilter();
        broadcastFilter.addAction(BoundWeatherService.DATA_READY_BROADCAST);
        broadcastFilter.addAction(BoundWeatherService.CITY_NOT_FOUND_BROADCAST);
        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver, broadcastFilter);
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

    private void SetupEventListeners() {
        // Set up on click event listener for Ok button
        Button btnOk = findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(OK_RESULT_CODE);
                finish();
            }
        });

        // Set up on click event listener for Remove button
        Button btnRemove = findViewById(R.id.btnRemove);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(REMOVE_RESULT_CODE, cityWeatherDetailsIntent);
                finish();
            }
        });
    }

    // Set all UI labels with city weather data selected from previous activity
    private void SetCityWeatherData(CityWeatherData cityWeatherData){
        TextView txtCountry = findViewById(R.id.txtCountry);
        txtCountry.setText(cityWeatherData.metaWeatherData.Country);

        TextView txtViewCityName = findViewById(R.id.txtViewCityName);
        txtViewCityName.setText(cityWeatherData.CityName);

        TextView cityWeatherHumidity = findViewById(R.id.CityWeatherHumidity);
        cityWeatherHumidity.setText(cityWeatherData.WeatherData.Humidity + "%");

        TextView cityWeatherTemperature = findViewById(R.id.CityWeatherTemperature);
        cityWeatherTemperature.setText(cityWeatherData.WeatherData.Temperature + "°C");

        TextView cityWeatherDescription = findViewById(R.id.CityWeatherDescription);

        ImageView imageView = findViewById(R.id.imgViewWeatherIcon);
        String encodedBitmap = cityWeatherData.EncodedWeatherIcon;
        if(encodedBitmap != null){
            try{
                byte[] decodedBytes = Base64.decode(encodedBitmap, Base64.DEFAULT);
                Bitmap bitMap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                imageView.setImageBitmap(bitMap);
            }
            catch (Exception e){
                Log.d("Weather", "Image error");
                Log.d("Weather", e.getMessage());
            }
        }

        if (cityWeatherData.WeatherDescription != null)
        {
            WeatherDescription weatherDescription = cityWeatherData.WeatherDescription.get(0);
            if (weatherDescription != null)
            {
                cityWeatherDescription.setText(weatherDescription.Description);
            }
        } else
        {
            cityWeatherDescription.setText("");
        }

        if(cityWeatherData.RetrievalDate != null){
            TextView timeStampTextView = findViewById(R.id.timstamptxt);
            timeStampTextView.setText(getString(R.string.TimeStampLabel) + cityWeatherData.RetrievalDate);
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
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.d("Weather", "Service Disconnected :(");
            mService = null;
            mBound = false;
        }
    };

    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("Weather", "Broadcast action was: " + intent.getAction());

            // Get the newest weather data for the city
            if (intent.getAction() == BoundWeatherService.DATA_READY_BROADCAST)
            {
                CityWeatherData data = mService.GetWeatherDataForCity(WeatherData.CityName);
                WeatherData = data;
                SetCityWeatherData(WeatherData);
            }
        }
    };
}
