package com.android.vestergaard.weatherapp.Models;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CityWeatherData {
    @SerializedName("name")
    public String CityName;

    @SerializedName("dt")
    public String RetrievalDate;

    @SerializedName("main")
    public WeatherData WeatherData;

    @SerializedName("sys")
    public MetaWeatherData metaWeatherData;

    @SerializedName("weather")
    public ArrayList<WeatherDescription> WeatherDescription;

    public String EncodedWeatherIcon;
}

