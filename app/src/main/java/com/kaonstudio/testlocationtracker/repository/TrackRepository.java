package com.kaonstudio.testlocationtracker.repository;

import com.kaonstudio.testlocationtracker.cache.track.TrackCache;
import com.kaonstudio.testlocationtracker.cache.track.TrackDatabase;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class TrackRepository {

    private TrackDatabase trackDatabase;

    public TrackRepository(TrackDatabase trackDatabase) {
        this.trackDatabase = trackDatabase;
    }

    public Completable insert(TrackCache trackCache) {
        return trackDatabase.trackDao().insert(trackCache);
    }

    public Completable deleteTracks() {
        return trackDatabase.trackDao().deleteAll();
    }

    public Flowable<List<TrackCache>> getTracks() {
        return trackDatabase.trackDao().getTracks();
    }
}
