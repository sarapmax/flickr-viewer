package com.example.flickrviewer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    final private String mApikey = "b4e2a07187a6b7bc852579fc2b5b8a71";
    private Flickr mFlickr;
    private PhotoList mPhotoList;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private ArrayList<Bitmap> mImages;
    private ArrayList<String> mTitles;
    private ArrayList<String> mURLs;
    private FlickrImageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImages = new ArrayList<>();
        mTitles = new ArrayList<>();
        mURLs = new ArrayList<>();

        GridView gridView = findViewById(R.id.gridView);
        mAdapter = new FlickrImageAdapter(this, mImages, mTitles);
        gridView.setAdapter(mAdapter);

        gridView.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, PhotoViewerActivity.class);
                intent.putExtra("url", mURLs.get(i));
                startActivity(intent);
            }
        });

        mFlickr = new Flickr(mApikey);

        mHandlerThread = new HandlerThread("FlickrHandlerThread");
        mHandlerThread.start();

        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                URL url;

                try {
                    mTitles.add(msg.getData().getString("title"));
                    mURLs.add(msg.getData().getString("largeUrl"));

                    url = new URL(msg.getData().getString("smallUrl"));

                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.connect();

                    InputStream is = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    mImages.add(bitmap);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
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

                for (Photo photo : mPhotoList) {
                    Bundle bundle = new Bundle();

                    bundle.putString("title", photo.getTitle());
                    bundle.putString("largeUrl", photo.getLargeUrl());
                    bundle.putString("smallUrl", photo.getSmallUrl());

                    Message msg = mHandler.obtainMessage();
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
            }

            @SuppressLint("WrongThread")
            @Override
            protected PhotoList doInBackground(Void... voids) {
                try {
                    return mFlickr.getPhotosInterface().getRecent(null, 20, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }
}
