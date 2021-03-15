package com.kaonstudio.testlocationtracker.cache.coordinates;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;


@Dao
public interface CoordinatesDao {

    @Query("DELETE FROM coordinates")
    Completable deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(CoordinatesCache coordinates);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertList(List<CoordinatesCache> coordinatesList);

    @Query("SELECT * FROM coordinates")
    Flowable<List<CoordinatesCache>> getObservableCoordinates();

    @Query("SELECT * FROM coordinates")
    Single<List<CoordinatesCache>> getCoordinatesOnce();

}
