package tech.thatgravyboat.goodall.common.registry.forge;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import tech.thatgravyboat.goodall.Goodall;

import java.util.function.Supplier;

public class ModEntitiesImpl {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Goodall.MOD_ID);

    public static <T extends Entity> Supplier<EntityType<T>> register(String id, EntityType.EntityFactory<T> factory, SpawnGroup group, float height, float width) {
        return ENTITIES.register(id, () -> EntityType.Builder.create(factory, group).setDimensions(width, height).build(id));
    }
}
