package tech.thatgravyboat.goodall.common.lib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import tech.thatgravyboat.goodall.Goodall;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum DeerVariant {
    BROWN(new ResourceLocation(Goodall.MOD_ID, "textures/entity/deer/brown_deer")),
    DARK_BROWN(new ResourceLocation(Goodall.MOD_ID, "textures/entity/deer/dark_brown_deer")),
    RED(new ResourceLocation(Goodall.MOD_ID, "textures/entity/deer/red_deer")),
    RUDOLPH(true, false, new ResourceLocation(Goodall.MOD_ID, "textures/entity/deer/rudolph_deer")),
    WHITE(new ResourceLocation(Goodall.MOD_ID, "textures/entity/deer/white_deer")),
    WHITE_TAILED(false, true, new ResourceLocation(Goodall.MOD_ID, "textures/entity/deer/white_tailed_deer"));

    public static final List<DeerVariant> NORMAL_VALUES = Arrays.stream(values()).filter(DeerVariant::isNormal).toList();

    public final boolean special;
    public final boolean passive;
    public final ResourceLocation texture;

    DeerVariant(boolean special, boolean passive, ResourceLocation texture) {
        this.special = special;
        this.passive = passive;
        this.texture = texture;
    }

    DeerVariant(ResourceLocation texture) {
        this(false, false, texture);
    }

    public boolean isNormal() {
        return !this.special;
    }

    public static DeerVariant getVariant(int id) {
        try {
            return DeerVariant.values()[id];
        } catch (Exception e) {
            return DeerVariant.BROWN;
        }
    }

    public static DeerVariant random(RandomSource random) {
        return NORMAL_VALUES.get(random.nextInt(NORMAL_VALUES.size()));
    }

    public static Optional<DeerVariant> getVariantForName(String name) {
        if (name.equalsIgnoreCase("rudolph")) {
            return Optional.of(DeerVariant.RUDOLPH);
        }
        return Optional.empty();
    }

    public ResourceLocation getTexture(boolean sleeping) {
        return new ResourceLocation(this.texture.getNamespace(), this.texture.getPath() + (sleeping ? "_sleeping.png" : ".png"));
    }

}
