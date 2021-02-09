package ru.job4j.tourist.data_base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;

public class CoordinatesDbHelper extends SQLiteOpenHelper {
    private static final String DB = "Locations.db";
    private static final int VERSION = 1;
    public CoordinatesDbHelper(Context context) {
        super (context, DB, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DbSchema.NAME
                + " (" + "_id integer primary key autoincrement, "
                + DbSchema.Tab.LATITUDE + " real,"
                + DbSchema.Tab.LONGITUDE + " real" + ")");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    public void loadLocation(LatLng latLng) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbSchema.Tab.LATITUDE, latLng.latitude);
        values.put(DbSchema.Tab.LONGITUDE, latLng.longitude);
        db.insert(DbSchema.NAME, null, values);
    }
    public List<LatLng> getLocations() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<LatLng> locations = new ArrayList<>();
        Cursor cursor = db.query(DbSchema.NAME, null, null, null,
                null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            LatLng l = new LatLng(cursor.getDouble(
                    cursor.getColumnIndex(DbSchema.Tab.LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(DbSchema.Tab.LONGITUDE)));
            locations.add(l);
            cursor.moveToNext();
        }
        cursor.close();
        return locations;
    }
}
