package com.project.grace.floodmeterapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class RateFlood extends Fragment implements LocationListener {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String thisDate;
    private SpinnerAdapter adapter;
    private String weatherInfo;
    private FusedLocationProviderClient client;
    private TextView txtWeather, txtFloodLevel;
    private RadioButton rbtnStatus;
    private RadioGroup rStatusGroup;
    private Button submitButton;
    double locale[];

    public static RateFlood getInstance() {
        return new RateFlood();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        assert bundle != null;
        locale = bundle.getDoubleArray("locale");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_rate_flood, container, false);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yy");
        Date todayDate = new Date();
        thisDate = df.format(todayDate);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("crowdsource").child(thisDate);

        ArrayList<ItemData> list = new ArrayList<>();
        list.add(new ItemData("Cloudy", R.mipmap.ic_cloudy));
        list.add(new ItemData("Light Rainfall", R.mipmap.ic_rain_low));
        list.add(new ItemData("Medium Rainfall", R.mipmap.ic_rain_medium));
        list.add(new ItemData("Heavy Rainfall", R.mipmap.ic_rain_high));
        list.add(new ItemData("Thunderstorm", R.mipmap.ic_rain_thunder));
        Spinner sp = rootView.findViewById(R.id.spinner);

        txtWeather = rootView.findViewById(R.id.txtWeather);
        adapter = new SpinnerAdapter(this.getActivity(), R.layout.spinner_weather, R.id.txt, list);
        sp.setAdapter(adapter);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                weatherInfo = adapter.getWeatherInfo(i);
                txtWeather.setText("Weather: " + weatherInfo);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button button1 = (Button) rootView.findViewById(R.id.submit_button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                List<LatLng> points = new ArrayList<>();
                points.add(new LatLng(7.085500, 125.614343));
                points.add(new LatLng(7.085500, 125.614343));
                points.add(new LatLng(7.086071, 125.617847));
                points.add(new LatLng(7.087436, 125.616721));

                double positionX = locale[0];
                double positionY = locale[1];
//
//                double positionX = 7.0658;
//                double positionY = 125.5967;

                double[] vertX = new double[]{
                        7.084118f, 7.086073f, 7.087450f, 7.085497f
                };
                double[] vertY = new double[]{
                        125.615469f, 125.617858f, 125.616718f, 125.614337f
                };

                if (isInsideLocation(4, vertX, vertY, positionX, positionY)){
                    System.out.println("inside");
                }

                CrowdSource cs = new CrowdSource();
                cs.setCrowdsource(0);
                cs.setLat((float) positionX);
                cs.setLon((float) positionY);
                cs.setTag(weatherInfo);
                cs.setDateAdded(thisDate);
                cs.setUserID(user.getUid());
                myRef.child((user.getUid())).setValue(cs);


                Bundle bundle = new Bundle();
                bundle.putDoubleArray("locale", new double[]{positionX, positionY});
                Fragment fragment = MapsFragment.getInstance();
                fragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
        });


        // *** Initialize radio button components
        // *** Changes made by Matt Lagat
        rStatusGroup = rootView.findViewById(R.id.radioGroup);
        txtFloodLevel = rootView.findViewById(R.id.txtFloodLevel);
        // *** End of code lines

        rStatusGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // checkedId is the RadioButton selected
            rbtnStatus = rootView.findViewById(checkedId);
            txtFloodLevel.setText("Flood Level: " + rbtnStatus.getText());
        });

        return rootView;
    }


    //    Algorithm for detecting if it is outside or inside...
    public boolean isInsideLocation(int nvert, double[] vertx, double[] verty, double testx, double testy) {
        int i, j;
        boolean c = false;
        for (i = 0, j = nvert - 1; i < nvert; j = i++) {
            if (((verty[i] > testy) != (verty[j] > testy)) && (testx < (vertx[j] - vertx[i]) * (testy - verty[i]) / (verty[j] - verty[i]) + vertx[i]))
                c = !c;
        }
        return c;
    }

    @Override
    public void onLocationChanged(Location location) {
        String txtLat;
        txtLat = ("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }
}
