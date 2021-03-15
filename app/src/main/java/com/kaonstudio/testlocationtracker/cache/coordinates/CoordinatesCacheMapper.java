package com.kaonstudio.testlocationtracker.cache.coordinates;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.kaonstudio.testlocationtracker.domain.CoordinatesDomain;
import com.kaonstudio.testlocationtracker.utils.EntityMapper;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class CoordinatesCacheMapper implements EntityMapper<CoordinatesCache, CoordinatesDomain> {

    @Inject
    CoordinatesCacheMapper() {

    }

    @Override
    public CoordinatesDomain mapFromEntity(CoordinatesCache coordinatesCache) {
        return new CoordinatesDomain(
                coordinatesCache.id,
                coordinatesCache.latitude,
                coordinatesCache.longitude,
                coordinatesCache.timeInMillis
        );
    }

    @Override
    public CoordinatesCache mapToEntity(CoordinatesDomain coordinatesDomain) {
        return new CoordinatesCache(
                coordinatesDomain.id,
                coordinatesDomain.latitude,
                coordinatesDomain.longitude,
                coordinatesDomain.timeInMillis
        );
    }

    public LatLng mapEntityToLatLng(CoordinatesCache coordinatesCache) {
        return new LatLng(coordinatesCache.latitude, coordinatesCache.longitude);
    }

    public LatLng mapDomainToLatLng(CoordinatesDomain coordinatesDomain) {
        return new LatLng(coordinatesDomain.latitude, coordinatesDomain.longitude);
    }

    public CoordinatesCache mapLocationToEntity(Location location) {
        return new CoordinatesCache(
                location.getLatitude(),
                location.getLongitude(),
                location.getTime()
        );
    }

    public List<CoordinatesDomain> mapFromEntityList(List<CoordinatesCache> entities) {
        return entities.stream()
                .map(this::mapFromEntity)
                .collect(Collectors.toList());
    }

    public List<CoordinatesCache> mapToEntityList(List<CoordinatesDomain> entities) {
        return entities.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
    }

    public List<LatLng> mapEntityListToLatLngList(List<CoordinatesCache> entities) {
        return entities.stream()
                .map(this::mapEntityToLatLng)
                .collect(Collectors.toList());
    }

    public List<LatLng> mapDomainListToLatLngList(List<CoordinatesDomain> list) {
        return list.stream()
                .map(this::mapDomainToLatLng)
                .collect(Collectors.toList());
    }

    public List<CoordinatesCache> mapLocationsToEntityList(List<Location> locations) {
        return locations.stream()
                .map(this::mapLocationToEntity)
                .collect(Collectors.toList());
    }
}
