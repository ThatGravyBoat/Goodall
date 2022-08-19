package tech.thatgravyboat.goodall.common.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;
import tech.thatgravyboat.goodall.common.entity.*;

public class ModSpawns {

    public static void registerSpawnRules() {
        setSpawnRules(ModEntities.RHINO.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        setSpawnRules(ModEntities.DUMBO.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.OCEAN_FLOOR, DumboEntity::canDumboSpawn);
        setSpawnRules(ModEntities.PELICAN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, PelicanEntity::checkMobSpawnRules);
        setSpawnRules(ModEntities.FENNEC_FOX.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FennecFoxEntity::checkMobSpawnRules);
        setSpawnRules(ModEntities.KIWI.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, KiwiEntity::checkMobSpawnRules);
        setSpawnRules(ModEntities.MANATEE.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.OCEAN_FLOOR, ManateeEntity::canManateeSpawn);
        setSpawnRules(ModEntities.FLAMINGO.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, FlamingoEntity::checkMobSpawnRules);
        setSpawnRules(ModEntities.DEER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DeerEntity::checkMobSpawnRules);
        setSpawnRules(ModEntities.SEAL.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SealEntity::checkMobSpawnRules);
        setSpawnRules(ModEntities.TOUCAN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, ToucanEntity::checkMobSpawnRules);
        setSpawnRules(ModEntities.SONGBIRD.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SongbirdEntity::checkMobSpawnRules);
        setSpawnRules(ModEntities.TORTOISE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TortoiseEntity::checkMobSpawnRules);
    }

    @ExpectPlatform
    public static <T extends Mob> void setSpawnRules(EntityType<T> entityType, SpawnPlacements.Type location, Heightmap.Types type, SpawnPlacements.SpawnPredicate<T> predicate) {
        throw new AssertionError();
    }
}
