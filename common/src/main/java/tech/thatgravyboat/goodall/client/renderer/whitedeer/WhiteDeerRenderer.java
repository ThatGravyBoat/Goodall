package tech.thatgravyboat.goodall.client.renderer.whitedeer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import tech.thatgravyboat.goodall.client.renderer.MobEntityRenderer;
import tech.thatgravyboat.goodall.common.entity.WhiteDeerEntity;

public class WhiteDeerRenderer extends MobEntityRenderer<WhiteDeerEntity> {

    public WhiteDeerRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new WhiteDeerModel());
    }

    @Override
    public void renderEarly(WhiteDeerEntity animatable, MatrixStack stackIn, float ticks, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);

        if (animatable.isBaby()) {
            stackIn.scale(0.5f, 0.5f, 0.5f);
        }
    }

    @Override
    public RenderLayer getRenderType(WhiteDeerEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(textureLocation);
    }
}
