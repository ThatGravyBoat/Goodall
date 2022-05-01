package tech.thatgravyboat.goodall.client.renderer.seal;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.common.entity.SealEntity;

public class SealModel extends AnimatedGeoModel<SealEntity> {
    @Override
    public Identifier getModelLocation(SealEntity object) {
        return new Identifier(Goodall.MOD_ID, "geo/seal.geo.json");
    }

    @Override
    public Identifier getTextureLocation(SealEntity object) {
        String texture = object.isWhite() ? "snowy_seal" : "seal";
        if (object.hasSleepingTexture()) texture +="_sleeping";
        return new Identifier(Goodall.MOD_ID, "textures/entity/"+texture+".png");
    }

    @Override
    public Identifier getAnimationFileLocation(SealEntity animatable) {
        return new Identifier(Goodall.MOD_ID, "animations/seal.animation.json");
    }
}
