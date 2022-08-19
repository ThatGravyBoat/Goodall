package tech.thatgravyboat.goodall.client.renderer.deerhead;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.common.block.DeerHeadBlockEntity;

public class DeerHeadBlockModel extends AnimatedGeoModel<DeerHeadBlockEntity> {
    @Override
    public ResourceLocation getModelResource(DeerHeadBlockEntity object) {
        return new ResourceLocation(Goodall.MOD_ID, "geo/deer_head.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DeerHeadBlockEntity object) {
        return new ResourceLocation(Goodall.MOD_ID, "textures/block/deer_head.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DeerHeadBlockEntity animatable) {
        return new ResourceLocation(Goodall.MOD_ID, "animations/empty.animation.json");
    }
}
