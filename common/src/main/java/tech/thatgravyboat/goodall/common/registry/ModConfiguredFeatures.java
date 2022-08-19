package tech.thatgravyboat.goodall.common.registry;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedBlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public class ModConfiguredFeatures {

    public static final Holder<ConfiguredFeature<DiskConfiguration, ?>> QUICKSAND = FeatureUtils.register(
            "goodall:quicksand", ModFeatures.DISK.get(),
            new DiskConfiguration(
                    RuleBasedBlockStateProvider.simple(ModBlocks.QUICKSAND.get()),
                    BlockPredicate.matchesBlocks(Blocks.SAND),
                    UniformInt.of(2, 3),
                    2
            )
    );

    public static final Holder<PlacedFeature> PLACED_QUICKSAND = PlacementUtils.register(
            "goodall:quicksand_common",
            QUICKSAND,
            RarityFilter.onAverageOnceEvery(40),
            InSquarePlacement.spread(),
            PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
            BiomeFilter.biome()
    );

    public static void registerFeatures() {

    }
}
