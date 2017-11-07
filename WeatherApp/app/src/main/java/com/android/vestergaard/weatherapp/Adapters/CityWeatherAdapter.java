package com.android.vestergaard.weatherapp.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.vestergaard.weatherapp.Models.CityWeatherData;
import com.android.vestergaard.weatherapp.R;

import java.util.ArrayList;

public class CityWeatherAdapter extends ArrayAdapter<CityWeatherData> {
    public CityWeatherAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<CityWeatherData> objects) {
        super(context, resource, objects);
    }

    /*private view holder class
    * http://theopentutorials.com/tutorials/android/listview/android-custom-listview-with-image-and-text-using-arrayadapter/*/
    private class ViewHolder {
        ImageView imageView;
        TextView cityName;
        TextView cityTemperature;
        TextView cityHumidity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate view
        ViewHolder holder;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.city_weather_list_item,
                    parent,
                    false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.imageView);
            holder.cityName = convertView.findViewById(R.id.CityNameText);
            holder.cityTemperature = convertView.findViewById(R.id.CityTemperature);
            holder.cityHumidity = convertView.findViewById(R.id.CityHumidity);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        // Get Weather Data for this position in list
        CityWeatherData cityWeatherData = getItem(position);

        // Populate data
        if(holder != null){
            holder.cityName.setText(cityWeatherData.CityName+ ", " + cityWeatherData.metaWeatherData.Country);
            holder.cityTemperature.setText(Double.toString(cityWeatherData.WeatherData.Temperature) + "°C");
            holder.cityHumidity.setText(Integer.toString(cityWeatherData.WeatherData.Humidity) + "%");
            if(cityWeatherData.EncodedWeatherIcon != null && holder.imageView != null){
                String encodedBitmap = cityWeatherData.EncodedWeatherIcon;
                if(encodedBitmap != null){
                    try{
                        byte[] decodedbytes = Base64.decode(encodedBitmap, Base64.DEFAULT);
                        Bitmap bitMap = BitmapFactory.decodeByteArray(decodedbytes, 0, decodedbytes.length);
                        holder.imageView.setImageBitmap(bitMap);
                    }
                    catch (Exception e){
                        Log.d("Weather", "Image error");
                        Log.d("Weather", e.getMessage());
                    }
                }
            }
        }
        return convertView;
    }
}
