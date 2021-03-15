package com.kaonstudio.testlocationtracker.domain;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Objects;

public class TrackDomain {
    public String name;

    public TrackDomain(String name, List<CoordinatesDomain> coordinates, long date) {
        this.name = name;
        this.coordinates = coordinates;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CoordinatesDomain> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<CoordinatesDomain> coordinates) {
        this.coordinates = coordinates;
    }

    public List<CoordinatesDomain> coordinates;
    public long date;

    @Override
    public int hashCode() {
        return Objects.hash(name, coordinates);
    }
}
