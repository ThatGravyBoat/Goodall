package tech.thatgravyboat.goodall.client.renderer.booby;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import tech.thatgravyboat.goodall.client.renderer.MobEntityRenderer;
import tech.thatgravyboat.goodall.common.entity.BoobyEntity;

public class BoobyRenderer extends MobEntityRenderer<BoobyEntity> {

    public BoobyRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new BoobyModel());
    }

    @Override
    public RenderLayer getRenderType(BoobyEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(textureLocation);
    }
}
