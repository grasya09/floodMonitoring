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

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.project.grace.floodmeterapp.Constant.CHANNEL_ID;
import static com.project.grace.floodmeterapp.Constant.LEVEL_1;
import static com.project.grace.floodmeterapp.Constant.LEVEL_2;
import static com.project.grace.floodmeterapp.Constant.LEVEL_3;
import static com.project.grace.floodmeterapp.Constant.LEVEL_4;
import static com.project.grace.floodmeterapp.Constant.LEVEL_5;

public class ServiceProvider extends Service {

    private static final String TAG = "ServiceProvider";
    private DataWorker dataWorker;
    private double[] testCases = new double[3];
    private double[] waterLevelTests = new double[3];
    private double[] rainFallTests = new double[3];
    private double[] xy = new double[3];
    private double[] getRainFallTestsSquared = new double[3];
    private NotificationManager notifManager;

    private double max;
    private double min;
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

    private void setupNotification(double y) {
        if (y > 8.5) {
            sendNotification("Flood Monitoring", LEVEL_1);
        } else if (y >= 6.5 && y <= 8.5) {
            sendNotification("Flood Monitoring", LEVEL_3);
        } else if (y < 6.5) {
            sendNotification("Flood Monitoring", LEVEL_4);
        }
    }

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
            builder.setContentTitle(aMessage)                            // required
                .setSmallIcon(R.mipmap.ic_launcher)   // required
                .setContentText(this.getString(R.string.app_name)) // required
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setTicker(aMessage)
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        } else {
            builder = new NotificationCompat.Builder(this, id);
            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            builder.setContentTitle(aMessage)                            // required
                .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                .setContentText(this.getString(R.string.app_name)) // required
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
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


    private double solveK() {
        // long version
        //var log = Math.Log(waterlevels.Length, 10);
        //var s = 1 + 3.22;
        //var kk = s * log;
        double k = Math.round(1 + 3.322 * (Math.log10(matinaData.size())));
        return k;
    }

    private double solveR() {
        max = matinaData.get(0).getY();
        min = matinaData.get(0).getY();

        for (Entry s : matinaData) {
            if (max < s.getY()) {
                max = s.getY();
            }
            if (min > s.getY()) {
                min = s.getY();
            }
        }
        return max - min;
    }

    private double solveI() {
        return solveR() / solveK();
    }


    public double solveLL() {
        return solveI() - 1 + min;
    }

    private double solveUL() {
        double lowerLimit = solveLL();
        double ul = lowerLimit;
        while (max >= ul) {
            ul += lowerLimit + 0.01;
        }
        return ul;
    }

    private int intervalCount() {
        int c = (int) (solveUL() / solveLL()) - 1;
        return c;
    }


    private double rangeAbove() {
        double a = solveUL();
        return a;
    }


    public double rangeBetween() {
        double intervalby3 = (double) intervalCount() / 3;
        double bw = solveUL() - (solveLL() + 0.01) * Math.round(intervalby3);
        return bw;
    }

    public double rangeBelow() {
        double intervalby3 = (double) intervalCount() / 3;
        double b = solveUL() - (solveLL() + 0.01) * (Math.round(intervalby3) * 2);
        return b;
    }


    class NotificationDataManager extends AsyncTask<Void, Void, Void> {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... voids) {
            //            <editor-fold desc="Thrad for rainfall API">
            try {

                while (true) {
                    dataWorker = new DataWorker();
                    if (dataWorker.getMatinaBridgeAPIData().size() > 0) {
//                    waterLevelTests[0] = 0.0;
//                    for (Entry entry : dataWorker.getMatinaBridgeAPIData()) {
//                        waterLevelTests[0] += (double) entry.getY();
//                    }
//                    waterLevelTests[1] = 0.0;
//                    for (Entry entry : dataWorker.getMintalBridgeAPIData()) {
//                        waterLevelTests[1] += (double) entry.getY();
//                    }
//                    waterLevelTests[2] = 0.0;
//                    for (Entry entry : dataWorker.getWaanBridgeAPIData()) {
//                        waterLevelTests[2] += (double) entry.getY();
//                    }
//                    rainFallTests[0] = 0.0;
//                    for (Entry entry : dataWorker.getMatinaBridgeAPIData()) {
//                        rainFallTests[0] += (double) entry.getY();
//                    }
//                    rainFallTests[1] = 0.0;
//                    for (Entry entry : dataWorker.getMintalBridgeAPIData()) {
//                        rainFallTests[1] += (double) entry.getY();
//                    }
//                    rainFallTests[2] = 0.0;
//                    for (Entry entry : dataWorker.getWaanBridgeAPIData()) {
//                        rainFallTests[2] += (double) entry.getY();
//                    }
//                    xy[0] = waterLevelTests[0] * rainFallTests[0];
//                    xy[1] = waterLevelTests[1] * rainFallTests[1];
//                    xy[1] = waterLevelTests[2] * rainFallTests[2];
//                    getRainFallTestsSquared[0] = Math.pow(waterLevelTests[0], 2);
//                    getRainFallTestsSquared[1] = Math.pow(waterLevelTests[1], 2);
//                    getRainFallTestsSquared[2] = Math.pow(waterLevelTests[2], 2);
//                    double xMin = 0.0d;
//                    for (double d : rainFallTests)
//                        xMin += d;
//                    double yMin = 0.0d;
//                    for (double d : waterLevelTests)
//                        yMin += d;
//                    xMin = xMin / 3.0d;
//                    yMin = yMin / 3.0d;
//                    double temp1 = 0.0;
//                    for (double d : xy)
//                        temp1 += d;
//                    double temp2 = 0.0;
//                    for (double d : xy)
//                        temp2 += d;
//                    double b = temp1 / temp2;
//                    double a = yMin - (b * xMin);
//                    double y = (a * xMin) + b;
//                    setupNotification(y);
                        // Referenc
                        matinaData = dataWorker.getMatinaBridgeAPIData();
                        double average = rangeBetween();
                        double high = rangeAbove();
                        double low = rangeBelow();

                        Thread.sleep(10000);
                    }
                }

<<<<<<< HEAD
=======
                /*
                * This equation will solve the range (Low, Average, High)
                * k = 1+3.322(LogN)
                * R = UL - LL
                * i = R/k
                *
                *
                * */
>>>>>>> develop

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
