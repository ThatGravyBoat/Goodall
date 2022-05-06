package tech.thatgravyboat.goodall.client.renderer.manatee;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import tech.thatgravyboat.goodall.client.renderer.base.BaseModel;
import tech.thatgravyboat.goodall.client.renderer.base.BaseRenderer;
import tech.thatgravyboat.goodall.common.entity.ManateeEntity;

public class ManateeRenderer extends BaseRenderer<ManateeEntity> {

    public ManateeRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new BaseModel<>());
    }

    @Override
    public void renderEarly(ManateeEntity animatable, MatrixStack stackIn, float ticks, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);

        if (animatable.isBaby()) stackIn.scale(0.5f, 0.5f, 0.5f);
    }
}
