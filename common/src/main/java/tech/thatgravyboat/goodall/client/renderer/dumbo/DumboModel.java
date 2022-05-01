package tech.thatgravyboat.goodall.client.renderer.dumbo;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.common.entity.DumboEntity;

public class DumboModel extends AnimatedGeoModel<DumboEntity> {
    @Override
    public Identifier getModelLocation(DumboEntity object) {
        return new Identifier(Goodall.MOD_ID, "geo/dumbo.geo.json");
    }

    @Override
    public Identifier getTextureLocation(DumboEntity object) {
        return object.getVariant().texture;
    }

    @Override
    public Identifier getAnimationFileLocation(DumboEntity animatable) {
        return new Identifier(Goodall.MOD_ID, "animations/dumbo.animation.json");
    }
}
