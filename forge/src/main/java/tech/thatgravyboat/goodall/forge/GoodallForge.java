package tech.thatgravyboat.goodall.forge;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.geckolib3.GeckoLib;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.common.registry.ModSpawns;
import tech.thatgravyboat.goodall.common.registry.SpawnData;
import tech.thatgravyboat.goodall.common.registry.forge.*;
import tech.thatgravyboat.goodall.config.forge.ConfigLoaderImpl;

import java.util.HashMap;
import java.util.Map;

@Mod(Goodall.MOD_ID)
public class GoodallForge {

    private static boolean hasRegisteredSpawns = false;

    public GoodallForge() {
        GeckoLib.initialize();
        Goodall.init();

        ConfigLoaderImpl.initialize();

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(GoodallForge::addEntityAttributes);
        bus.addListener(this::onComplete);


        ModEntitiesImpl.ENTITIES.register(bus);
        ModItemsImpl.ITEMS.register(bus);
        ModBlocksImpl.BLOCKS.register(bus);
        ModBlocksImpl.BLOCK_ENTITIES.register(bus);
        ModSoundsImpl.SOUNDS.register(bus);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> GoodallForgeClient::init);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onComplete(FMLLoadCompleteEvent event) {
        ModSpawns.registerSpawnRules();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void addSpawns(BiomeLoadingEvent event) {
        if (!hasRegisteredSpawns) {
            ModSpawns.registerSpawns();
            hasRegisteredSpawns = true;
        }
        for (SpawnData spawnData : ModSpawnsImpl.CATEGORY_SPAWNS.get(event.getCategory())) {
            event.getSpawns().spawn(spawnData.group(),
                    new SpawnSettings.SpawnEntry(spawnData.entityType(), spawnData.weight(), spawnData.min(), spawnData.max()));
        }
        for (SpawnData spawnData : ModSpawnsImpl.BIOME_SPAWNS.get(event.getName())) {
            event.getSpawns().spawn(spawnData.group(),
                    new SpawnSettings.SpawnEntry(spawnData.entityType(), spawnData.weight(), spawnData.min(), spawnData.max()));
        }
    }

    public static void addEntityAttributes(EntityAttributeCreationEvent event) {
        Map<EntityType<? extends LivingEntity>, DefaultAttributeContainer.Builder> attributes = new HashMap<>();
        Goodall.addEntityAttributes(attributes);
        attributes.forEach((entity, builder) -> event.put(entity, builder.build()));
    }
}
