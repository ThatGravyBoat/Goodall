package tech.thatgravyboat.goodall.client.renderer.deerhead;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.common.block.DeerHeadBlockEntity;

public class DeerHeadBlockModel extends AnimatedGeoModel<DeerHeadBlockEntity> {
    @Override
    public Identifier getModelLocation(DeerHeadBlockEntity object) {
        return new Identifier(Goodall.MOD_ID, "geo/deer_head.geo.json");
    }

    @Override
    public Identifier getTextureLocation(DeerHeadBlockEntity object) {
        return new Identifier(Goodall.MOD_ID, "textures/block/deer_head.png");
    }

    @Override
    public Identifier getAnimationFileLocation(DeerHeadBlockEntity animatable) {
        return new Identifier(Goodall.MOD_ID, "animations/empty.animation.json");
    }
}
