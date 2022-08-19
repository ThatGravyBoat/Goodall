package tech.thatgravyboat.goodall.common.registry.forge;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import tech.thatgravyboat.goodall.Goodall;

import java.util.function.Supplier;

public class ModEntitiesImpl {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Goodall.MOD_ID);

    public static <T extends Entity> Supplier<EntityType<T>> register(String id, EntityType.EntityFactory<T> factory, MobCategory group, float height, float width) {
        return ENTITIES.register(id, () -> EntityType.Builder.of(factory, group).sized(width, height).build(id));
    }
}
