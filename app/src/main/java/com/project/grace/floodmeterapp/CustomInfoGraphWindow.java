package com.project.grace.floodmeterapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomInfoGraphWindow implements GoogleMap.InfoWindowAdapter {

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private final View mWindow;
    private final Context mContext;

    private StorageReference mStorageRef;
    private String thisDate;
    private Bitmap my_image;
    private static LineData lineData;
    private ArrayList<Entry> waterLevel;
    private ArrayList<Entry> rainfall;

    private String latestReading;

    private TextView textDate;
    private TextView textWaterLevel;
    private TextView textRainfall;

    public CustomInfoGraphWindow(Context context, ArrayList<Entry> entry, ArrayList<Entry> rainfall, String currentDate) {
        this.mContext = context;
        this.waterLevel = entry;
        this.rainfall = rainfall;
        this.latestReading = currentDate;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_graph_info_window, null);
    }


    private void renderWindowText(Marker marker, View view) {

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yy");
        Date todayDate = new Date();
        String thisDate = df.format(todayDate);

        String title = marker.getTitle();
        TextView tvTitle = view.findViewById(R.id.title);

        if (!title.equals(""))
            tvTitle.setText(title);

        String snippet = marker.getSnippet();
        TextView tvSnippest = view.findViewById(R.id.snippet);

        if (!snippet.equals(""))
            tvSnippest.setText(snippet);

        textDate = view.findViewById(R.id.textDate);
        textDate.setText(latestReading);

        if(waterLevel.size()>0){
            textWaterLevel = view.findViewById(R.id.textWaterLevel);
            textWaterLevel.setText("Water Level: " + waterLevel.get(waterLevel.size() - 1).getY() + "m");
        }
        if(rainfall.size()>0){
            textRainfall = view.findViewById(R.id.textRainfall);
            textRainfall.setText("Rainfall Amount: " + rainfall.get(rainfall.size() - 1).getY() + "mm");
        }


    }

    @Override
    public View getInfoWindow(Marker marker) {

        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }
}
