package com.project.grace.floodmeterapp.dummy;

import com.project.grace.floodmeterapp.R;

import java.util.ArrayList;

public class DummyWeather {

    public static ArrayList<Weather> weathers = new ArrayList<>();

    static{

        Weather weather = new Weather(R.mipmap.ic_cloudy, "No Rain", "No Rain");
        weathers.add(weather);
        weather = new Weather(R.mipmap.ic_rain_low, "Light Rainfall", "Individual drops easily identified and puddles(small muddy pools) form slowly.");
        weathers.add(weather);
        weather = new Weather(R.mipmap.ic_rain_medium, "Moderate Rainfall", "Puddles rapidly forming and down pipes flowing freely.");
        weathers.add(weather);
        weather = new Weather(R.drawable.rain_high, "Heavy Rainfall", "The sky is overcast, there is a continuous precipitation.");
        weathers.add(weather);
    }
}
