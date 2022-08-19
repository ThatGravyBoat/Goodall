package tech.thatgravyboat.goodall.client.renderer.deerhead;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.common.item.DeerHeadBlockItem;

public class DeerHeadItemModel extends AnimatedGeoModel<DeerHeadBlockItem> {
    @Override
    public ResourceLocation getModelResource(DeerHeadBlockItem object) {
        return new ResourceLocation(Goodall.MOD_ID, "geo/deer_head_item.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DeerHeadBlockItem object) {
        return new ResourceLocation(Goodall.MOD_ID, "textures/block/deer_head.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DeerHeadBlockItem animatable) {
        return new ResourceLocation(Goodall.MOD_ID, "animations/empty.animation.json");
    }
}
