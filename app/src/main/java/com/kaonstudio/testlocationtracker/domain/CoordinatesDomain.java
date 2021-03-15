package com.kaonstudio.testlocationtracker.domain;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import java.util.Objects;

public class CoordinatesDomain {

    public CoordinatesDomain(int id, double latitude, double longitude, long date) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        timeInMillis = date;
    }

    public int id;
    public double latitude;
    public long timeInMillis;

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

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

    public double longitude;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoordinatesDomain that = (CoordinatesDomain) o;
        return Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, latitude, longitude);
    }

}
