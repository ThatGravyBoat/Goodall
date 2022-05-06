package tech.thatgravyboat.goodall.client.renderer.base;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import tech.thatgravyboat.goodall.common.entity.base.IEntityModel;

public class BaseModel<T extends IEntityModel & IAnimatable> extends AnimatedGeoModel<T> {
    @Override
    public Identifier getModelLocation(T object) {
        return object.getIModel();
    }

    @Override
    public Identifier getTextureLocation(T object) {
        return object.getITexture();
    }

    @Override
    public Identifier getAnimationFileLocation(T object) {
        return object.getIAnimation();
    }
}
