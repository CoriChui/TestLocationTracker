package com.kaonstudio.testlocationtracker.cache.coordinates;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.kaonstudio.testlocationtracker.utils.Converters;

@Database(entities = {CoordinatesCache.class}, version = 4, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class CoordinatesDatabase extends RoomDatabase {

    public final static String COORDINATES_DATABASE_NAME = "coordinates_db";

    public abstract CoordinatesDao coordinatesDao();

}
