package com.kaonstudio.testlocationtracker.cache.coordinates;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "coordinates")
public class CoordinatesCache {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;

    @ColumnInfo(name = "timeInMillis")
    public long timeInMillis;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public CoordinatesCache(int id, double latitude, double longitude, long timeInMillis) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeInMillis = timeInMillis;
    }

    @Ignore
    public CoordinatesCache(double latitude, double longitude, long date) {
        this.latitude = latitude;
        this.longitude = longitude;
        timeInMillis = date;
    }
}
