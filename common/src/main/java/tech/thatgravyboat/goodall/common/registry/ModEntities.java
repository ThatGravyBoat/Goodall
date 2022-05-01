package tech.thatgravyboat.goodall.common.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import tech.thatgravyboat.goodall.common.entity.*;

import java.util.function.Supplier;

public class ModEntities {

    public static final Supplier<EntityType<RhinoEntity>> RHINO = register("rhino", RhinoEntity::new, SpawnGroup.CREATURE, 1.65F, 1.65F);
    public static final Supplier<EntityType<DumboEntity>> DUMBO = register("dumbo_octopus", DumboEntity::new, SpawnGroup.WATER_CREATURE, 0.9F, 0.9F);
    public static final Supplier<EntityType<BoobyEntity>> BOOBY = register("blue_footed_booby", BoobyEntity::new, SpawnGroup.CREATURE, 0.75F, 0.75F);
    public static final Supplier<EntityType<FennecFoxEntity>> FENNEC_FOX = register("fennec_fox", FennecFoxEntity::new, SpawnGroup.CREATURE, 0.75F, 1F);
    public static final Supplier<EntityType<KiwiEntity>> KIWI = register("kiwi", KiwiEntity::new, SpawnGroup.CREATURE, 0.75F, 0.75F);
    public static final Supplier<EntityType<ManateeEntity>> MANATEE = register("manatee", ManateeEntity::new, SpawnGroup.WATER_AMBIENT, 1.2F, 1.2F);
    public static final Supplier<EntityType<SealEntity>> SEAL = register("seal", SealEntity::new, SpawnGroup.WATER_AMBIENT, 1.2F, 1.2F);
    public static final Supplier<EntityType<WhiteDeerEntity>> WHITE_DEER = register("white_tailed_deer", WhiteDeerEntity::new, SpawnGroup.CREATURE, 1.2F, 1.2F);
    public static final Supplier<EntityType<RedDeerEntity>> RED_DEER = register("red_deer", RedDeerEntity::new, SpawnGroup.CREATURE, 1.2F, 1.2F);
    public static final Supplier<EntityType<FlamingoEntity>> FLAMINGO = register("flamingo", FlamingoEntity::new, SpawnGroup.WATER_AMBIENT, 1.8F, 0.9F);

    public static void register() {
        //Initialize Class
    }

    @ExpectPlatform
    public static <T extends Entity> Supplier<EntityType<T>> register(String id, EntityType.EntityFactory<T> factory, SpawnGroup group, float height, float width) {
        throw new AssertionError();
    }


}
