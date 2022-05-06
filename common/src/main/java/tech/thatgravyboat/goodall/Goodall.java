package tech.thatgravyboat.goodall;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import tech.thatgravyboat.goodall.common.config.GoodallConfig;
import tech.thatgravyboat.goodall.common.entity.*;
import tech.thatgravyboat.goodall.common.registry.ModBlocks;
import tech.thatgravyboat.goodall.common.registry.ModEntities;
import tech.thatgravyboat.goodall.common.registry.ModItems;
import tech.thatgravyboat.goodall.common.registry.ModSounds;
import tech.thatgravyboat.goodall.config.ConfigLoader;

import java.util.Map;

public class Goodall {

    public static final String MOD_ID = "goodall";

    public static final GoodallConfig CONFIG = new GoodallConfig();

    public static void init() {
        ModEntities.register();
        ModItems.register();
        ModBlocks.register();
        ModSounds.register();

        ConfigLoader.registerConfig(CONFIG);
    }

    public static void addEntityAttributes(Map<EntityType<? extends LivingEntity>, DefaultAttributeContainer.Builder> attributes) {
        attributes.put(ModEntities.RHINO.get(), RhinoEntity.createRhinoAttributes());
        attributes.put(ModEntities.DUMBO.get(), DumboEntity.createSquidAttributes());
        attributes.put(ModEntities.BOOBY.get(), BoobyEntity.createBoobyAttributes());
        attributes.put(ModEntities.FENNEC_FOX.get(), FennecFoxEntity.createFennecFoxAttributes());
        attributes.put(ModEntities.KIWI.get(), KiwiEntity.createKiwiAttributes());
        attributes.put(ModEntities.MANATEE.get(), ManateeEntity.createManateeAttributes());
        attributes.put(ModEntities.SEAL.get(), SealEntity.createSealAttributes());
        attributes.put(ModEntities.WHITE_DEER.get(), WhiteDeerEntity.createDeerAttributes());
        attributes.put(ModEntities.RED_DEER.get(), RedDeerEntity.createDeerAttributes());
        attributes.put(ModEntities.FLAMINGO.get(), FlamingoEntity.createFlamingoAttributes());
    }
}
