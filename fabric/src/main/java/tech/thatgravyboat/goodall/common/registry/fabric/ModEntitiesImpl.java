package tech.thatgravyboat.goodall.common.registry.fabric;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModEntitiesImpl {

    public static final Map<String, Supplier<EntityType<?>>> ENTITIES = new LinkedHashMap<>();

    public static <T extends Entity> Supplier<EntityType<T>> register(String id, EntityType.EntityFactory<T> factory, MobCategory group, float height, float width) {
        var object = FabricEntityTypeBuilder.create(group, factory).dimensions(EntityDimensions.scalable(width, height)).build();
        ENTITIES.put(id, () -> object);
        return () -> object;
    }
}
