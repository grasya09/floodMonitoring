package com.project.grace.floodmeterapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.github.mikephil.charting.data.Entry;
import com.project.grace.floodmeterapp.PhilSensorData.DataWorker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.project.grace.floodmeterapp.Constant.CHANNEL_ID;
import static com.project.grace.floodmeterapp.Constant.LEVEL_1;
//import static com.project.grace.floodmeterapp.Constant.LEVEL_2;
import static com.project.grace.floodmeterapp.Constant.LEVEL_3;
import static com.project.grace.floodmeterapp.Constant.LEVEL_4;
import static com.project.grace.floodmeterapp.Constant.LEVEL_5;
import static com.project.grace.floodmeterapp.Constant.LEVEL_6;
import static com.project.grace.floodmeterapp.Constant.LEVEL_7;

public class ServiceProvider extends Service {

    private static final String TAG = "ServiceProvider";
    private DataWorker dataWorker;
    private double[] testCases = new double[3];
    private double[] waterLevelTests = new double[1];
    private double[] rainFallTests = new double[1];
    private double[] xy = new double[1];
    private double[] getRainFallTestsSquared = new double[1];
    private double yLv;
    private double xLv;

    private ArrayList<Double> waterLevelList = new ArrayList<>();
    private ArrayList<Double> rainFallList = new ArrayList<>();
    private ArrayList<Double> xyList = new ArrayList<>();
    private ArrayList<Double> xSquaredList = new ArrayList<>();
    private double summationXY;
    private double summationXSq;

//    private double[] waterLevelTests = new double[3];
//    private double[] rainFallTests = new double[3];
//    private double[] xy = new double[3];
//    private double[] getRainFallTestsSquared = new double[3];
    private NotificationManager notifManager;

    private double max;
    private double min;
    //double xMin = 0.0d;
    private ArrayList<Entry> matinaData = new ArrayList<>();


    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");
        NotificationDataManager ndm = new NotificationDataManager();
        ndm.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service onStartCommand");

        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        return Service.START_STICKY;

    }


    private void setupNotification(double y, double lastValue) {


        if (y > lastValue) {
            sendNotification("Flood Monitoring", LEVEL_1);
        } else if (y == lastValue) {
            sendNotification("Flood Monitoring", LEVEL_3);
        } else if (y < lastValue) {
            sendNotification("Flood Monitoring", LEVEL_4);
        }
    }


    private void setupNotificationNoRain(double beforeLV, double lastValue) {
         if(lastValue > beforeLV  ) {
             setupNotificationNoRain("Flood Monitoring", LEVEL_5);
         }
        else if(lastValue == beforeLV ) {
            setupNotificationNoRain("Flood Monitoring", LEVEL_6);
        }
        else if(lastValue < beforeLV  ) {
            setupNotificationNoRain("Flood Monitoring", LEVEL_7);
        }
    }



    /*For no rain notification*/

    public void setupNotificationNoRain(String title2, String aMessage) {
        //Get an instance of NotificationManager//
        final int NOTIFY_ID = 0; // ID of notification
        String id = CHANNEL_ID; // default_channel_id
        String title = title2; // Default Channel
        Intent intent;
        PendingIntent pendingIntent;

        NotificationCompat.Builder builder;
        if (notifManager == null) {
            notifManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, id);
            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            builder.setContentTitle(this.getString(R.string.app_name))                            // required
                    .setSmallIcon(R.mipmap.ic_launcher)   // required
                    .setContentText(aMessage)
                    //.setContentText(this.getString(R.string.app_name)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(aMessage))
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

        } else {
            builder = new NotificationCompat.Builder(this, id);
            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            builder.setContentTitle(this.getString(R.string.app_name))                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(aMessage)
                    //.setContentText(this.getString(R.string.app_name)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(aMessage))
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
        }


        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);
    }
    /*End of No rain notification*/


    public void sendNotification(String title2, String aMessage) {
        //Get an instance of NotificationManager//
        final int NOTIFY_ID = 0; // ID of notification
        String id = CHANNEL_ID; // default_channel_id
        String title = title2; // Default Channel
        Intent intent;
        PendingIntent pendingIntent;

        NotificationCompat.Builder builder;
        if (notifManager == null) {
            notifManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, id);
            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            builder.setContentTitle(this.getString(R.string.app_name))                            // required
                    .setSmallIcon(R.mipmap.ic_launcher)   // required
                    .setContentText(aMessage)
                    //.setContentText(this.getString(R.string.app_name)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(aMessage))
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

        } else {
            builder = new NotificationCompat.Builder(this, id);
            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            builder.setContentTitle(this.getString(R.string.app_name))                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(aMessage)
                    //.setContentText(this.getString(R.string.app_name)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(aMessage))
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
        }


        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);
    }


    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service onDestroy");
    }


    class NotificationDataManager extends AsyncTask<Void, Void, Void> {
        DecimalFormat df = new DecimalFormat("##00.000");
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... voids) {
            //            <editor-fold desc="Thrad for rainfall API">
            try {

                while (true) {

                    dataWorker = new DataWorker();

                    waterLevelTests[0] = 0.0;
                    for (Entry entry : dataWorker.getMatinaBridgeAPIData()) {
                        waterLevelTests[0] += (double) entry.getY();
                        waterLevelList.add((double) entry.getY());
                        yLv = (double) entry.getY();
                    }


//                    waterLevelTests[1] = 0.0;
//                    for (Entry entry : dataWorker.getMintalBridgeAPIData()) {
//                        waterLevelTests[1] += (double) entry.getY();
//                    }
//                    waterLevelTests[2] = 0.0;
//                    for (Entry entry : dataWorker.getWaanBridgeAPIData()) {
//                        waterLevelTests[2] += (double) entry.getY();
//                    }
                    //Rainfall data
                    rainFallTests[0] = 0.0;
                    for (Entry entry : dataWorker.getMatinaRainfallAPIData()) {
                        rainFallTests[0] += (double) entry.getY();
                        rainFallList.add((double) entry.getY());
                        xLv = (double) entry.getY();
                    }


                    for (int x = 0; x < rainFallList.size(); x++){
                        xSquaredList.add(rainFallList.get(x) * rainFallList.get(x));
                        xyList.add(rainFallList.get(x) * waterLevelList.get(x));

                        summationXY += xyList.get(x);
                        summationXSq += xSquaredList.get(x);

                    }

                    ArrayList<Double> testX = waterLevelList;
                    ArrayList<Double> testY = rainFallList;
                    ArrayList<Double> testXY = xyList;
                    ArrayList<Double> testXSq = xSquaredList;

                    double s = summationXSq;
                    double t = summationXY;





//                    rainFallTests[1] = 0.0;
//                    for (Entry entry : dataWorker.getMintalBridgeAPIData()) {
//                        rainFallTests[1] += (double) entry.getY();
//                    }
//                    rainFallTests[2] = 0.0;
//                    for (Entry entry : dataWorker.getWaanBridgeAPIData()) {
//                        rainFallTests[2] += (double) entry.getY();
//                    }
                    //xy[0] = waterLevelTests[0] * rainFallTests[0];

                    // xy[1] = waterLevelTests[1] * rainFallTests[1];
                    // xy[1] = waterLevelTests[2] * rainFallTests[2];
                    //getRainFallTestsSquared[0] = Math.pow(waterLevelTests[0], 2);
                    //getRainFallTestsSquared[0] = rainFallTests[0] * rainFallTests[0];
                    // getRainFallTestsSquared[1] = Math.pow(waterLevelTests[1], 2);
                    // getRainFallTestsSquared[2] = Math.pow(waterLevelTests[2], 2);

                    double sss = waterLevelList.get(waterLevelList.size()-2);
                    double ss = yLv;

                    int countN = dataWorker.getMatinaBridgeAPIData().size();
                    double xMin = 0.0d;
                    double xTotal = 0.0d;
                    for (double d : rainFallTests)
                        xTotal += d;
                    xMin= xTotal / countN;
                    double yMin = 0.0d;
                    double yTotal = 0.0d;
                    for (double d : waterLevelTests)
                        yTotal += d;
                    yMin = yTotal /countN;

                    //summation of xy
                    double temp1 = 0.0;
                    for (double d : xy)
                        temp1 += d;

                    //summation of x squared
                    double temp2 = 0.0;
                    for (double d : getRainFallTestsSquared)
                        temp2 += d;

                    //for b1
                    double b1 = (xTotal * yTotal)/countN;
                    double bb1 =(xTotal * xTotal)/countN;

                    double fb1 = (summationXY - b1) / (summationXSq - bb1);

                    //for b0
                    double b0 = yMin - (fb1 * xMin);

                    double y = b0 + (fb1 * Double.valueOf(df.format(xLv)) );

//                    double b = summationXY / summationXSq;
//                    double a = yMin - (b * xMin);
//                    double y = (a * Double.valueOf(df.format(xLv))) + b;
//                    double c = Double.valueOf(df.format(xLv));
//                    double c1=c;
                    y = Double.valueOf(df.format(y));

                    if(xMin!=0) {
                        setupNotification(y, Double.valueOf(df.format(yLv)));
                    }
                    else{
                        setupNotificationNoRain(waterLevelList.get(waterLevelList.size()-2),Double.valueOf(df.format(yLv)));
                    }

                    Thread.sleep(1000000);
                }



            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }


    }
}
