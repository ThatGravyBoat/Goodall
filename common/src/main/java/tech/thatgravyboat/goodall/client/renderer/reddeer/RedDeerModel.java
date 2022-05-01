package tech.thatgravyboat.goodall.client.renderer.reddeer;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.common.entity.RedDeerEntity;

public class RedDeerModel extends AnimatedGeoModel<RedDeerEntity> {
    @Override
    public Identifier getModelLocation(RedDeerEntity object) {
        return new Identifier(Goodall.MOD_ID, "geo/red_deer.geo.json");
    }

    @Override
    public Identifier getTextureLocation(RedDeerEntity object) {
        return new Identifier(Goodall.MOD_ID, "textures/entity/red_deer.png");
    }

    @Override
    public Identifier getAnimationFileLocation(RedDeerEntity animatable) {
        return new Identifier(Goodall.MOD_ID, "animations/red_deer.animation.json");
    }
}
