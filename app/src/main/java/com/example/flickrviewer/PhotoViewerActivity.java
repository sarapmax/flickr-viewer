package com.example.flickrviewer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class PhotoViewerActivity extends AppCompatActivity {
    private ViewSwitcher mViewSwitcher;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        mViewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
        mImageView = (ImageView) findViewById(R.id.imageView);

        Intent intent = getIntent();
        final String photoUrl = intent.getStringExtra("url");

        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                mImageView.setImageBitmap(bitmap);
                mViewSwitcher.showNext();
            }

            @SuppressLint("WrongThread")
            @Override
            protected Bitmap doInBackground(String... strings) {
                try {
                    URL url = new URL(strings[0]);
                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.connect();

                    InputStream is = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    return bitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute(photoUrl);
    }
}
