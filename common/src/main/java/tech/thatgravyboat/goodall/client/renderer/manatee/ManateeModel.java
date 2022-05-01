package tech.thatgravyboat.goodall.client.renderer.manatee;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.common.entity.ManateeEntity;

public class ManateeModel extends AnimatedGeoModel<ManateeEntity> {
    @Override
    public Identifier getModelLocation(ManateeEntity object) {
        return new Identifier(Goodall.MOD_ID, "geo/manatee.geo.json");
    }

    @Override
    public Identifier getTextureLocation(ManateeEntity object) {
        return new Identifier(Goodall.MOD_ID, "textures/entity/manatee.png");
    }

    @Override
    public Identifier getAnimationFileLocation(ManateeEntity animatable) {
        return new Identifier(Goodall.MOD_ID, "animations/manatee.animation.json");
    }
}
