package com.android.vestergaard.weatherapp.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WeatherData {
    @SerializedName("humidity")
    public int Humidity;
    @SerializedName("temp")
    public double Temperature;
}
