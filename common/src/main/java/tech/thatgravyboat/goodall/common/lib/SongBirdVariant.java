package tech.thatgravyboat.goodall.common.lib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import tech.thatgravyboat.goodall.Goodall;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum SongBirdVariant {
    BROWN(new ResourceLocation(Goodall.MOD_ID, "textures/entity/songbird/brown.png")),
    CANARY(new ResourceLocation(Goodall.MOD_ID, "textures/entity/songbird/canary.png")),
    CARDINAL(new ResourceLocation(Goodall.MOD_ID, "textures/entity/songbird/cardinal.png")),
    ROBIN(new ResourceLocation(Goodall.MOD_ID, "textures/entity/songbird/robin.png")),
    TWEETY(new ResourceLocation(Goodall.MOD_ID, "textures/entity/songbird/tweety.png"), true),
    BLUE(new ResourceLocation(Goodall.MOD_ID, "textures/entity/songbird/blue.png"), true);

    public static final List<SongBirdVariant> NORMAL_VALUES = Arrays.stream(values()).filter(SongBirdVariant::isNormal).toList();

    public final ResourceLocation texture;
    public final boolean special;

    SongBirdVariant(ResourceLocation texture) {
        this(texture, false);
    }

    SongBirdVariant(ResourceLocation texture, boolean special) {
        this.texture = texture;
        this.special = special;
    }

    public boolean isNormal() {
        return !this.special;
    }

    public static SongBirdVariant getVariant(int id) {
        try {
            return SongBirdVariant.values()[id];
        } catch (Exception e) {
            return SongBirdVariant.CARDINAL;
        }
    }

    public static Optional<SongBirdVariant> getVariantForName(String name) {
        if (name.equalsIgnoreCase("tweety")) {
            return Optional.of(SongBirdVariant.TWEETY);
        }
        return Optional.empty();
    }

    public static SongBirdVariant random(RandomSource random) {
        return NORMAL_VALUES.get(random.nextInt(NORMAL_VALUES.size()));
    }
}
