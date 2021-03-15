package com.kaonstudio.testlocationtracker.utils;

import androidx.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaonstudio.testlocationtracker.domain.CoordinatesDomain;

import java.util.List;

public class Converters {
    @TypeConverter
    public String latLngListToJson(List<LatLng> value) {
        return new Gson().toJson(value);
    }

    @TypeConverter
    public List<LatLng> jsonToLatLngList(String value) {
        return new Gson().fromJson(value, new TypeToken<List<LatLng>>() {
        }.getType());
    }

    @TypeConverter
    public String coordinatesListToJson(List<CoordinatesDomain> value) {
        return new Gson().toJson(value);
    }

    @TypeConverter
    public List<CoordinatesDomain> jsonToCoordinatesList(String value) {
        return new Gson().fromJson(value, new TypeToken<List<CoordinatesDomain>>() {
        }.getType());
    }

    public String objectToJson(LatLng value) {
        return new Gson().toJson(value);
    }

    public LatLng jsonToObject(String value) {
        return new Gson().fromJson(value, new TypeToken<LatLng>(){}.getType());
    }
}
