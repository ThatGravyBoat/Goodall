package tech.thatgravyboat.goodall.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import software.bernie.geckolib3.GeckoLib;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.common.registry.ModSpawns;
import tech.thatgravyboat.goodall.common.registry.fabric.ModBlocksImpl;
import tech.thatgravyboat.goodall.common.registry.fabric.ModEntitiesImpl;
import tech.thatgravyboat.goodall.common.registry.fabric.ModItemsImpl;
import tech.thatgravyboat.goodall.common.registry.fabric.ModSoundsImpl;
import tech.thatgravyboat.goodall.config.fabric.ConfigLoaderImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GoodallFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        GeckoLib.initialize();
        Goodall.init();

        try {
            ConfigLoaderImpl.initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ModEntitiesImpl.ENTITIES.forEach((id, entity) -> Registry.register(Registry.ENTITY_TYPE, new Identifier(Goodall.MOD_ID, id), entity.get()));
        ModItemsImpl.ITEMS.forEach((id, item) -> Registry.register(Registry.ITEM, new Identifier(Goodall.MOD_ID, id), item.get()));
        ModBlocksImpl.BLOCKS.forEach((id, block) -> Registry.register(Registry.BLOCK, new Identifier(Goodall.MOD_ID, id), block.get()));
        ModBlocksImpl.BLOCK_ENTITIES.forEach((id, block) -> Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Goodall.MOD_ID, id), block.get()));
        ModSoundsImpl.SOUNDS.forEach((id, sound) -> Registry.register(Registry.SOUND_EVENT, new Identifier(Goodall.MOD_ID, id), sound.get()));

        Map<EntityType<? extends LivingEntity>, DefaultAttributeContainer.Builder> attributes = new HashMap<>();
        Goodall.addEntityAttributes(attributes);
        attributes.forEach(FabricDefaultAttributeRegistry::register);

        ModSpawns.registerSpawnRules();
        ModSpawns.registerSpawns();
    }
}
