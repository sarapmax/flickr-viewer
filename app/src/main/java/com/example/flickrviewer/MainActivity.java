package com.example.flickrviewer;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    final private String mApikey = "dbb2b362edfccfd7ffcbb0736f4eb51c";
    private Flickr mFlickr;
    private PhotoList mPhotoList;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private FlickrImageAdapter mAdapter;
    private GridView gridView;
    private int totalImage = 20;
    PhotoDB mPhotoDB;
    private ArrayList<com.example.flickrviewer.Photo> photoArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFlickr = new Flickr(mApikey);

        mHandlerThread = new HandlerThread("FlickrHandlerThread");
        mHandlerThread.start();

        mPhotoDB = new PhotoDB(this);
        mPhotoDB.open();

        FloatingActionButton button = findViewById(R.id.fab);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadRecentImages();
            }
        });

        photoArrayList = new ArrayList<>();
        photoArrayList = mPhotoDB.queryAll();

        gridView = findViewById(R.id.gridView);
        mAdapter = new FlickrImageAdapter(this, photoArrayList);
        gridView.setAdapter(mAdapter);
    }

    public void downloadRecentImages() {
        mPhotoDB.deleteAllFlickrPhotos();

        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                URL url;

                try {
                    url = new URL(msg.getData().getString("smallUrl"));

                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.connect();

                    InputStream is = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    mPhotoDB.insertFlickrPhoto(mPhotoDB.createContentValues(msg.getData().getString("title"), DbBitmapUtility.getBytes(bitmap)));

                    if (msg.getData().getInt("index") == totalImage) {
                        photoArrayList = mPhotoDB.queryAll();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                gridView = findViewById(R.id.gridView);
                                mAdapter = new FlickrImageAdapter(MainActivity.this, photoArrayList);
                                gridView.setAdapter(mAdapter);

                                Log.i("i", "changed");
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        new AsyncTask<Void, Void, PhotoList>() {
            @Override
            protected void onPostExecute(PhotoList photos) {
                super.onPostExecute(photos);
                mPhotoList = photos;

                int i = 1;

                for (Photo photo : mPhotoList) {
                    Bundle bundle = new Bundle();

                    bundle.putString("title", photo.getTitle());
                    bundle.putString("largeUrl", photo.getLargeUrl());
                    bundle.putString("smallUrl", photo.getSmallUrl());
                    bundle.putInt("index", i++);

                    Message msg = mHandler.obtainMessage();
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
            }

            @SuppressLint("WrongThread")
            @Override
            protected PhotoList doInBackground(Void... voids) {
                try {
                    return mFlickr.getPhotosInterface().getRecent(null, totalImage, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }
}

