package com.kaonstudio.testlocationtracker.cache.track;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.kaonstudio.testlocationtracker.utils.Converters;

@Database(entities = {TrackCache.class}, version = 3)
@TypeConverters(Converters.class)
public abstract class TrackDatabase extends RoomDatabase {

    public final static String TRACK_DATABASE_NAME = "tracks_db";

    public abstract TrackDao trackDao();

}
