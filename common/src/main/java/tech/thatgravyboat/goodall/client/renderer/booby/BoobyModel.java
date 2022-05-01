package tech.thatgravyboat.goodall.client.renderer.booby;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.common.entity.BoobyEntity;

public class BoobyModel extends AnimatedGeoModel<BoobyEntity> {
    @Override
    public Identifier getModelLocation(BoobyEntity object) {
        return new Identifier(Goodall.MOD_ID, "geo/booby.geo.json");
    }

    @Override
    public Identifier getTextureLocation(BoobyEntity object) {
        return new Identifier(Goodall.MOD_ID, "textures/entity/booby.png");
    }

    @Override
    public Identifier getAnimationFileLocation(BoobyEntity animatable) {
        return new Identifier(Goodall.MOD_ID, "animations/booby.animation.json");
    }
}
