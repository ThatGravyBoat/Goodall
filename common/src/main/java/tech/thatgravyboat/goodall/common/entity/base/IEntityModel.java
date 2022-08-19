package tech.thatgravyboat.goodall.common.entity.base;

import net.minecraft.resources.ResourceLocation;

public interface IEntityModel {

    EntityModel getEntityModel();

    default ResourceLocation getIModel() {
        return getEntityModel().model;
    }

    default ResourceLocation getITexture() {
        return getEntityModel().texture;
    }

    default ResourceLocation getIAnimation() {
        return getEntityModel().animation;
    }
}
