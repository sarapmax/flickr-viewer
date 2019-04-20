package com.example.flickrviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

class FlickrImageAdapter extends BaseAdapter {
    private final Context mContext;
    private final ArrayList<Photo> mPhotos;

    public FlickrImageAdapter(final Context context, ArrayList<Photo> photos) {
        mContext = context;
        mPhotos = photos;
    }

    @Override
    public int getCount() {
        return mPhotos.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View layout = inflater.inflate(R.layout.image_item, null);

        Photo photo;

        photo = mPhotos.get(i);

        ImageView imageView = layout.findViewById(R.id.imageView);
        imageView.setImageBitmap(photo.getImage());

        TextView textView = layout.findViewById(R.id.textView);
        textView.setText(photo.getTitle());

        int width = ((GridView) viewGroup).getColumnWidth();
        layout.setLayoutParams(new GridView.LayoutParams(width, width));

        return layout;
    }
}
