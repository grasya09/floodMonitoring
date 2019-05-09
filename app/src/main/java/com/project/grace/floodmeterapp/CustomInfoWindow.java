package com.project.grace.floodmeterapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.CircularProgressDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
    private TextView tv;
    private String title;
    private String snippet;
    private ProgressDialog progressDialog;
    private Marker marker;
    private boolean isImageLoad = false;
    private CircularProgressDrawable circularProgressDrawable;
    private ProgressBar progressBar;

    public CustomInfoWindow(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.info_window, null);
        this.circularProgressDrawable = new CircularProgressDrawable(mContext);
    }

    private void getImage() {

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
        StorageReference ref = mStorageRef.child(snippet);

//        Glide.with(mContext)
//            .load(ref)
//            .into(imageView);

//        StorageReference rf = mStorageRef.child(snippet);

//        if(imageView != null){
//        }else{

        if (!isImageLoad) {

            circularProgressDrawable.setStrokeWidth(5f);
            circularProgressDrawable.setCenterRadius(30f);
            circularProgressDrawable.start();

            imageView = mWindow.findViewById(R.id.pic_info);
            ImageStorageSync sync = new ImageStorageSync();
            sync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

//        }


    }


    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, mWindow);
        this.marker = marker;
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }


    class ImageStorageSync extends AsyncTask<String, Bitmap, Void> {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(String... params) {

            StorageReference ref = mStorageRef.child(snippet);
            try {
                final File localFile = File.createTempFile("Images", "bmp");
                ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            GlideApp.with(mContext)
                                .load(task.getResult())
                                .apply(RequestOptions.circleCropTransform().override(128, 128))
//                                .listener(new RequestListener<Drawable>() {
//
//                                    @Override
//                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                                        progressBar.setVisibility(View.GONE);
//                                        return false;
//                                    }
//
//                                    @Override
//                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                                        progressBar.setVisibility(View.GONE);
//                                        return false;
//                                    }
//                                })
                                .placeholder(circularProgressDrawable)
                                .into(imageView);
                            marker.showInfoWindow();
                            isImageLoad = true;
                        } else {
                            Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("Firebase id", user.getUid());
                        }
                    }
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
            imageView = mWindow.findViewById(R.id.pic_info);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Bitmap... bitmaps) {
            // Getting reference to the TextView tv_counter of the layout activity_main
            imageView = mWindow.findViewById(R.id.pic_info);
            if (imageView != null) {
                if (bitmaps[0] != null) {

                } else {
                    System.out.println("Bitmap is null");
                }
            } else {
                System.out.println("ERROR Image");
            }
        }

    }
}
