package com.kaonstudio.testlocationtracker.utils;

public interface EntityMapper<Entity, Domain> {

    public Domain mapFromEntity(Entity entity);

    public Entity mapToEntity(Domain domain);
}
