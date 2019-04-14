package com.example.flickrviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class FlickrImageAdapter extends BaseAdapter {
    private final Context mContext;
    private final ArrayList<Bitmap> mImages;
    private final ArrayList<String> mTitles;

    public FlickrImageAdapter(final Context context, final ArrayList<Bitmap> images, final ArrayList<String> titles) {
        mContext = context;
        mImages = images;
        mTitles = titles;
    }

    @Override
    public int getCount() {
        return mImages.size();
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
        View layout = (View) inflater.inflate(R.layout.image_item, null);

        ImageView imageView = (ImageView) layout.findViewById(R.id.imageView);
        imageView.setImageBitmap(mImages.get(i));

        TextView textView = (TextView) layout.findViewById(R.id.textView);
        textView.setText(mTitles.get(i));

        int width = ((GridView) viewGroup).getColumnWidth();
        layout.setLayoutParams(new GridView.LayoutParams(width, width));

        return layout;
    }
}
