package com.android.vestergaard.weatherapp.Models;

import com.google.gson.annotations.SerializedName;

public class MetaWeatherData {
    @SerializedName("country")
    public String Country;
    @SerializedName("sunrise")
    public String SunRise;
    @SerializedName("sunset")
    public String SunSet;
}
