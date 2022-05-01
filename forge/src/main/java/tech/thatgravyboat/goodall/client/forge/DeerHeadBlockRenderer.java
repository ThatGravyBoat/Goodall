package tech.thatgravyboat.goodall.client.forge;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;
import tech.thatgravyboat.goodall.common.block.DeerHeadBlockEntity;

public class DeerHeadBlockRenderer extends GeoBlockRenderer<DeerHeadBlockEntity> {
    public DeerHeadBlockRenderer(BlockEntityRendererFactory.Context rendererDispatcherIn, AnimatedGeoModel<DeerHeadBlockEntity> modelProvider) {
        super(rendererDispatcherIn, modelProvider);
    }

    @Override
    public RenderLayer getRenderType(DeerHeadBlockEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(textureLocation);
    }
}
