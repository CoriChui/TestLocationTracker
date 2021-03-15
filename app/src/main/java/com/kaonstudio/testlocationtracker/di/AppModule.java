package com.kaonstudio.testlocationtracker.di;

import com.kaonstudio.testlocationtracker.cache.coordinates.CoordinatesDatabase;
import com.kaonstudio.testlocationtracker.cache.track.TrackDatabase;
import com.kaonstudio.testlocationtracker.repository.CoordinatesRepository;
import com.kaonstudio.testlocationtracker.repository.TrackRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class AppModule {

    @Singleton
    @Provides
    public CoordinatesRepository provideCoordinatesRepository(CoordinatesDatabase database) {
        return new CoordinatesRepository(database);
    }

    @Singleton
    @Provides
    public TrackRepository provideTrackRepository(TrackDatabase trackDatabase) {
        return new TrackRepository(trackDatabase);
    }
}
