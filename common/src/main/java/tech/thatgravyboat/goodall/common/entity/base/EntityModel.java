package tech.thatgravyboat.goodall.common.entity.base;

import net.minecraft.resources.ResourceLocation;
import tech.thatgravyboat.goodall.Goodall;

public enum EntityModel {
    DUMBO("dumbo"),
    PELICAN("pelican"),
    FENNEC("fennec_fox"),
    FLAMINGO("flamingo"),
    KIWI("kiwi"),
    MANATEE("manatee"),
    SONGBIRD("songbird"),
    TOUCAN("toucan"),
    TORTOISE("tortoise"),
    DEER("deer"),
    SEAL("seal"),
    RHINO("rhino");

    public final ResourceLocation model;
    public final ResourceLocation texture;
    public final ResourceLocation animation;

    EntityModel(String id) {
        this(new ResourceLocation(Goodall.MOD_ID, "geo/"+id+".geo.json"), new ResourceLocation(Goodall.MOD_ID, "textures/entity/"+id+".png"), new ResourceLocation(Goodall.MOD_ID, "animations/"+id+".animation.json"));
    }

    EntityModel(ResourceLocation model, ResourceLocation texture, ResourceLocation animation) {
        this.model = model;
        this.texture = texture;
        this.animation = animation;
    }
}
