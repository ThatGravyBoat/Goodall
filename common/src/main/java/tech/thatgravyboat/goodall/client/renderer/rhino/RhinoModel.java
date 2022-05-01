package tech.thatgravyboat.goodall.client.renderer.rhino;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.common.entity.RhinoEntity;

public class RhinoModel extends AnimatedGeoModel<RhinoEntity> {
    @Override
    public Identifier getModelLocation(RhinoEntity object) {
        return new Identifier(Goodall.MOD_ID, "geo/rhino.geo.json");
    }

    @Override
    public Identifier getTextureLocation(RhinoEntity object) {
        String texture = object.isWhite() ? "white_rhino" : "black_rhino";
        if (object.isSleeping()) texture +="_sleep";
        return new Identifier(Goodall.MOD_ID, "textures/entity/"+texture+".png");
    }

    @Override
    public Identifier getAnimationFileLocation(RhinoEntity animatable) {
        return new Identifier(Goodall.MOD_ID, "animations/rhino.animation.json");
    }
}
