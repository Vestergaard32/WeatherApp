package com.android.vestergaard.weatherapp.Repositories;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.vestergaard.weatherapp.Models.Cities;
import com.android.vestergaard.weatherapp.Models.CityWeatherData;
import com.google.gson.Gson;

import java.util.ArrayList;

/*
    Repository pattern used for app code to save city weather data to shared prefs
 */
public class SharedPreferenceRepository {
    private Context applicationContext;
    private final String CITY_WEATHER_DATA_KEY = "CityWeatherData";
    private final String CITIES_KEY = "Cities";

    public SharedPreferenceRepository(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void SaveCityWeatherData(String city, CityWeatherData cityWeatherData)
    {
        SharedPreferences sharedPref = applicationContext.getSharedPreferences(CITY_WEATHER_DATA_KEY , Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
        Gson gson = new Gson();

        sharedPrefEditor.putString(city, gson.toJson(cityWeatherData));
        sharedPrefEditor.commit();
    }

    public CityWeatherData GetCityWeatherData(String cityName)
    {
        SharedPreferences sharedPref = applicationContext.getSharedPreferences(CITY_WEATHER_DATA_KEY , Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPref.getString(cityName, "");

        return gson.fromJson(json, CityWeatherData.class);
    }

    public void SaveCity(String city){
        SharedPreferences sharedPref = applicationContext.getSharedPreferences(CITIES_KEY , Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
        Gson gson = new Gson();

        Cities cities = GetCities();
        if(cities == null){
            cities = new Cities();
            cities.Cities = new ArrayList<>();
        }

        if(!cities.Cities.contains(city)){
            cities.Cities.add(city);
        }

        sharedPrefEditor.putString(CITIES_KEY, gson.toJson(cities));
        sharedPrefEditor.commit();
    }

    public Cities GetCities(){
        SharedPreferences sharedPref = applicationContext.getSharedPreferences(CITIES_KEY, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPref.getString(CITIES_KEY, "");

        Cities cities = gson.fromJson(json, Cities.class);
        if(cities == null){
            cities = new Cities();
            cities.Cities = new ArrayList<>();
        }
        return cities;
    }

    public void RemoveCity(String city)
    {
        SharedPreferences sharedPref = applicationContext.getSharedPreferences(CITIES_KEY , Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
        Gson gson = new Gson();

        Cities cities = GetCities();
        if(cities == null){
            return;
        }
        cities.Cities.remove(city);

        sharedPrefEditor.putString(CITIES_KEY, gson.toJson(cities));
        sharedPrefEditor.commit();
    }
}
