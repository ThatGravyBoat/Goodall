package tech.thatgravyboat.goodall.common.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.level.levelgen.feature.DiskFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.function.Supplier;

public class ModFeatures {

    public static final Supplier<DiskFeature> DISK = registerFeature("land_disk", () -> new DiskFeature(DiskConfiguration.CODEC));

    public static void register() {
        //Initialize class
    }

    @ExpectPlatform
    public static <T extends FeatureConfiguration, F extends Feature<T>> Supplier<F> registerFeature(String id, Supplier<F> feature) {
        throw new AssertionError();
    }
}
