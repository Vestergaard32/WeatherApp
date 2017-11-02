package com.android.vestergaard.weatherapp.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WeatherData {
    @SerializedName("humidity")
    public int Humidity;
    @SerializedName("pressure")
    public int Pressure;
    @SerializedName("temp")
    public double Temperature;
    @SerializedName("temp_min")
    public double MinimumTemperature;
    @SerializedName("temp_max")
    public double MaximumTemperature;
}
