package com.project.grace.floodmeterapp.dummy;

import com.project.grace.floodmeterapp.R;

import java.util.ArrayList;

public class DummyWeather {

    public static ArrayList<Weather> weathers = new ArrayList<>();

    static{

        Weather weather = new Weather(R.mipmap.ic_cloudy, "Cloudy", "Cloudy Description");
        weathers.add(weather);
        weather = new Weather(R.mipmap.ic_rain_low, "Light Rainafall", "Cloudy Description");
        weathers.add(weather);
        weather = new Weather(R.mipmap.ic_rain_medium, "Medium Rainfall", "Cloudy Description");
        weathers.add(weather);
        weather = new Weather(R.drawable.rain_high, "Heavy Rainfall", "Cloudy Description");
        weathers.add(weather);
    }
}
