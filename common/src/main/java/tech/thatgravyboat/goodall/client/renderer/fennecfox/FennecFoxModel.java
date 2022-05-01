package tech.thatgravyboat.goodall.client.renderer.fennecfox;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.common.entity.FennecFoxEntity;

public class FennecFoxModel extends AnimatedGeoModel<FennecFoxEntity> {
    @Override
    public Identifier getModelLocation(FennecFoxEntity object) {
        return new Identifier(Goodall.MOD_ID, "geo/fennec_fox.geo.json");
    }

    @Override
    public Identifier getTextureLocation(FennecFoxEntity object) {
        return new Identifier(Goodall.MOD_ID, "textures/entity/fennec_fox.png");
    }

    @Override
    public Identifier getAnimationFileLocation(FennecFoxEntity animatable) {
        return new Identifier(Goodall.MOD_ID, "animations/fennec_fox.animation.json");
    }
}
