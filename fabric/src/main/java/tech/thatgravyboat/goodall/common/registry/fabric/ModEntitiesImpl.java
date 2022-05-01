package tech.thatgravyboat.goodall.common.registry.fabric;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModEntitiesImpl {

    public static final Map<String, Supplier<EntityType<?>>> ENTITIES = new LinkedHashMap<>();

    public static <T extends Entity> Supplier<EntityType<T>> register(String id, EntityType.EntityFactory<T> factory, SpawnGroup group, float height, float width) {
        var object = FabricEntityTypeBuilder.create(group, factory).dimensions(EntityDimensions.fixed(width, height)).build();
        ENTITIES.put(id, () -> object);
        return () -> object;
    }
}
