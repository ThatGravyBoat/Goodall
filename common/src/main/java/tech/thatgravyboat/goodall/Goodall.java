package tech.thatgravyboat.goodall;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import tech.thatgravyboat.goodall.common.entity.*;
import tech.thatgravyboat.goodall.common.registry.*;

import java.util.Map;

public class Goodall {

    public static final String MOD_ID = "goodall";

    public static void init() {
        ModEntities.register();
        ModItems.register();
        ModBlocks.register();
        ModSounds.register();
        ModParticles.register();
        ModFeatures.register();
        ModEnchantments.register();
    }

    public static void addEntityAttributes(Map<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> attributes) {
        attributes.put(ModEntities.RHINO.get(), RhinoEntity.createRhinoAttributes());
        attributes.put(ModEntities.DUMBO.get(), DumboEntity.createAttributes());
        attributes.put(ModEntities.PELICAN.get(), PelicanEntity.createBoobyAttributes());
        attributes.put(ModEntities.FENNEC_FOX.get(), FennecFoxEntity.createFennecFoxAttributes());
        attributes.put(ModEntities.KIWI.get(), KiwiEntity.createKiwiAttributes());
        attributes.put(ModEntities.MANATEE.get(), ManateeEntity.createManateeAttributes());
        attributes.put(ModEntities.SEAL.get(), SealEntity.createSealAttributes());
        attributes.put(ModEntities.DEER.get(), DeerEntity.createDeerAttributes());
        attributes.put(ModEntities.FLAMINGO.get(), FlamingoEntity.createFlamingoAttributes());
        attributes.put(ModEntities.SONGBIRD.get(), SongbirdEntity.createBirdAttributes());
        attributes.put(ModEntities.TOUCAN.get(), ToucanEntity.createBirdAttributes());
        attributes.put(ModEntities.TORTOISE.get(), TortoiseEntity.createTortoiseAttributes());
    }
}
