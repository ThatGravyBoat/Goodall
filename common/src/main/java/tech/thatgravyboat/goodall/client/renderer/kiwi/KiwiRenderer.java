package tech.thatgravyboat.goodall.client.renderer.kiwi;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import tech.thatgravyboat.goodall.client.renderer.MobEntityRenderer;
import tech.thatgravyboat.goodall.common.entity.KiwiEntity;

public class KiwiRenderer extends MobEntityRenderer<KiwiEntity> {

    public KiwiRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new KiwiModel());
    }

    @Override
    public RenderLayer getRenderType(KiwiEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(textureLocation);
    }
}
