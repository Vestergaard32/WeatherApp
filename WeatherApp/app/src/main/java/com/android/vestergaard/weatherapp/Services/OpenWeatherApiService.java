package com.android.vestergaard.weatherapp.Services;

import com.android.vestergaard.weatherapp.Models.CityWeatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by David on 26/10/2017.
 */

public interface OpenWeatherApiService {
    @GET("weather?units=metric&APPID=a8efb0323e509e281fd214964e53ac97")
    Call<CityWeatherData> getCityWeatherData(@Query("q") String cityName);
}
