package com.kaonstudio.testlocationtracker.cache.track;

import com.kaonstudio.testlocationtracker.domain.TrackDomain;
import com.kaonstudio.testlocationtracker.utils.EntityMapper;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class TrackCacheMapper implements EntityMapper<TrackCache, TrackDomain> {

    @Inject
    TrackCacheMapper() {

    }

    @Override
    public TrackDomain mapFromEntity(TrackCache trackCache) {
        return new TrackDomain(
                trackCache.name,
                trackCache.coordinates,
                trackCache.date
        );
    }

    @Override
    public TrackCache mapToEntity(TrackDomain trackDomain) {
        return new TrackCache(
                trackDomain.name,
                trackDomain.coordinates,
                trackDomain.date
        );
    }

    public List<TrackDomain> mapFromEntityList(List<TrackCache> entities) {
        return entities.stream()
                .map(this::mapFromEntity)
                .collect(Collectors.toList());
    }
}
