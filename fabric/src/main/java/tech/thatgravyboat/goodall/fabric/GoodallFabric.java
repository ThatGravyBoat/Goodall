package tech.thatgravyboat.goodall.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import software.bernie.geckolib3.GeckoLib;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.common.registry.ModConfiguredFeatures;
import tech.thatgravyboat.goodall.common.registry.ModSpawns;
import tech.thatgravyboat.goodall.common.registry.fabric.ModBlocksImpl;
import tech.thatgravyboat.goodall.common.registry.fabric.ModEntitiesImpl;
import tech.thatgravyboat.goodall.common.registry.fabric.ModItemsImpl;
import tech.thatgravyboat.goodall.common.registry.fabric.ModSoundsImpl;
import tech.thatgravyboat.goodall.config.ConfigLoader;
import tech.thatgravyboat.goodall.config.ConfigLoaderImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GoodallFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        GeckoLib.initialize();
        Goodall.init();

        ConfigLoader.registerConfig(FabricSpawns.CONFIG);

        try {
            ConfigLoaderImpl.initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ModEntitiesImpl.ENTITIES.forEach((id, entity) -> Registry.register(Registry.ENTITY_TYPE, new ResourceLocation(Goodall.MOD_ID, id), entity.get()));
        ModItemsImpl.ITEMS.forEach((id, item) -> Registry.register(Registry.ITEM, new ResourceLocation(Goodall.MOD_ID, id), item.get()));
        ModBlocksImpl.BLOCKS.forEach((id, block) -> Registry.register(Registry.BLOCK, new ResourceLocation(Goodall.MOD_ID, id), block.get()));
        ModBlocksImpl.BLOCK_ENTITIES.forEach((id, block) -> Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(Goodall.MOD_ID, id), block.get()));
        ModSoundsImpl.SOUNDS.forEach((id, sound) -> Registry.register(Registry.SOUND_EVENT, new ResourceLocation(Goodall.MOD_ID, id), sound.get()));

        Map<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> attributes = new HashMap<>();
        Goodall.addEntityAttributes(attributes);
        attributes.forEach(FabricDefaultAttributeRegistry::register);

        ModSpawns.registerSpawnRules();
        FabricSpawns.register();
        FabricSpawns.registerFeature();

        ModConfiguredFeatures.registerFeatures();
    }
}
