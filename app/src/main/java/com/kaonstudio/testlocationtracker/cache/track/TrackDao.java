package com.kaonstudio.testlocationtracker.cache.track;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(TrackCache track);

    @Query("DELETE FROM tracks_db")
    Completable deleteAll();

    @Query("SELECT * FROM tracks_db")
    Flowable<List<TrackCache>> getTracks();

}
