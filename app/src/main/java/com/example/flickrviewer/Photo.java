package com.example.flickrviewer;

import android.graphics.Bitmap;

public class Photo {
    String mTitle;
    Bitmap mImage;

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setImage(Bitmap image) {
        mImage = image;
    }

    public String getTitle() {
        return mTitle;
    }

    public Bitmap getImage() {
        return mImage;
    }
}
