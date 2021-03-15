package com.kaonstudio.testlocationtracker.cache.track;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;
import com.kaonstudio.testlocationtracker.cache.coordinates.CoordinatesCache;
import com.kaonstudio.testlocationtracker.domain.CoordinatesDomain;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@Entity(tableName = "tracks_db")
public class TrackCache {
    @PrimaryKey(autoGenerate = false)
    @NotNull
    public String name;
    public List<CoordinatesDomain> coordinates;
    public long date;

    public TrackCache(@NotNull String name, List<CoordinatesDomain> coordinates, long date) {
        this.name = name;
        this.coordinates = coordinates;
        this.date = date;
    }
}
