package tech.thatgravyboat.goodall.client.renderer.base;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import tech.thatgravyboat.goodall.common.entity.base.IEntityModel;

public class BaseModel<T extends IEntityModel & IAnimatable> extends AnimatedGeoModel<T> {
    @Override
    public ResourceLocation getModelResource(T object) {
        return object.getIModel();
    }

    @Override
    public ResourceLocation getTextureResource(T object) {
        return object.getITexture();
    }

    @Override
    public ResourceLocation getAnimationResource(T object) {
        return object.getIAnimation();
    }
}
