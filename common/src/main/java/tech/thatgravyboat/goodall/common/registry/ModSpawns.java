package tech.thatgravyboat.goodall.common.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.common.config.spawns.*;
import tech.thatgravyboat.goodall.common.entity.*;

public class ModSpawns {

    public static void registerSpawns() {
        Rhino rhino = Goodall.CONFIG.spawnConfig.rhino;
        registerSpawn(Biome.Category.SAVANNA, new SpawnData(ModEntities.RHINO.get(), rhino.weight, rhino.min, rhino.max));

        Dumbo dumbo = Goodall.CONFIG.spawnConfig.dumboOctopus;
        SpawnData dumboSpawnData = new SpawnData(ModEntities.DUMBO.get(), dumbo.weight, dumbo.min, dumbo.max);
        registerSpawn(BiomeKeys.DEEP_OCEAN, dumboSpawnData);
        registerSpawn(BiomeKeys.DEEP_COLD_OCEAN, dumboSpawnData);
        registerSpawn(BiomeKeys.DEEP_LUKEWARM_OCEAN, dumboSpawnData);

        Booby booby = Goodall.CONFIG.spawnConfig.blueFootedBooby;
        registerSpawn(Biome.Category.BEACH, new SpawnData(ModEntities.BOOBY.get(), booby.weight, booby.min, booby.max));

        FennecFox fennecFox = Goodall.CONFIG.spawnConfig.fennecFox;
        registerSpawn(Biome.Category.DESERT, new SpawnData(ModEntities.FENNEC_FOX.get(), fennecFox.weight, fennecFox.min, fennecFox.max));

        Kiwi kiwi = Goodall.CONFIG.spawnConfig.kiwi;
        registerSpawn(Biome.Category.JUNGLE, new SpawnData(ModEntities.KIWI.get(), kiwi.weight, kiwi.min, kiwi.max));

        Manatee manatee = Goodall.CONFIG.spawnConfig.manatee;
        registerSpawn(BiomeKeys.OCEAN, new SpawnData(ModEntities.MANATEE.get(), manatee.weight, manatee.min, manatee.max));

        Seal seal = Goodall.CONFIG.spawnConfig.seal;
        SpawnData sealSpawnData = new SpawnData(ModEntities.SEAL.get(), seal.weight, seal.min, seal.max);
        registerSpawn(BiomeKeys.STONY_SHORE, sealSpawnData);
        registerSpawn(Biome.Category.BEACH, sealSpawnData);
        registerSpawn(BiomeKeys.FROZEN_OCEAN, sealSpawnData);

        RedDeer redDeer = Goodall.CONFIG.spawnConfig.redDeer;
        registerSpawn(Biome.Category.FOREST, new SpawnData(ModEntities.RED_DEER.get(), redDeer.weight, redDeer.min, redDeer.max));

        WhiteDeer whiteTailedDeer = Goodall.CONFIG.spawnConfig.whiteTailedDeer;
        registerSpawn(Biome.Category.FOREST, new SpawnData(ModEntities.WHITE_DEER.get(), whiteTailedDeer.weight, whiteTailedDeer.min, whiteTailedDeer.max));
        registerSpawn(BiomeKeys.FLOWER_FOREST, new SpawnData(ModEntities.WHITE_DEER.get(), whiteTailedDeer.weight+5, whiteTailedDeer.min, whiteTailedDeer.max));

        Flamingo flamingo = Goodall.CONFIG.spawnConfig.flamingo;
        SpawnData flamingoSpawnData = new SpawnData(ModEntities.FLAMINGO.get(), flamingo.weight, flamingo.min, flamingo.max);
        registerSpawn(BiomeKeys.SWAMP, flamingoSpawnData);
        registerSpawn(BiomeKeys.JUNGLE, flamingoSpawnData);
    }

    public static void registerSpawnRules() {
        setSpawnRules(ModEntities.RHINO.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        setSpawnRules(ModEntities.DUMBO.get(), SpawnRestriction.Location.IN_WATER, Heightmap.Type.OCEAN_FLOOR, DumboEntity::canDumboSpawn);
        setSpawnRules(ModEntities.BOOBY.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, BoobyEntity::canMobSpawn);
        setSpawnRules(ModEntities.FENNEC_FOX.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, FennecFoxEntity::canMobSpawn);
        setSpawnRules(ModEntities.KIWI.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, KiwiEntity::canMobSpawn);
        setSpawnRules(ModEntities.MANATEE.get(), SpawnRestriction.Location.IN_WATER, Heightmap.Type.OCEAN_FLOOR, ManateeEntity::canManateeSpawn);
        setSpawnRules(ModEntities.FLAMINGO.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.WORLD_SURFACE, FlamingoEntity::canMobSpawn);
        setSpawnRules(ModEntities.RED_DEER.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, RedDeerEntity::canMobSpawn);
        setSpawnRules(ModEntities.WHITE_DEER.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WhiteDeerEntity::canMobSpawn);
        setSpawnRules(ModEntities.SEAL.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SealEntity::canMobSpawn);
    }


    @ExpectPlatform
    public static void registerSpawn(RegistryKey<Biome> biome, SpawnData data) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerSpawn(Biome.Category category, SpawnData data) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends MobEntity> void setSpawnRules(EntityType<T> entityType, SpawnRestriction.Location location, Heightmap.Type type, SpawnRestriction.SpawnPredicate<T> predicate) {
        throw new AssertionError();
    }
}
