package com.android.vestergaard.weatherapp.Models;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.vestergaard.weatherapp.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by CodingBeagle on 01-11-2017.
 */
public class CityWeatherAdapter extends ArrayAdapter<CityWeatherData> {
    public CityWeatherAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<CityWeatherData> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Check if an existing view is being reused, otherwise inflate view
        if (convertView == null)
        {
            Log.d("Weather", "ConvertView was null!");
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.city_weather_list_item,
                    parent,
                    false);
        }

        // Get Weather Data for this position in list
        CityWeatherData cityWeatherData = getItem(position);

        // Find views that has to be populated with data
        TextView cityName = (TextView) convertView.findViewById(R.id.CityNameText);
        TextView cityTemperature = (TextView) convertView.findViewById(R.id.CityTemperature);
        TextView cityHumidity = (TextView) convertView.findViewById(R.id.CityHumidity);

        // Populate data
        cityName.setText(cityWeatherData.CityName);
        cityTemperature.setText(Double.toString(cityWeatherData.WeatherData.Temperature) + "°C");
        cityHumidity.setText(Integer.toString(cityWeatherData.WeatherData.Humidity) + "%");

        return convertView;
    }
}
