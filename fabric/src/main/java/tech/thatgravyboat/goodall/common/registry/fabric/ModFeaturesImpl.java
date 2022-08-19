package tech.thatgravyboat.goodall.common.registry.fabric;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import tech.thatgravyboat.goodall.Goodall;

import java.util.function.Supplier;

public class ModFeaturesImpl {
    public static <T extends FeatureConfiguration, F extends Feature<T>> Supplier<F> registerFeature(String id, Supplier<F> input) {
        F feature = Registry.register(Registry.FEATURE, new ResourceLocation(Goodall.MOD_ID, id), input.get());
        return () -> feature;
    }
}
