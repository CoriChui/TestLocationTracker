package com.kaonstudio.testlocationtracker.repository;

import com.kaonstudio.testlocationtracker.cache.coordinates.CoordinatesCache;
import com.kaonstudio.testlocationtracker.cache.coordinates.CoordinatesDatabase;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;


public class CoordinatesRepository {

    private CoordinatesDatabase database;

    public CoordinatesRepository(CoordinatesDatabase database) {
        this.database = database;
    }

    public Flowable<List<CoordinatesCache>> getObservableCoordinates() {
        return database.coordinatesDao().getObservableCoordinates();
    }

    public Single<List<CoordinatesCache>> getCoordinatesOnce() {
        return database.coordinatesDao().getCoordinatesOnce();
    }

    public Completable insert(CoordinatesCache coordinates) {
        return database.coordinatesDao().insert(coordinates);
    }

    public Completable insertList(List<CoordinatesCache> coordinatesList) {
        return database.coordinatesDao().insertList(coordinatesList);
    }

    public Completable deleteAll() {
        return database.coordinatesDao().deleteAll();
    }
}
