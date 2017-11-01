package com.android.vestergaard.weatherapp.Repositories;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.vestergaard.weatherapp.Models.CityWeatherData;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by CodingBeagle on 02-11-2017.
 */

public class SharedPreferenceRepository {
    private Context applicationContext;

    public SharedPreferenceRepository(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void SaveCityWeatherData(CityWeatherData cityWeatherData)
    {
        SharedPreferences sharedPref = applicationContext.getSharedPreferences("cityWeatherData", Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
        Gson gson = new Gson();

        sharedPrefEditor.putString(cityWeatherData.CityName, gson.toJson(cityWeatherData));
        sharedPrefEditor.commit();
    }

    public CityWeatherData GetCityWeatherData(String cityName)
    {
        SharedPreferences sharedPref = applicationContext.getSharedPreferences("cityWeatherData", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPref.getString(cityName, "");

        return gson.fromJson(json, CityWeatherData.class);
    }

    public void RemoveCityWeatherData(String cityName)
    {
        SharedPreferences sharedPref = applicationContext.getSharedPreferences("cityWeatherData", Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
        Gson gson = new Gson();

        sharedPrefEditor.remove(cityName);
        sharedPrefEditor.commit();
    }

    public ArrayList<CityWeatherData> GetAllCities()
    {
        return null;
    }
}
