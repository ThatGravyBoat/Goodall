package tech.thatgravyboat.goodall.common.lib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import tech.thatgravyboat.goodall.Goodall;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public enum ToucanVariant {
    COMMON(new ResourceLocation(Goodall.MOD_ID, "textures/entity/toucan/normal.png")),
    FANCY(new ResourceLocation(Goodall.MOD_ID, "textures/entity/toucan/fancy.png")),
    RARE(new ResourceLocation(Goodall.MOD_ID, "textures/entity/toucan/rare.png")),
    FRUIT_LOOPS(new ResourceLocation(Goodall.MOD_ID, "textures/entity/toucan/sam.png"), true);

    public static final List<ToucanVariant> NORMAL_VALUES = Arrays.stream(values()).filter(ToucanVariant::isNormal).toList();

    public final ResourceLocation texture;
    public final boolean special;

    ToucanVariant(ResourceLocation texture) {
        this(texture, false);
    }

    ToucanVariant(ResourceLocation texture, boolean special) {
        this.texture = texture;
        this.special = special;
    }

    public boolean isNormal() {
        return !this.special;
    }

    public static ToucanVariant getVariant(int id) {
        try {
            return ToucanVariant.values()[id];
        } catch (Exception e) {
            return ToucanVariant.COMMON;
        }
    }

    public static ToucanVariant random(RandomSource random) {
        var v = random.nextFloat();
        if (v < 0.1) {
            return ToucanVariant.RARE;
        } else if (v < 0.4) {
            return ToucanVariant.FANCY;
        }
        return ToucanVariant.COMMON;
    }

    public static Optional<ToucanVariant> getVariantForName(String name) {
        if (name.equalsIgnoreCase("fruit loops") || name.equalsIgnoreCase("toucan sam") || name.equalsIgnoreCase("sam")) {
            return Optional.of(ToucanVariant.FRUIT_LOOPS);
        }
        return Optional.empty();
    }
}
