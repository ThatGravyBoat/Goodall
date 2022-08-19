package tech.thatgravyboat.goodall.common.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import tech.thatgravyboat.goodall.common.entity.*;

import java.util.function.Supplier;

public class ModEntities {

    public static final Supplier<EntityType<RhinoEntity>> RHINO = register("rhino", RhinoEntity::new, MobCategory.CREATURE, 1.65F, 1.65F);
    public static final Supplier<EntityType<DumboEntity>> DUMBO = register("dumbo_octopus", DumboEntity::new, MobCategory.WATER_AMBIENT, 0.9F, 0.9F);
    public static final Supplier<EntityType<PelicanEntity>> PELICAN = register("pelican", PelicanEntity::new, MobCategory.CREATURE, 0.75F, 0.75F);
    public static final Supplier<EntityType<FennecFoxEntity>> FENNEC_FOX = register("fennec_fox", FennecFoxEntity::new, MobCategory.CREATURE, 0.75F, 1F);
    public static final Supplier<EntityType<KiwiEntity>> KIWI = register("kiwi", KiwiEntity::new, MobCategory.CREATURE, 0.75F, 0.75F);
    public static final Supplier<EntityType<ManateeEntity>> MANATEE = register("manatee", ManateeEntity::new, MobCategory.WATER_AMBIENT, 1.2F, 1.2F);
    public static final Supplier<EntityType<SealEntity>> SEAL = register("seal", SealEntity::new, MobCategory.WATER_AMBIENT, 1.2F, 1.2F);
    public static final Supplier<EntityType<DeerEntity>> DEER = register("deer", DeerEntity::new, MobCategory.CREATURE, 1.2F, 1.2F);
    public static final Supplier<EntityType<FlamingoEntity>> FLAMINGO = register("flamingo", FlamingoEntity::new, MobCategory.WATER_AMBIENT, 1.8F, 0.9F);
    public static final Supplier<EntityType<SongbirdEntity>> SONGBIRD = register("songbird", SongbirdEntity::new, MobCategory.CREATURE, 0.5F, 0.5F);
    public static final Supplier<EntityType<ToucanEntity>> TOUCAN = register("toucan", ToucanEntity::new, MobCategory.CREATURE, 0.9F, 0.9F);
    public static final Supplier<EntityType<TortoiseEntity>> TORTOISE = register("tortoise", TortoiseEntity::new, MobCategory.CREATURE, 1.2F, 1.5F);
    public static final Supplier<EntityType<KrillEntity>> KRILL = register("krill", KrillEntity::new, MobCategory.WATER_AMBIENT, 1F, 1F);

    public static void register() {
        //Initialize Class
    }

    @ExpectPlatform
    public static <T extends Entity> Supplier<EntityType<T>> register(String id, EntityType.EntityFactory<T> factory, MobCategory group, float height, float width) {
        throw new AssertionError();
    }


}
