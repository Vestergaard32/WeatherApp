package com.android.vestergaard.weatherapp.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CityWeatherData {
    @SerializedName("id")
    public long CityId;

    @SerializedName("name")
    public String CityName;

    @SerializedName("dt")
    public String RetrievalDate;

    @SerializedName("main")
    public WeatherData WeatherData;

    @SerializedName("weather")
    public ArrayList<WeatherDescription> WeatherDescription;
}

