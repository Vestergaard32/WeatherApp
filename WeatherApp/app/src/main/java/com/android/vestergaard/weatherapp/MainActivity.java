package com.android.vestergaard.weatherapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.vestergaard.weatherapp.Services.BoundWeatherService;

public class MainActivity extends AppCompatActivity {
    public boolean mBound;
    public BoundWeatherService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
