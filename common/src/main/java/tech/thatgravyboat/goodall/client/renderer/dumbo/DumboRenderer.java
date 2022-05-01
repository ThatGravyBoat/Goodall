package tech.thatgravyboat.goodall.client.renderer.dumbo;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import tech.thatgravyboat.goodall.client.renderer.MobEntityRenderer;
import tech.thatgravyboat.goodall.common.entity.DumboEntity;

public class DumboRenderer extends MobEntityRenderer<DumboEntity> {

    public DumboRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new DumboModel());
    }


    @Override
    protected void applyRotations(DumboEntity dumbo, MatrixStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks) {
        float i = MathHelper.lerp(partialTicks, dumbo.prevTiltAngle, dumbo.tiltAngle);
        float j = MathHelper.lerp(partialTicks, dumbo.prevRollAngle, dumbo.rollAngle);
        matrixStack.translate(0.0D, 0.5D, 0.0D);
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F - rotationYaw));
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(i));
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(j));
        matrixStack.translate(0.0D, -0.2D, 0.0D);
    }

    @Override
    public RenderLayer getRenderType(DumboEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(textureLocation);
    }
}
