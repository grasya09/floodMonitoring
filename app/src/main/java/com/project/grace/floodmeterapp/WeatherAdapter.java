package com.project.grace.floodmeterapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.grace.floodmeterapp.dummy.Weather;

import org.w3c.dom.Text;

import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    private List<Weather> weatherList;

    public WeatherAdapter(List<Weather> weatherList) {
        this.weatherList = weatherList;
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View weatherView = inflater.inflate(R.layout.weather_list, viewGroup, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(weatherView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder viewHolder, int i) {
// Get the data model based on position
        Weather weather = weatherList.get(i);

        // Set item views based on your views and data model

        ImageView imageView = viewHolder.image;
        imageView.setImageResource(weather.getImage());
        TextView condition = viewHolder.condition;
        condition.setText(weather.getCondition());
        TextView desc = viewHolder.description;
        desc.setText(weather.getDescription());
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        ImageView image;
        TextView condition;
        TextView description;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.imageCondition);
            condition = (TextView) itemView.findViewById(R.id.textWeather);
            description = (TextView) itemView.findViewById(R.id.textWeatherDesc);
        }
    }
}
