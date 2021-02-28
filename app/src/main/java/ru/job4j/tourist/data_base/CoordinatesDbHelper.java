package ru.job4j.tourist.data_base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;

import ru.job4j.tourist.model.Place;
import ru.job4j.tourist.model.Track;

public class CoordinatesDbHelper extends SQLiteOpenHelper {
    private static final String DB = "Locations.db";
    private static final int VERSION = 1;
    private final SQLiteDatabase readableDb = this.getReadableDatabase();
    private final SQLiteDatabase writableDb = this.getWritableDatabase();
    public CoordinatesDbHelper(Context context) {
        super (context, DB, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DbSchema.LocTab.NAME
                + " (" + "_id integer primary key autoincrement, "
                + DbSchema.LocTab.Cols.LATITUDE + " real,"
                + DbSchema.LocTab.Cols.LONGITUDE + " real,"
                + DbSchema.LocTab.Cols.TITLE + " text" + ")");
        db.execSQL("create table " + DbSchema.TrackTab.NAME
                + " (" + "_id integer primary key autoincrement, "
                + DbSchema.TrackTab.Cols.DATE + " integer,"
                + DbSchema.TrackTab.Cols.T_TITLE + " text" + ")");
        db.execSQL("create table " + DbSchema.TrackPoints.NAME
                + " (" + "_id integer primary key autoincrement, "
                + DbSchema.TrackPoints.Cols.T_LATITUDE + " real,"
                + DbSchema.TrackPoints.Cols.T_LONGITUDE + " real,"
                + DbSchema.TrackPoints.Cols.FOREIGN_KEY + ", "
                + "foreign key " + "(" + DbSchema.TrackPoints.Cols.FOREIGN_KEY + ")"
                + " references " + DbSchema.TrackTab.NAME + "(_id)"
                + " on delete cascade"
                + ")");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    public void loadPlace(Place place) {
        if (place != null) {
                ContentValues values = new ContentValues();
                double latitude = place.getCoordinates().latitude;
                double longitude = place.getCoordinates().longitude;
                String title = place.getTitle();
                values.put(DbSchema.LocTab.Cols.LATITUDE, latitude);
                values.put(DbSchema.LocTab.Cols.LONGITUDE, longitude);
                values.put(DbSchema.LocTab.Cols.TITLE, title);
                writableDb.insert(DbSchema.LocTab.NAME, null, values);
        }
    }
    public List<Place> getPlaces() {
        Cursor cursor = readableDb.query(DbSchema.LocTab.NAME, null, null,
                null, null, null, null);
        List<Place> result = new ArrayList<>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            result.add(new Place(new LatLng(
                    cursor.getDouble(cursor.getColumnIndex(DbSchema.LocTab.Cols.LATITUDE)),
                            cursor.getDouble(cursor.getColumnIndex(DbSchema.LocTab.Cols.LONGITUDE))),
                    cursor.getString(cursor.getColumnIndex(DbSchema.LocTab.Cols.TITLE))));
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }
    public void saveTrack(Track track) {
        ContentValues values = new ContentValues();
        if (track != null) {
            values.put(DbSchema.TrackTab.Cols.T_TITLE, track.getTitle());
            values.put(DbSchema.TrackTab.Cols.DATE, track.getDate());
            writableDb.insert(DbSchema.TrackTab.NAME, null, values);
            values.clear();
            Cursor cursor = readableDb.query(DbSchema.TrackTab.NAME, null, null,
                    null, null, null, null);
            cursor.moveToLast();
            int key = cursor.getInt(cursor.getColumnIndex("_id"));
            cursor.close();
            for (LatLng loc : track.getWay()) {
                values.put(DbSchema.TrackPoints.Cols.FOREIGN_KEY, key);
                values.put(DbSchema.TrackPoints.Cols.T_LATITUDE, loc.latitude);
                values.put(DbSchema.TrackPoints.Cols.T_LONGITUDE, loc.longitude);
                writableDb.insert(DbSchema.TrackPoints.NAME, null, values);
            }
        }
    }
    public List<Track> getAllTracks() {
        List<Track> result = new ArrayList<>();
        Cursor cursor = readableDb.query(DbSchema.TrackTab.NAME, null, null,
                null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result.add(new Track(
                    getTrackCoordinates(cursor.getInt(cursor.getColumnIndex("_id"))),
                    cursor.getLong(cursor.getColumnIndex(DbSchema.TrackTab.Cols.DATE)),
                    cursor.getString(cursor.getColumnIndex(DbSchema.TrackTab.Cols.T_TITLE))));
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }
    public List<LatLng> getTrackCoordinates(int id) {
        List<LatLng> result = new ArrayList<>();
        Cursor cursor = readableDb.query(DbSchema.TrackPoints.NAME, null,
                DbSchema.TrackPoints.Cols.FOREIGN_KEY + " = " + id,
                null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (cursor.getInt(cursor.getColumnIndex(DbSchema.TrackPoints.Cols.FOREIGN_KEY)) == id) {
                result.add(new LatLng(
                        cursor.getDouble(
                                cursor.getColumnIndex(DbSchema.TrackPoints.Cols.T_LATITUDE)),
                        cursor.getDouble(
                                cursor.getColumnIndex(DbSchema.TrackPoints.Cols.T_LONGITUDE))));
            }
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }
    public void renamePlace(int id, String newName) {
        ContentValues values = new ContentValues();
        values.put(DbSchema.LocTab.Cols.TITLE, newName);
        writableDb.update(DbSchema.LocTab.NAME, values, "_id = " + id, null);
    }
    public void renameTrack(int id, String newName) {
        ContentValues values = new ContentValues();
        values.put(DbSchema.TrackTab.Cols.T_TITLE, newName);
        writableDb.update(DbSchema.TrackTab.NAME, values, "_id = " + id, null);
    }
    public void deletePlace(int id) {
        writableDb.delete(DbSchema.LocTab.NAME, "_id = " + id, null);
    }
    public void deleteTrack(int id) {
        writableDb.delete(DbSchema.TrackTab.NAME, "_id = " + id, null);
    }
}
