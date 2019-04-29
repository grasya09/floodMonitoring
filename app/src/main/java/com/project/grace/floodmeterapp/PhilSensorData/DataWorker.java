package com.project.grace.floodmeterapp.PhilSensorData;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.project.grace.floodmeterapp.Graph;

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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class DataWorker {

    private static InputStream iStream;
    private static BufferedReader reader;

    private final String strUrlWaanBridge = "http://philsensors.asti.dost.gov.ph/php/24hrs.php?stationid=1177";
    private final String strUrlMintalBirdge = "http://philsensors.asti.dost.gov.ph/php/24hrs.php?stationid=1195";
    private final String strUrlMatinaBridge = "http://philsensors.asti.dost.gov.ph/php/24hrs.php?stationid=954&fbclid=IwAR3uykazMjdYDWTvFFjADihonicb6hh57Fb1CHCfhbXTNjQ45WCbLjGNYoo";
    private URL url = null;
    private String data = "";
    private StringBuffer sb = new StringBuffer();
    private HttpURLConnection connection;
    private WaterLevelMonitoring viewData;
    private ProgressDialog progressDialog;

    private ArrayList<Entry> wlmsDataWaan = new ArrayList<>();
    private ArrayList<Entry> wlmsDataMintal = new ArrayList<>();
    private ArrayList<Entry> wlmsDataMatina = new ArrayList<>();
    private ArrayList<Entry> rainfallDataWaan = new ArrayList<>();
    private ArrayList<Entry> rainfallDataMintal = new ArrayList<>();
    private ArrayList<Entry> rainfallDataMatina = new ArrayList<>();

    public DataWorker() throws ExecutionException, InterruptedException {
        viewData = new WaterLevelMonitoring();
        viewData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
    }

    public DataWorker(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
        viewData = new WaterLevelMonitoring();
        viewData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public ArrayList<Entry> getWaanBridgeAPIData() {
        return wlmsDataWaan;
    }

    public ArrayList<Entry> getMintalBridgeAPIData() {
        return wlmsDataMintal;
    }

    public ArrayList<Entry> getMatinaBridgeAPIData() {
        return wlmsDataMatina;
    }

    public ArrayList<Entry> getWaanRainfallAPIData() {
        return rainfallDataWaan;
    }

    public ArrayList<Entry> getMintalRainfallAPIData() {
        return rainfallDataMintal;
    }

    public ArrayList<Entry> getMatinaRainfallAPIData() {
        return rainfallDataMatina;
    }

    public AsyncTask.Status getStatus() {
        return viewData.getStatus();
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

            super.onPostExecute(aVoid);
        }
    }

}
