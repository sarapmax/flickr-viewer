package com.example.flickrviewer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;

public final class PhotoDB {
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private PhotoDBHelper mHelper;

    public PhotoDB(Context context) {
        mContext = context;
    }

    public void open() throws SQLException {
        mHelper = new PhotoDBHelper(mContext);
        mDatabase = mHelper.getWritableDatabase();
    }

    public void close() throws SQLException {
        mHelper.close();
        mHelper = null;
        mDatabase = null;
    }

    public ArrayList<Photo> queryAll() {
        String query = "SELECT * FROM " + PhotoEntry.TABLE_NAME;
        ArrayList<Photo> photos = new ArrayList<>();
        SQLiteDatabase database = mHelper.getReadableDatabase();
        Cursor c = database.rawQuery(query, null);
        if (c != null) {
            while (c.moveToNext()) {
//                int id = c.getInt(PhotoEntry.INDEX_ID);
                String title = c.getString(c.getColumnIndex(PhotoEntry.COLUMN_TITLE));
                Bitmap image = DbBitmapUtility.getImage(c.getBlob(c.getColumnIndex(PhotoEntry.COLUMN_IMAGE)));

                Photo photo = new Photo();
                photo.setTitle(title);
                photo.setImage(image);

                Log.v("DBHelper: ", "Title: " + title);
                Log.v("DBHelper: ", "Image: " + image);

                photos.add(photo);
            }
        }

        return photos;
    }

    public long insertFlickrPhoto(ContentValues values) {
        Log.i("contentvalue", values.toString());

        return mDatabase.insert(PhotoEntry.TABLE_NAME, null, values);
    }

    public boolean deleteAllFlickrPhotos() {
        return mDatabase.delete(PhotoEntry.TABLE_NAME, null, null) > 0;
    }

    ContentValues createContentValues(String title, byte[] image) {
        ContentValues values = new ContentValues();

        values.put(PhotoEntry.COLUMN_TITLE, title);
        values.put(PhotoEntry.COLUMN_IMAGE, image);

        return values;
    }

    public static abstract class PhotoEntry implements BaseColumns {
        public static final String TABLE_NAME = "photos";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMAGE = "image";
        public static final int INDEX_ID = 0;
        public static final int INDEX_TITLE = 1;
        public static final int INDEX_IMAGE = 2;
    }

    public class PhotoDBHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "FlickrViewer.db";

        private static final String SQL_CREATE_ENTRIES =
                "create table " + PhotoEntry.TABLE_NAME + "(" +
                        PhotoEntry._ID + " integer primary key autoincrement, " +
                        PhotoEntry.COLUMN_TITLE + " text not null, " +
                        PhotoEntry.COLUMN_IMAGE + " BLOB " +
                        ");";

        public PhotoDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) { }
    }
}
