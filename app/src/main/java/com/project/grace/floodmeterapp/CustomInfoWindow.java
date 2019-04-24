package com.project.grace.floodmeterapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.w3c.dom.Text;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private final Context mContext;

    public CustomInfoWindow(Context context){
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.info_window, null);
    }

    private void renderWindowText(Marker marker, View view){

        String title = marker.getTitle();
        TextView tvTitle = (TextView) view.findViewById(R.id.marker_title);

        if(!title.equals(""))
            tvTitle.setText(title);

        String snippet = marker.getSnippet();
        TextView tvSnippest = (TextView) view.findViewById(R.id.maker_snippest);
        if(!snippet.equals(""))
            tvSnippest.setText(snippet);


//        ImageView
        
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
