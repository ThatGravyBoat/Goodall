package tech.thatgravyboat.goodall.forge;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.geckolib3.GeckoLib;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.common.registry.ModConfiguredFeatures;
import tech.thatgravyboat.goodall.common.registry.ModSpawns;
import tech.thatgravyboat.goodall.common.registry.forge.*;

import java.util.HashMap;
import java.util.Map;

@Mod(Goodall.MOD_ID)
public class GoodallForge {

    public GoodallForge() {
        GeckoLib.initialize();
        Goodall.init();

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(GoodallForge::addEntityAttributes);
        bus.addListener(this::onComplete);
        bus.addListener(this::commonSetup);

        ModEntitiesImpl.ENTITIES.register(bus);
        ModItemsImpl.ITEMS.register(bus);
        ModBlocksImpl.BLOCKS.register(bus);
        ModBlocksImpl.BLOCK_ENTITIES.register(bus);
        ModSoundsImpl.SOUNDS.register(bus);
        ModParticlesImpl.PARTICLES.register(bus);
        ModFeaturesImpl.FEATURES.register(bus);
        ModEnchantmentsImpl.ENCHANTMENTS.register(bus);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> GoodallForgeClient::init);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onComplete(FMLLoadCompleteEvent event) {
        ModSpawns.registerSpawnRules();
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        ModConfiguredFeatures.registerFeatures();
    }

    public static void addEntityAttributes(EntityAttributeCreationEvent event) {
        Map<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> attributes = new HashMap<>();
        Goodall.addEntityAttributes(attributes);
        attributes.forEach((entity, builder) -> event.put(entity, builder.build()));
    }
}
