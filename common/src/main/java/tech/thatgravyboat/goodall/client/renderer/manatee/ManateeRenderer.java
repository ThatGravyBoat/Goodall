package tech.thatgravyboat.goodall.client.renderer.manatee;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import tech.thatgravyboat.goodall.client.renderer.MobEntityRenderer;
import tech.thatgravyboat.goodall.common.entity.ManateeEntity;

public class ManateeRenderer extends MobEntityRenderer<ManateeEntity> {

    public ManateeRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new ManateeModel());
    }

    @Override
    public void renderEarly(ManateeEntity animatable, MatrixStack stackIn, float ticks, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);

        if (animatable.isBaby()) stackIn.scale(0.5f, 0.5f, 0.5f);
    }

    @Override
    public RenderLayer getRenderType(ManateeEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(textureLocation);
    }
}
