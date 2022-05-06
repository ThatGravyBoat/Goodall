package tech.thatgravyboat.goodall.common.entity.base;

import net.minecraft.util.Identifier;

public interface IEntityModel {

    EntityModel getEntityModel();

    default Identifier getIModel() {
        return getEntityModel().model;
    }

    default Identifier getITexture() {
        return getEntityModel().texture;
    }

    default Identifier getIAnimation() {
        return getEntityModel().animation;
    }
}
