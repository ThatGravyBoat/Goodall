package tech.thatgravyboat.goodall.client.renderer.reddeer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import tech.thatgravyboat.goodall.client.renderer.MobEntityRenderer;
import tech.thatgravyboat.goodall.common.entity.RedDeerEntity;

public class RedDeerRenderer extends MobEntityRenderer<RedDeerEntity> {

    public RedDeerRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new RedDeerModel());
    }

    @Override
    public RenderLayer getRenderType(RedDeerEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(textureLocation);
    }
}
