package tech.thatgravyboat.goodall.common.registry.forge;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import tech.thatgravyboat.goodall.Goodall;

import java.util.function.Supplier;

public class ModFeaturesImpl {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, Goodall.MOD_ID);

    public static <T extends FeatureConfiguration, F extends Feature<T>> Supplier<F> registerFeature(String id, Supplier<F> feature) {
        return FEATURES.register(id, feature);
    }
}
