package tech.thatgravyboat.goodall.client.renderer.deerhead;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.common.item.DeerHeadBlockItem;

public class DeerHeadItemModel extends AnimatedGeoModel<DeerHeadBlockItem> {
    @Override
    public Identifier getModelLocation(DeerHeadBlockItem object) {
        return new Identifier(Goodall.MOD_ID, "geo/deer_head_item.geo.json");
    }

    @Override
    public Identifier getTextureLocation(DeerHeadBlockItem object) {
        return new Identifier(Goodall.MOD_ID, "textures/block/deer_head.png");
    }

    @Override
    public Identifier getAnimationFileLocation(DeerHeadBlockItem animatable) {
        return new Identifier(Goodall.MOD_ID, "animations/empty.animation.json");
    }
}
