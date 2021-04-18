package com.kaonstudio.testlocationtracker.ui.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kaonstudio.testlocationtracker.R;
import com.kaonstudio.testlocationtracker.cache.coordinates.CoordinatesCache;
import com.kaonstudio.testlocationtracker.cache.coordinates.CoordinatesCacheMapper;
import com.kaonstudio.testlocationtracker.cache.track.TrackCache;
import com.kaonstudio.testlocationtracker.databinding.FragmentMapBinding;
import com.kaonstudio.testlocationtracker.domain.CoordinatesDomain;
import com.kaonstudio.testlocationtracker.domain.TrackDomain;
import com.kaonstudio.testlocationtracker.services.TrackingService;
import com.kaonstudio.testlocationtracker.ui.MainViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@AndroidEntryPoint
public class MapFragment extends Fragment implements CustomBottomSheet.OnClickListener {

    private FragmentMapBinding binding;
    private GoogleMap map;
    private CompositeDisposable compositeDisposable;
    private boolean isFragmentFirstLaunch = true;
    private boolean isTracking = false;
    private NavBackStackEntry navBackStackEntry;
    private LifecycleEventObserver observer;
    MainViewModel viewModel;

    @Inject
    CoordinatesCacheMapper cacheMapper;

    public final static int LOCATION_PERMISSION_REQUEST_CDDE = 99;
    public final static int DEFAULT_UPDATE_INTERVAL = 5;
    public final static int FAST_UPDATE_INTERVAL = 5;
    public final static String SAVE_TO_DATABASE_KEY = "save_to_db_key";
    private final static int POLYLINE_COLOR = Color.RED;
    private final static float POLYLINE_WIDTH = 16f;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(googleMap -> {
            map = googleMap;
            map.setOnMapClickListener(latLng -> binding.fragmentMapBottomSheet.hide());
            observeTrackingServiceData();
            observeCoordinateData();
            observeIsTracking();
        });
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        binding.fragmentMapBottomSheet.setListener(this);
        compositeDisposable = new CompositeDisposable();
        initOnBackStackObserver();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.mapView.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        binding.mapView.onDestroy();
        map = null;
        compositeDisposable.clear();
        binding = null;
        navBackStackEntry.getLifecycle().removeObserver(observer);
        super.onDestroyView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CDDE) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(requireContext(), getString(R.string.fragment_map_permission_denied), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }

    private void initOnBackStackObserver() {
        NavController navController = NavHostFragment.findNavController(this);
        navBackStackEntry = navController.getBackStackEntry(R.id.mapFragment);
         observer = (source, event) -> {
             if (event.equals(Lifecycle.Event.ON_RESUME)
                     && navBackStackEntry.getSavedStateHandle().contains(SAVE_TO_DATABASE_KEY)) {
                 final String result = Objects.requireNonNull(navBackStackEntry.getSavedStateHandle().get(SAVE_TO_DATABASE_KEY));
                 viewModel.getCoordinatesOnce()
                         .subscribeOn(Schedulers.io())
                         .observeOn(AndroidSchedulers.mainThread())
                         .subscribe(new SingleObserver<List<CoordinatesCache>>() {
                             @Override
                             public void onSubscribe(@NotNull Disposable d) {
                                 compositeDisposable.add(d);
                             }

                             @Override
                             public void onSuccess(@NotNull List<CoordinatesCache> coordinatesCaches) {
                                 final TrackCache track = new TrackCache(result, cacheMapper.mapFromEntityList(coordinatesCaches), coordinatesCaches.get(0).getTimeInMillis());
                                 viewModel.insertTrack(track);
                                 Toast.makeText(requireContext(), getString(R.string.dialog_save_to_db_success_toast), Toast.LENGTH_SHORT).show();
                             }

                             @Override
                             public void onError(@NotNull Throwable e) {

                             }
                         });
                 Objects.requireNonNull(navController.getCurrentBackStackEntry()).getSavedStateHandle().remove(SAVE_TO_DATABASE_KEY);
             }
         };
        navBackStackEntry.getLifecycle().addObserver(observer);
    }

    private void observeIsTracking() {
        viewModel.observeIsTracking().observe(getViewLifecycleOwner(), aBoolean -> isTracking = aBoolean);
    }

    private void observeTrackingServiceData() {
        TrackingService.coordinates.observe(getViewLifecycleOwner(), coordinatesCaches -> viewModel.insertCoordinatesList(coordinatesCaches));
    }

    private void observeCoordinateData() {
        viewModel.observeCoordinates().observe(getViewLifecycleOwner(), listDataState -> {
            if (listDataState instanceof DataState.Success) {
                List<CoordinatesDomain> coordinatesDomains = listDataState.getData();
                if (coordinatesDomains != null && !coordinatesDomains.isEmpty()) {
                    binding.fragmentMapBottomSheet.setData(coordinatesDomains);
                    CoordinatesDomain coordinatesDomain = listDataState.getData().get(listDataState.getData().size() - 1);
                    LatLng latLng = new LatLng(coordinatesDomain.latitude, coordinatesDomain.longitude);
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
                    final int listSize = coordinatesDomains.size();
                    if (!isFragmentFirstLaunch && listSize > 1) {
                        updatePolyline(coordinatesDomains, listSize);
                    } else if (isFragmentFirstLaunch && listSize > 1) {
                        drawAllPolylines(cacheMapper.mapDomainListToLatLngList(coordinatesDomains));
                    }
                    isFragmentFirstLaunch = false;
                }
            } else if (listDataState instanceof DataState.Error) {
                binding.fragmentMapBottomSheet.setData(new ArrayList<>());
            }
        });
    }

    private void drawAllPolylines(List<LatLng> coordinates) {
        final PolylineOptions options = new PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(coordinates);
        map.addPolyline(options);
    }

    private void updatePolyline(List<CoordinatesDomain> coordinates, int listSize) {
        final CoordinatesDomain lastItem = coordinates.get(listSize - 1);
        final CoordinatesDomain preLastItem = coordinates.get(listSize - 2);
        if (lastItem != preLastItem) {
            final PolylineOptions options = new PolylineOptions()
                    .color(POLYLINE_COLOR)
                    .width(POLYLINE_WIDTH)
                    .add(new LatLng(preLastItem.latitude, preLastItem.longitude))
                    .add(new LatLng(lastItem.latitude, lastItem.longitude));
            map.addPolyline(options);
        }
    }

    private boolean checkLocationPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) || !checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MapFragment.LOCATION_PERMISSION_REQUEST_CDDE);
                return false;
            } else return true;
        }
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION) &&
                    checkPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            ) return true;
            else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, MapFragment.LOCATION_PERMISSION_REQUEST_CDDE);
                return false;
            }
        }
        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) || !checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MapFragment.LOCATION_PERMISSION_REQUEST_CDDE);
            return false;
        }
        if (!checkPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.dialog_location_permission_title))
                    .setPositiveButton(getString(R.string.dialog_location_permission_allow), (dialog, which) -> requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, MapFragment.LOCATION_PERMISSION_REQUEST_CDDE))
                    .setNegativeButton(getString(R.string.dialog_location_permission_deny), (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
            return false;
        }
        return true;
    }

    private boolean checkPermission(String permission) {
        return ActivityCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void dispatchTrackingServiceCommand(String command) {
        Intent intent = new Intent(requireContext(), TrackingService.class);
        intent.setAction(command);
        requireContext().startService(intent);
    }

    @Override
    public void onStartClick() {
        final boolean isGranted = checkLocationPermissions();
        if (isGranted) {
            binding.fragmentMapBottomSheet.onStartButtonCallback();
            viewModel.deleteAllCoordinates();
            map.clear();
            Toast.makeText(requireContext(), getString(R.string.fragment_map_started_toast), Toast.LENGTH_SHORT).show();
            viewModel.setIsTracking(true);
            dispatchTrackingServiceCommand(TrackingService.ACTION_START);
        }
    }

    @Override
    public void onStopClick() {
        binding.fragmentMapBottomSheet.onStopButtonCallback();
        dispatchTrackingServiceCommand(TrackingService.ACTION_STOP);
        showSaveToDatabaseDialog();
        viewModel.setIsTracking(false);
    }

    @Override
    public void onClearClick() {
        Toast.makeText(requireContext(), getString(R.string.fragment_map_cleared_toast), Toast.LENGTH_SHORT).show();
        viewModel.deleteAllCoordinates();
        map.clear();
    }

    private void showSaveToDatabaseDialog() {
        NavHostFragment.findNavController(this).navigate(R.id.saveToDatabaseDialog);
    }

}
