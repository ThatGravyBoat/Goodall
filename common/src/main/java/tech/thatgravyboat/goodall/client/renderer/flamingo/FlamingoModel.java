package tech.thatgravyboat.goodall.client.renderer.flamingo;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.common.entity.FlamingoEntity;

public class FlamingoModel extends AnimatedGeoModel<FlamingoEntity> {
    @Override
    public Identifier getModelLocation(FlamingoEntity object) {
        return new Identifier(Goodall.MOD_ID, "geo/flamingo.geo.json");
    }

    @Override
    public Identifier getTextureLocation(FlamingoEntity object) {
        return new Identifier(Goodall.MOD_ID, "textures/entity/flamingo.png");
    }

    @Override
    public Identifier getAnimationFileLocation(FlamingoEntity animatable) {
        return new Identifier(Goodall.MOD_ID, "animations/flamingo.animation.json");
    }
}
