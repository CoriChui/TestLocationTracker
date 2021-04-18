package com.kaonstudio.testlocationtracker.di;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.kaonstudio.testlocationtracker.MainActivity;
import com.kaonstudio.testlocationtracker.R;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ServiceComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.FragmentScoped;
import dagger.hilt.android.scopes.ServiceScoped;

import static com.kaonstudio.testlocationtracker.services.TrackingService.NAVIGATE_TO_TRACKING_FRAGMENT;
import static com.kaonstudio.testlocationtracker.services.TrackingService.NOTIFICATION_CHANNEL_ID;
import static com.kaonstudio.testlocationtracker.ui.map.MapFragment.DEFAULT_UPDATE_INTERVAL;
import static com.kaonstudio.testlocationtracker.ui.map.MapFragment.FAST_UPDATE_INTERVAL;

@InstallIn(ServiceComponent.class)
@Module
public class ServiceModule {

    @ServiceScoped
    @Provides
    public PendingIntent provideActivityPendingIntent(@ApplicationContext Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(NAVIGATE_TO_TRACKING_FRAGMENT);
        return PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    @ServiceScoped
    @Provides
    public Notification provideNotificationBuilder(@ApplicationContext Context context, PendingIntent pendingIntent) {
        return new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_track_small_icon)
                .setContentTitle(context.getString(R.string.service_title))
                .setContentIntent(pendingIntent)
                .build();
    }

    @ServiceScoped
    @Provides
    public FusedLocationProviderClient provideFusedLocationProviderClient(@ApplicationContext Context context) {
        return LocationServices.getFusedLocationProviderClient(context);
    }

    @ServiceScoped
    @Provides
    public LocationRequest provideLocationRequest() {
        return new LocationRequest()
                .setInterval(1000 * DEFAULT_UPDATE_INTERVAL)
                .setFastestInterval(1000 * FAST_UPDATE_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
}
