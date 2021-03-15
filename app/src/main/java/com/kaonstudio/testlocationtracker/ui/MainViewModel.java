package com.kaonstudio.testlocationtracker.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.kaonstudio.testlocationtracker.cache.coordinates.CoordinatesCacheMapper;
import com.kaonstudio.testlocationtracker.cache.coordinates.CoordinatesCache;
import com.kaonstudio.testlocationtracker.cache.track.TrackCache;
import com.kaonstudio.testlocationtracker.cache.track.TrackCacheMapper;
import com.kaonstudio.testlocationtracker.domain.CoordinatesDomain;
import com.kaonstudio.testlocationtracker.domain.TrackDomain;
import com.kaonstudio.testlocationtracker.repository.CoordinatesRepository;
import com.kaonstudio.testlocationtracker.repository.TrackRepository;
import com.kaonstudio.testlocationtracker.ui.map.DataState;
import com.kaonstudio.testlocationtracker.utils.SingleEvent;

import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.scopes.FragmentScoped;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class MainViewModel extends ViewModel {

    private final CoordinatesRepository coordinatesRepository;
    private final TrackRepository trackRepository;
    private final SavedStateHandle savedStateHandle;
    private final CoordinatesCacheMapper coordinatesCacheMapper;
    private final TrackCacheMapper trackCacheMapper;
    private final MediatorLiveData<DataState<List<CoordinatesDomain>>> coordinatesLiveData;
    private final MediatorLiveData<DataState<List<TrackDomain>>> tracksLiveData;
    private final MutableLiveData<Boolean> isTracking;
    private final CompositeDisposable disposable;

    @Inject
    MainViewModel(
            CoordinatesRepository coordinatesRepository,
            TrackRepository trackRepository,
            SavedStateHandle savedStateHandle,
            CoordinatesCacheMapper coordinatesCacheMapper,
            TrackCacheMapper trackCacheMapper
    ) {
        this.coordinatesRepository = coordinatesRepository;
        this.trackRepository = trackRepository;
        this.savedStateHandle = savedStateHandle;
        this.coordinatesCacheMapper = coordinatesCacheMapper;
        this.trackCacheMapper = trackCacheMapper;
        disposable = new CompositeDisposable();
        coordinatesLiveData = new MediatorLiveData<>();
        tracksLiveData = new MediatorLiveData<>();
        isTracking = new MutableLiveData<>();
    }

    public void setIsTracking(boolean tracking) {
        isTracking.setValue(tracking);
    }

    public LiveData<Boolean> observeIsTracking() {
        return isTracking;
    }

    public LiveData<DataState<List<TrackDomain>>> observeTracks() {
        tracksLiveData.setValue(new DataState.Loading<>());
        LiveData<DataState<List<TrackDomain>>> source = LiveDataReactiveStreams.fromPublisher(
                trackRepository.getTracks()
                        .onErrorReturn(throwable -> new ArrayList<>())
                        .map(trackCacheList -> {
                            if (trackCacheList.isEmpty())
                                return new DataState.Error<List<TrackDomain>>("Caching Error");
                            else
                                return new DataState.Success<>(trackCacheMapper.mapFromEntityList(trackCacheList));
                        })
                        .subscribeOn(Schedulers.io()));
        tracksLiveData.addSource(source, listDataState -> {
            tracksLiveData.setValue(listDataState);
            tracksLiveData.removeSource(coordinatesLiveData);
        });
        return tracksLiveData;
    }

    public LiveData<DataState<List<CoordinatesDomain>>> observeCoordinates() {
        coordinatesLiveData.setValue(new DataState.Loading<>());
        LiveData<DataState<List<CoordinatesDomain>>> source = LiveDataReactiveStreams.fromPublisher(
                coordinatesRepository.getObservableCoordinates()
                        .onErrorReturn(throwable -> new ArrayList<>())
                        .map(coordinatesCacheList -> {
                            if (coordinatesCacheList.isEmpty())
                                return new DataState.Error<List<CoordinatesDomain>>("Caching Error");
                            else
                                return new DataState.Success<>(coordinatesCacheMapper.mapFromEntityList(coordinatesCacheList));
                        })
                        .subscribeOn(Schedulers.io()));
        coordinatesLiveData.addSource(source, listDataState -> {
            coordinatesLiveData.setValue(listDataState);
            coordinatesLiveData.removeSource(coordinatesLiveData);
        });
        return coordinatesLiveData;
    }

    public Single<List<CoordinatesCache>> getCoordinatesOnce() {
        return coordinatesRepository.getCoordinatesOnce();
    }

    public void insertCoordinatesList(List<CoordinatesCache> list) {
        coordinatesRepository.insertList(list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(@NotNull Throwable e) {

                    }
                });
    }

    public void insertCoordinateItem(CoordinatesCache coordinates) {
        coordinatesRepository.insert(coordinates)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(@NotNull Throwable e) {

                    }
                });
    }

    public void deleteAllCoordinates() {
        coordinatesRepository.deleteAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(@NotNull Throwable e) {

                    }
                });
    }

    public void insertTrack(TrackCache trackCache) {
        trackRepository.insert(trackCache)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(@NotNull Throwable e) {

                    }
                });
    }

    public void deleteAllTracks() {
        trackRepository.deleteTracks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(@NotNull Throwable e) {

                    }
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
