package tech.thatgravyboat.goodall.client.renderer.whitedeer;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.common.entity.WhiteDeerEntity;

public class WhiteDeerModel extends AnimatedGeoModel<WhiteDeerEntity> {
    @Override
    public Identifier getModelLocation(WhiteDeerEntity object) {
        return new Identifier(Goodall.MOD_ID, "geo/white_tailed_deer.geo.json");
    }

    @Override
    public Identifier getTextureLocation(WhiteDeerEntity object) {
        String texture = "white_tailed_deer";
        if (object.isSleeping()) texture +="_sleep";
        return new Identifier(Goodall.MOD_ID, "textures/entity/"+texture+".png");
    }

    @Override
    public Identifier getAnimationFileLocation(WhiteDeerEntity animatable) {
        return new Identifier(Goodall.MOD_ID, "animations/white_tailed_deer.animation.json");
    }
}
