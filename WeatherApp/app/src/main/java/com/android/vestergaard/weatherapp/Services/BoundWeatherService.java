package com.android.vestergaard.weatherapp.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;

import com.android.vestergaard.weatherapp.MainActivity;
import com.android.vestergaard.weatherapp.Models.Cities;
import com.android.vestergaard.weatherapp.Models.CityWeatherData;
import com.android.vestergaard.weatherapp.R;
import com.android.vestergaard.weatherapp.Repositories.SharedPreferenceRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BoundWeatherService extends Service {
    private final IBinder binder = new WeatherServiceBinder();
    private final String DATA_READY_BROADCAST = "Weather.Data.Ready";
    private OpenWeatherApiService weatherApiService;
    private SharedPreferenceRepository repository;
    private final Handler handler = new Handler();;
    private Timer Timer;
    private boolean isStarted = false;

    /* Methods For The Components */
    private void getCurrentWeather(final String city){
        Log.d("Weather", "Getting current weather data");
        Call<CityWeatherData> cityWeatherDataApiCall = weatherApiService.getCityWeatherData(city);
        try
        {
            // Call weather API synchroniously and get the returned HTTP body
            CityWeatherData data = cityWeatherDataApiCall.execute().body();

            // We override the city name returned from the API with what the user
            // Actually entered instead for the model, so that the model is consistent
            // With the user input
            data.CityName = city;

            try {
                // If an icon was retrieved from the API call, we decode it and encode
                // It as a Base64 string which is saved in SharedPrefs with the rest of the city
                // Data
                if(data.WeatherDescription.get(0).Icon != null){
                    String icon = data.WeatherDescription.get(0).Icon;
                    String imageUrl = "http://openweathermap.org/img/w/" + icon + ".png";
                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(imageUrl).getContent());
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream .toByteArray();
                    data.EncodedWeatherIcon = Base64.encodeToString(byteArray, Base64.DEFAULT);
                }
            } catch (IOException e) {
                Log.d("Weather", "IOException from getting ICON");
                e.printStackTrace();
            }

            // Save city weather data to shared prefs
            repository.SaveCityWeatherData(city, data);
        } catch (Exception e)
        {
            Log.d("Weather", "Exception: " + e.toString());
            Log.d("Weather", "Exception occurred in BoundWeatherService: " + e.getMessage());
        }
    }

    // Get a list of weather data for all the user's cities
    public ArrayList<CityWeatherData> getAllCitiesWeather(){
        Cities cities = repository.GetCities();
        ArrayList<CityWeatherData> data = new ArrayList<>();
        for (String city:cities.Cities) {
            CityWeatherData weatherData = repository.GetCityWeatherData(city);
            data.add(weatherData);
        }
        return data;
    }

    // Add a city to the list of user's cities
    public void AddCity(String cityName){
        repository.SaveCity(cityName);
        ForceRefresh();
    }

    // Remove a city from the list of user's cities
    public void RemoveCity(String cityName){
        repository.RemoveCity(cityName);
        ForceRefresh();
    }

    // Force refresh will start an asynchronous task which will update weather data for all
    // Cities which the user have entered
    public void ForceRefresh(){
        AsyncTask<String, String, String> asyncTask = new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String[] strings) {
                Cities cities = repository.GetCities();
                for (String city:cities.Cities) {
                    getCurrentWeather(city);
                }
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                // After weather data update is done, we send a local broadcast so that other activities
                // Can react to it
                Intent broadcastIntent = new Intent(DATA_READY_BROADCAST);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);

                // Post Notification
                final Intent emptyIntent = new Intent();
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                // Get local time
                // This is heavily inspired by: https://stackoverflow.com/questions/11913358/how-to-get-the-current-time-in-android
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
                Date currentLocalTime = cal.getTime();
                DateFormat date = new SimpleDateFormat("HH:mm:ss");
                date.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));

                String localTime = date.format(currentLocalTime);

                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= 26)
                {
                    Notification.Builder notification = new Notification.Builder(getApplicationContext(), MainActivity.CHANNEL_ID)
                            .setContentTitle(getText(R.string.AppName))
                            .setContentText(getText(R.string.NotificationChannelDescription) + ": " + localTime)
                            .setSmallIcon(R.mipmap.ic_launcher_round)
                            .setTicker(getText(R.string.NotificationChannelDescription));

                    notificationManager.notify(101, notification.build());
                } else
                {
                    NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext())
                            .setChannel(MainActivity.CHANNEL_ID)
                            .setContentTitle(getText(R.string.AppName))
                            .setContentText(getText(R.string.NotificationChannelDescription) + ": " + localTime)
                            .setSmallIcon(R.mipmap.ic_launcher_round)
                            .setTicker(getText(R.string.NotificationChannelDescription));

                    notificationManager.notify(101, notification.build());
                }
            }
        };

        // Start to run the previously defined asynchronous task
        asyncTask.execute("yolo");
    }

    /* Binder Method */
    public class WeatherServiceBinder extends Binder {
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

        Timer = new Timer();
        weatherApiService = retrofit.create(OpenWeatherApiService.class);
        repository = new SharedPreferenceRepository(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!isStarted){
        /* Heavily influence by https://stackoverflow.com/questions/6531950/how-to-execute-async-task-repeatedly-after-fixed-time-intervals*/
            TimerTask doAsynchronousTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                ForceRefresh();
                            } catch (Exception e) {
                                Log.d("Weather", "Timed refresh threw an exception: " + e);
                            }
                        }
                    });
                }
            };
            Timer.schedule(doAsynchronousTask, 0, 5*60*1000); // Every 5 minutes
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timer.cancel();
        Timer.purge();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
