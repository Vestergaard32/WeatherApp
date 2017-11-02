package com.android.vestergaard.weatherapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.vestergaard.weatherapp.Models.CityWeatherData;
import com.android.vestergaard.weatherapp.Models.WeatherDescription;
import com.google.gson.Gson;

public class CityWeatherDetailsActivity extends AppCompatActivity {
    public static final int OK_RESULT_CODE = 100;
    public static final int REMOVE_RESULT_CODE = 101;
    Intent cityWeatherDetailsIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_weather_details);

        // Get city weather data intent
        cityWeatherDetailsIntent = getIntent();

        Gson gson = new Gson();
        CityWeatherData cityWeatherData = (CityWeatherData)gson.fromJson(cityWeatherDetailsIntent.getStringExtra("cityWeather"), CityWeatherData.class);
        Log.d("Weather", "Timestamp: " + cityWeatherData.RetrievalDate);
        SetCityWeatherData(cityWeatherData);

        SetupEventListeners();
    }

    private void SetupEventListeners()
    {
        Button btnOk = (Button)findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(OK_RESULT_CODE);
                finish();
            }
        });

        Button btnRemove = (Button)findViewById(R.id.btnRemove);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(REMOVE_RESULT_CODE, getIntent());
                finish();
            }
        });
    }

    private void SetCityWeatherData(CityWeatherData cityWeatherData)
    {
        TextView txtViewCityName = (TextView)findViewById(R.id.txtViewCityName);
        txtViewCityName.setText(cityWeatherData.CityName);

        TextView cityWeatherHumidity = (TextView)findViewById(R.id.CityWeatherHumidity);
        cityWeatherHumidity.setText(cityWeatherData.WeatherData.Humidity + "%");

        TextView cityWeatherTemperature = (TextView)findViewById(R.id.CityWeatherTemperature);
        cityWeatherTemperature.setText(cityWeatherData.WeatherData.Temperature + "°C");

        TextView cityWeatherDescription = (TextView)findViewById(R.id.CityWeatherDescription);

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
    }
}
