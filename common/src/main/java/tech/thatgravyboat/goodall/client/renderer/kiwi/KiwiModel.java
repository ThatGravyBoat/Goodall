package tech.thatgravyboat.goodall.client.renderer.kiwi;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.common.entity.KiwiEntity;

public class KiwiModel extends AnimatedGeoModel<KiwiEntity> {
    @Override
    public Identifier getModelLocation(KiwiEntity object) {
        return new Identifier(Goodall.MOD_ID, "geo/kiwi.geo.json");
    }

    @Override
    public Identifier getTextureLocation(KiwiEntity object) {
        return new Identifier(Goodall.MOD_ID, "textures/entity/kiwi.png");
    }

    @Override
    public Identifier getAnimationFileLocation(KiwiEntity animatable) {
        return new Identifier(Goodall.MOD_ID, "animations/kiwi.animation.json");
    }
}
