package tech.thatgravyboat.goodall.fabric;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.common.fabric.config.GoodallConfig;
import tech.thatgravyboat.goodall.common.registry.ModConfiguredFeatures;
import tech.thatgravyboat.goodall.common.registry.ModEntities;

public class FabricSpawns {

    public static final GoodallConfig CONFIG = new GoodallConfig();

    public static void register() {
        registerSpawn(tag("deer"), ModEntities.DEER.get(), CONFIG.spawnConfig.deer.weight, CONFIG.spawnConfig.deer.min, CONFIG.spawnConfig.deer.max);
        registerSpawn(tag("dumbo"), ModEntities.DUMBO.get(), CONFIG.spawnConfig.dumboOctopus.weight, CONFIG.spawnConfig.dumboOctopus.min, CONFIG.spawnConfig.dumboOctopus.max);
        registerSpawn(tag("fennec_fox"), ModEntities.FENNEC_FOX.get(), CONFIG.spawnConfig.fennecFox.weight, CONFIG.spawnConfig.fennecFox.min, CONFIG.spawnConfig.fennecFox.max);
        registerSpawn(tag("flamingo"), ModEntities.FLAMINGO.get(), CONFIG.spawnConfig.flamingo.weight, CONFIG.spawnConfig.flamingo.min, CONFIG.spawnConfig.flamingo.max);
        registerSpawn(tag("kiwi"), ModEntities.KIWI.get(), CONFIG.spawnConfig.kiwi.weight, CONFIG.spawnConfig.kiwi.min, CONFIG.spawnConfig.kiwi.max);
        registerSpawn(tag("manatee"), ModEntities.MANATEE.get(), CONFIG.spawnConfig.manatee.weight, CONFIG.spawnConfig.manatee.min, CONFIG.spawnConfig.manatee.max);
        registerSpawn(tag("pelican"), ModEntities.PELICAN.get(), CONFIG.spawnConfig.pelican.weight, CONFIG.spawnConfig.pelican.min, CONFIG.spawnConfig.pelican.max);
        registerSpawn(tag("rhino"), ModEntities.RHINO.get(), CONFIG.spawnConfig.rhino.weight, CONFIG.spawnConfig.rhino.min, CONFIG.spawnConfig.rhino.max);
        registerSpawn(tag("seal"), ModEntities.SEAL.get(), CONFIG.spawnConfig.seal.weight, CONFIG.spawnConfig.seal.min, CONFIG.spawnConfig.seal.max);
        registerSpawn(tag("songbird"), ModEntities.SONGBIRD.get(), CONFIG.spawnConfig.songbird.weight, CONFIG.spawnConfig.songbird.min, CONFIG.spawnConfig.songbird.max);
        registerSpawn(tag("tortoise"), ModEntities.TORTOISE.get(), CONFIG.spawnConfig.tortoise.weight, CONFIG.spawnConfig.tortoise.min, CONFIG.spawnConfig.tortoise.max);
        registerSpawn(tag("toucan"), ModEntities.TOUCAN.get(), CONFIG.spawnConfig.toucan.weight, CONFIG.spawnConfig.toucan.min, CONFIG.spawnConfig.toucan.max);
    }

    public static void registerFeature() {
        TagKey<Biome> biome = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(Goodall.MOD_ID, "quicksand_generates"));
        BiomeModifications.addFeature(BiomeSelectors.tag(biome), GenerationStep.Decoration.SURFACE_STRUCTURES, ModConfiguredFeatures.PLACED_QUICKSAND.unwrapKey().orElseThrow());
    }

    public static TagKey<Biome> tag(String tag) {
        return TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(Goodall.MOD_ID, tag+"_spawns"));
    }

    public static void registerSpawn(TagKey<Biome> category, EntityType<?> entity, int weight, int min, int max) {
        BiomeModifications.addSpawn(BiomeSelectors.tag(category), entity.getCategory(), entity, weight, min, max);
    }

}
