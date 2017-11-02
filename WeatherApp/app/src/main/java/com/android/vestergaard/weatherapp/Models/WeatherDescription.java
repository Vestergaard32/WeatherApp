package com.android.vestergaard.weatherapp.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WeatherDescription {
    @SerializedName("description")
    public String Description;
    @SerializedName("icon")
    public String Icon;
    @SerializedName("main")
    public String Keyword;
}
