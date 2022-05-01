package tech.thatgravyboat.goodall.client.renderer.deerhead;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import tech.thatgravyboat.goodall.common.item.DeerHeadBlockItem;

public class DeerHeadBlockItemRenderer extends GeoItemRenderer<DeerHeadBlockItem> {
    public DeerHeadBlockItemRenderer() {
        super(new DeerHeadItemModel());
    }

    @Override
    public RenderLayer getRenderType(DeerHeadBlockItem animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(textureLocation);
    }
}
