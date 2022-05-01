package tech.thatgravyboat.goodall.common.registry.fabric;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.mixin.object.builder.SpawnRestrictionAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import tech.thatgravyboat.goodall.common.registry.SpawnData;

public class ModSpawnsImpl {
    public static void registerSpawn(Biome.Category category, SpawnData data) {
        BiomeModifications.addSpawn(BiomeSelectors.categories(category), data.group(), data.entityType(), data.weight(), data.min(), data.max());
    }

    public static void registerSpawn(RegistryKey<Biome> biome, SpawnData data) {
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(biome), data.group(), data.entityType(), data.weight(), data.min(), data.max());
    }

    public static <T extends MobEntity> void setSpawnRules(EntityType<T> entityType, SpawnRestriction.Location location, Heightmap.Type type, SpawnRestriction.SpawnPredicate<T> predicate) {
        SpawnRestrictionAccessor.callRegister(entityType, location, type, predicate);
    }
}
