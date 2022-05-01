package tech.thatgravyboat.goodall.common.lib;

import net.minecraft.util.Identifier;
import tech.thatgravyboat.goodall.Goodall;

import java.util.Random;

public enum DumboVariant {
    YELLOW(new Identifier(Goodall.MOD_ID, "textures/entity/dumbo_octopus_yellow.png")),
    BLUE(new Identifier(Goodall.MOD_ID, "textures/entity/dumbo_octopus_blue.png")),
    PINK(new Identifier(Goodall.MOD_ID, "textures/entity/dumbo_octopus_pink.png"));

    public final Identifier texture;

    DumboVariant(Identifier texture) {
        this.texture = texture;
    }

    public static DumboVariant getVariant(int id) {
        try {
            return DumboVariant.values()[id];
        } catch (Exception e) {
            return DumboVariant.YELLOW;
        }
    }

    public static DumboVariant random(Random random) {
        return DumboVariant.values()[random.nextInt(DumboVariant.values().length)];
    }
}
