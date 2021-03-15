package com.kaonstudio.testlocationtracker.di;

import android.content.Context;

import androidx.room.Room;

import com.kaonstudio.testlocationtracker.cache.coordinates.CoordinatesDao;
import com.kaonstudio.testlocationtracker.cache.coordinates.CoordinatesDatabase;
import com.kaonstudio.testlocationtracker.cache.track.TrackDao;
import com.kaonstudio.testlocationtracker.cache.track.TrackDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class CacheModule {

    @Singleton
    @Provides
    public CoordinatesDatabase provideCoordinatesDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(
                context,
                CoordinatesDatabase.class,
                CoordinatesDatabase.COORDINATES_DATABASE_NAME
        )
                .fallbackToDestructiveMigration()
                .build();
    }

    @Singleton
    @Provides
    public CoordinatesDao provideCoordinatesDao(CoordinatesDatabase database) {
        return database.coordinatesDao();
    }

    @Singleton
    @Provides
    public TrackDatabase provideTrackDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(
                context,
                TrackDatabase.class,
                TrackDatabase.TRACK_DATABASE_NAME
        )
                .fallbackToDestructiveMigration()
                .build();
    }

    @Singleton
    @Provides
    public TrackDao provideTrackDao(TrackDatabase trackDatabase) {
        return trackDatabase.trackDao();
    }
}
