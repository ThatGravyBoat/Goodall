package tech.thatgravyboat.goodall.client.renderer.dumbo;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import tech.thatgravyboat.goodall.client.renderer.base.BaseModel;
import tech.thatgravyboat.goodall.client.renderer.base.BaseRenderer;
import tech.thatgravyboat.goodall.common.entity.DumboEntity;

public class DumboRenderer extends BaseRenderer<DumboEntity> {

    public DumboRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new BaseModel<>());
    }

    @Override
    protected void applyRotations(DumboEntity dumbo, MatrixStack stack, float ageInTicks, float rotationYaw, float partialTicks) {
        float i = MathHelper.lerp(partialTicks, dumbo.prevTiltAngle, dumbo.tiltAngle);
        float j = MathHelper.lerp(partialTicks, dumbo.prevRollAngle, dumbo.rollAngle);
        stack.translate(0.0D, 0.5D, 0.0D);
        stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F - rotationYaw));
        stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(i));
        stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(j));
        stack.translate(0.0D, -0.2D, 0.0D);
    }
}
