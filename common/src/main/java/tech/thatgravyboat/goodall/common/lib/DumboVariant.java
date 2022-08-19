package tech.thatgravyboat.goodall.common.lib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import tech.thatgravyboat.goodall.Goodall;

import java.util.Random;

public enum DumboVariant {
    YELLOW(new ResourceLocation(Goodall.MOD_ID, "textures/entity/dumbo/yellow.png")),
    PURPLE(new ResourceLocation(Goodall.MOD_ID, "textures/entity/dumbo/purple.png")),
    PINK(new ResourceLocation(Goodall.MOD_ID, "textures/entity/dumbo/pink.png"));

    public final ResourceLocation texture;

    DumboVariant(ResourceLocation texture) {
        this.texture = texture;
    }

    public static DumboVariant getVariant(int id) {
        try {
            return DumboVariant.values()[id];
        } catch (Exception e) {
            return DumboVariant.YELLOW;
        }
    }

    public static DumboVariant random(RandomSource random) {
        return DumboVariant.values()[random.nextInt(DumboVariant.values().length)];
    }
}
