package com.project.grace.floodmeterapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private final View mWindow;
    private final Context mContext;

    private StorageReference mStorageRef;
    private String thisDate;
    private Bitmap my_image;
    private ImageView imageView;
    private String title;
    private String snippet;
    private ProgressDialog progressDialog;

    public CustomInfoWindow(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.info_window, null);
    }

    private void renderWindowText(Marker marker, View view) {

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yy");
        Date todayDate = new Date();
        String thisDate = df.format(todayDate);

        title = marker.getTitle();
        TextView tvTitle = view.findViewById(R.id.marker_title);

        if (!title.equals(""))
            tvTitle.setText(title);

        snippet = marker.getSnippet();
        TextView tvSnippest = view.findViewById(R.id.maker_snippet);

        if (!snippet.equals(""))
            tvSnippest.setText(snippet);


        mStorageRef = FirebaseStorage.getInstance().getReference("crowdsource").child(thisDate);
        ImageStorageSync sync = new ImageStorageSync();
        sync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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


    class ImageStorageSync extends AsyncTask<Void, Void, Void> {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... voids) {
            //            <editor-fold desc="Thrad for rainfall API">
            StorageReference ref = mStorageRef.child(snippet);
            try {
                final File localFile = File.createTempFile("Images", "bmp");
                ref.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    my_image = BitmapFactory.decodeFile(localFile.getAbsolutePath(), options);
                    Bitmap resized = Bitmap.createScaledBitmap(my_image, 32, 32, true);
                    my_image = resized;
                    imageView = mWindow.findViewById(R.id.pic);
//                    if (my_image != null)
//                        imageView.setImageBitmap(my_image);
//                    else

                    imageView.setImageBitmap(my_image);
                }).addOnFailureListener(e -> {
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                    imageView = mWindow.findViewById(R.id.pic);
                    imageView.setImageResource(R.drawable.camera);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (progressDialog != null)
                progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(mContext,
                "Fetching API Data...",
                "Please patiently wait.");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
