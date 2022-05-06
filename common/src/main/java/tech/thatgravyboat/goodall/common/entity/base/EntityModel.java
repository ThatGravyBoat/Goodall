package tech.thatgravyboat.goodall.common.entity.base;

import net.minecraft.util.Identifier;
import tech.thatgravyboat.goodall.Goodall;

public enum EntityModel {
    DUMBO(new Identifier(Goodall.MOD_ID, "geo/dumbo.geo.json"), null, new Identifier(Goodall.MOD_ID, "animations/dumbo.animation.json")),
    BOOBY("booby"),
    FENNEC("fennec_fox"),
    FLAMINGO("flamingo"),
    KIWI("kiwi"),
    MANATEE("manatee"),
    REDDEER("red_deer");

    public final Identifier model;
    public final Identifier texture;
    public final Identifier animation;

    EntityModel(String id) {
        this(new Identifier(Goodall.MOD_ID, "geo/"+id+".geo.json"), new Identifier(Goodall.MOD_ID, "textures/entity/"+id+".png"), new Identifier(Goodall.MOD_ID, "animations/"+id+".animation.json"));
    }

    EntityModel(Identifier model, Identifier texture, Identifier animation) {
        this.model = model;
        this.texture = texture;
        this.animation = animation;
    }
}
