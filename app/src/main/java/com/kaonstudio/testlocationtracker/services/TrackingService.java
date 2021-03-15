package com.kaonstudio.testlocationtracker.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.kaonstudio.testlocationtracker.cache.coordinates.CoordinatesCache;
import com.kaonstudio.testlocationtracker.cache.coordinates.CoordinatesCacheMapper;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TrackingService extends LifecycleService {

    public final static String ACTION_START = "actionStart";
    public final static String ACTION_STOP = "actionStop";
    public final static String NAVIGATE_TO_TRACKING_FRAGMENT = "NAVIGATE_TO_TRACKING_FRAGMENT";
    public final static String NOTIFICATION_CHANNEL_ID = "channelId";
    public final static String NOTIFICATION_CHANNEL_NAME = "channelName";
    public static MutableLiveData<List<CoordinatesCache>> coordinates = new MutableLiveData<>();
    public final static int NOTIFICATION_ID = 1;
    private LocationCallbackReference locationCallbackReference;


    @Inject
    FusedLocationProviderClient fusedLocationProviderClient;
    @Inject
    LocationRequest locationRequest;
    @Inject
    CoordinatesCacheMapper cacheMapper;
    @Inject
    Notification notification;

    @Override
    public void onCreate() {
        super.onCreate();
        coordinates.setValue(new ArrayList<>());
        initLocationCallback();
    }

    @Override
    public int onStartCommand(@Nullable @org.jetbrains.annotations.Nullable Intent intent, int flags, int startId) {
        if (intent != null) {
            switch (intent.getAction()) {
                case ACTION_START:
                    startForegroundService();
                    requestLocationUpdates();
                    break;
                case ACTION_STOP:
                    removeLocationUpdates();
                    stopForegroundService();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private void startForegroundService() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(manager);
        }
        startForeground(NOTIFICATION_ID, notification);
    }

    private void stopForegroundService() {
        coordinates.setValue(new ArrayList<>());
        stopForeground(true);
        stopSelf();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(NotificationManager manager) {
        NotificationChannel channel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
        );
        manager.createNotificationChannel(channel);
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallbackReference, Looper.getMainLooper());
    }

    private void initLocationCallback() {
        locationCallbackReference = new LocationCallbackReference(new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                final List<CoordinatesCache> coordinateCaches = cacheMapper.mapLocationsToEntityList(locationResult.getLocations());
                coordinates.setValue(coordinateCaches);
            }
        });
    }

    private void removeLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallbackReference);
        locationCallbackReference.mLocationCallbackRef.clear();
        locationCallbackReference = null;
    }

    static class LocationCallbackReference extends LocationCallback {

        private final SoftReference<LocationCallback> mLocationCallbackRef;

        public LocationCallbackReference(LocationCallback locationCallback) {
            mLocationCallbackRef = new SoftReference<>(locationCallback);
        }

        @Override
        public void onLocationResult(@NotNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (mLocationCallbackRef.get() != null) {
                mLocationCallbackRef.get().onLocationResult(locationResult);
            }
        }

        @Override
        public void onLocationAvailability(@NotNull LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            if (mLocationCallbackRef.get() != null) {
                mLocationCallbackRef.get().onLocationAvailability(locationAvailability);
            }
        }
    }
}


