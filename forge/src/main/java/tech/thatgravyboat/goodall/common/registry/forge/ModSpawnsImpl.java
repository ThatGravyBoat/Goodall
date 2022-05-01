package tech.thatgravyboat.goodall.common.registry.forge;

import com.google.common.collect.ArrayListMultimap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import tech.thatgravyboat.goodall.common.registry.SpawnData;

public class ModSpawnsImpl {

    public static final ArrayListMultimap<Biome.Category, SpawnData> CATEGORY_SPAWNS = ArrayListMultimap.create();
    public static final ArrayListMultimap<Identifier, SpawnData> BIOME_SPAWNS = ArrayListMultimap.create();

    public static void registerSpawn(Biome.Category category, SpawnData data) {
        CATEGORY_SPAWNS.put(category, data);
    }

    public static void registerSpawn(RegistryKey<Biome> biome, SpawnData data) {
        BIOME_SPAWNS.put(biome.getValue(), data);
    }


    public static <T extends MobEntity> void setSpawnRules(EntityType<T> entityType, SpawnRestriction.Location location, Heightmap.Type type, SpawnRestriction.SpawnPredicate<T> predicate) {
        SpawnRestriction.register(entityType, location, type, predicate);
    }
}
