package com.project.grace.floodmeterapp;

    import android.app.ProgressDialog;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.graphics.Color;
    import android.net.Uri;
    import android.os.AsyncTask;
    import android.os.Build;
    import android.os.Bundle;
    import android.support.annotation.RequiresApi;
    import android.support.v4.app.Fragment;
    import android.support.v7.app.AlertDialog;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.AdapterView;
    import android.widget.Spinner;

    import com.github.mikephil.charting.charts.LineChart;
    import com.github.mikephil.charting.components.YAxis;
    import com.github.mikephil.charting.data.Entry;
    import com.github.mikephil.charting.data.LineData;
    import com.github.mikephil.charting.data.LineDataSet;
    import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
    import com.project.grace.floodmeterapp.PhilSensorData.DataWorker;

    import org.json.JSONArray;
    import org.json.JSONException;
    import org.json.JSONObject;

    import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.InputStream;
    import java.io.InputStreamReader;
    import java.net.HttpURLConnection;
    import java.net.MalformedURLException;
    import java.net.URL;
    import java.sql.Array;
    import java.sql.Timestamp;
    import java.util.ArrayList;
    import java.util.Collection;
    import java.util.Collections;
    import java.util.concurrent.ExecutionException;


public class Graph extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private LineChart lineChart;
    private static String TAG = "Rain Gauge Chart";
    private static LineData lineData;
    private static InputStream iStream;
    private static BufferedReader reader;

    private final String strUrlWaanBridge = "http://philsensors.asti.dost.gov.ph/php/24hrs.php?stationid=1177";
    private final String strUrlMintalBirdge = "http://philsensors.asti.dost.gov.ph/php/24hrs.php?stationid=1195";
    private final String strUrlMatinaBridge = "http://philsensors.asti.dost.gov.ph/php/24hrs.php?stationid=954&fbclid=IwAR3uykazMjdYDWTvFFjADihonicb6hh57Fb1CHCfhbXTNjQ45WCbLjGNYoo";
    private URL url = null;
    private String data = "";
    private StringBuffer sb = new StringBuffer();
    double result = 0;
    private HttpURLConnection connection;
    private SpinnerAdapter adapter;

    private final String message = "You are not connected to the internet!";
    private boolean isDataConstant = true;
    private View rootView;
    private ProgressDialog progressDialog;
    private  DataWorker dataWorker;
    private Spinner sp;

    private WaterLevelMonitoring viewData;
    private ArrayList<Entry> wlmsDataWaan = new ArrayList<>();
    private ArrayList<Entry> wlmsDataMintal = new ArrayList<>();
    private ArrayList<Entry> wlmsDataMatina = new ArrayList<>();
    private ArrayList<Entry> rainfallDataWaan = new ArrayList<>();
    private ArrayList<Entry> rainfallDataMintal = new ArrayList<>();
    private ArrayList<Entry> rainfallDataMatina = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public static Graph getInstance() {
        return new Graph();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            dataWorker = new DataWorker();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_graph, container, false);
        lineChart = rootView.findViewById(R.id.line_chart);

        ArrayList<ItemData> list = new ArrayList<>();
        list.add(new ItemData("Matina Pangi Bridge, Davao City", R.drawable.bridge));
        list.add(new ItemData("Mintal Bridge, Davao City", R.drawable.bridge));
        list.add(new ItemData("Waan Bridge, Davao City", R.drawable.bridge));
        sp = rootView.findViewById(R.id.graph_spinner);

        adapter = new SpinnerAdapter(getActivity(), R.layout.spinner_weather, R.id.txt, list);
        sp.setAdapter(adapter);

        viewData = new WaterLevelMonitoring();
        viewData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        return rootView;
    }

    private void setupItemListener(){
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String location = adapter.getWeatherInfo(i);
                if(location.equals("Matina Pangi Bridge, Davao City")){
                    setupChart(rainfallDataMatina, wlmsDataMatina);
                }else if(location.equals("Mintal Bridge, Davao City")){
                    setupChart(rainfallDataMintal, wlmsDataMintal);
                }else if(location.equals("Waan Bridge, Davao City")){
                    setupChart(rainfallDataWaan, wlmsDataWaan);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void setupChart(ArrayList<Entry> data1, ArrayList<Entry> data2) {

        lineChart.setNoDataText("Tap to refresh.");

        LineDataSet set2 = new LineDataSet(data1, "Rainfall Amount");
        LineDataSet set1 = new LineDataSet(data2, "Water Level");
        set1.setFillAlpha(110);
        set1.setLineWidth(2.5f);
        set1.setColor(Color.rgb(66, 103, 178));
        set1.setCircleColor(Color.rgb(240, 238, 70));
        set1.setCircleRadius(2f);
        set1.setFillColor(Color.rgb(240, 238, 70));
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set1.setDrawValues(true);
        set1.setValueTextSize(5f);
        set1.setValueTextColor(Color.rgb(240, 238, 70));
        set1.setLineWidth(2f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set2);
        lineData = new LineData(dataSets);
        lineChart.getAxisLeft().setTextColor(Color.rgb(247, 77, 24));
        lineChart.getXAxis().setTextColor(Color.rgb(247, 77, 24));
        lineChart.getLegend().setTextColor(Color.rgb(247, 77, 24));
        lineChart.setData(lineData);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause() {
        if(progressDialog != null)
            progressDialog.dismiss();

        super.onPause();
    }



    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void showAlert(String message) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(rootView.getContext());
            dialog.setMessage(message);
            dialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                }
            });
            dialog.show();
        } catch (Exception e) {
        }
    }


    class WaterLevelMonitoring extends AsyncTask<Void, Void, Void> {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... voids) {
            //            <editor-fold desc="Thrad for rainfall API">
            try {

                String line = "";
                //Lacson Bridge
                url = new URL(strUrlMintalBirdge);
                connection = (HttpURLConnection) url.openConnection();
                iStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(iStream));


                while ((line = reader.readLine()) != null) {
                    data += line;
                }

                if (!data.equals("")) {
                    JSONObject jsonObject = new JSONObject(data);
                    jsonObject.toString();

                    JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    //For Values
                    int pos = 0;
                    for (int i = jsonArray.length() - 1; i >= 0; i--) {
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        float waterLevel = Float.parseFloat(jObject.getString("Waterlevel"));
                        float rainFallAmount = Float.parseFloat(jObject.getString("Rainfall Amount"));
                        wlmsDataMintal.add(new Entry((float) (pos + 2), waterLevel));
                        rainfallDataMintal.add(new Entry((float) (pos + 2), rainFallAmount));
                        pos++;
                        // here you put ean as key and nr as value
                    }

                }


                //Mc Arthur Birdge
                url = new URL(strUrlWaanBridge);
                connection = (HttpURLConnection) url.openConnection();
                iStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(iStream));
                line = "";
                data = "";

                while ((line = reader.readLine()) != null) {
                    data += line;
                }


                if (!data.equals("")) {
                    JSONObject jsonObject = new JSONObject(data);
                    jsonObject.toString();

                    JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    //For Values
                    int pos = 0;
                    for (int i = jsonArray.length() - 1; i >= 0; i--) {
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        String dateRecord = jObject.getString("Datetime Read");
                        float waterLevel = Float.parseFloat(jObject.getString("Waterlevel"));
                        float rainFallAmount = Float.parseFloat(jObject.getString("Rainfall Amount"));
                        Timestamp time = Timestamp.valueOf(jObject.getString("Datetime Read"));
                        wlmsDataWaan.add(new Entry((float) (pos + 2), waterLevel));
                        rainfallDataWaan.add(new Entry((float) (pos + 2), rainFallAmount));
                        pos++;
                        // here you put ean as key and nr as value
                    }
                }

                //Matina Pangi Birdge
                url = new URL(strUrlMatinaBridge);
                connection = (HttpURLConnection) url.openConnection();
                iStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(iStream));
                line = "";
                data = "";

                while ((line = reader.readLine()) != null) {
                    data += line;
                }


                if (!data.equals("")) {
                    JSONObject jsonObject = new JSONObject(data);
                    jsonObject.toString();

                    JSONArray jsonArray = jsonObject.getJSONArray("Data");
                    //For Values
                    int pos = 0;
                    for (int i = jsonArray.length() - 1; i >= 0; i--) {
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        float waterLevel = Float.parseFloat(jObject.getString("Waterlevel"));

                        float rainFallAmount = Float.parseFloat(jObject.getString("Rainfall Amount"));

                        wlmsDataMatina.add(new Entry((float) (pos + 2), waterLevel));
                        rainfallDataMatina.add(new Entry((float) (pos + 2), rainFallAmount));
                        pos++;
                        // here you put ean as key and nr as value
                    }
                }

                setupChart(rainfallDataMatina, wlmsDataMatina);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException ignored) {
                ignored.printStackTrace();
            } finally {
                try {
                    if (iStream != null && reader != null) {
                        reader.close();
                        iStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                connection.disconnect();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (progressDialog != null)
                progressDialog.dismiss();
            setupItemListener();
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getActivity(),
                "Fetching API Data...",
                "Please patiently wait.");
            super.onPreExecute();
        }
    }
}
