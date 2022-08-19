package tech.thatgravyboat.goodall.client.renderer.deerhead;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import tech.thatgravyboat.goodall.common.item.DeerHeadBlockItem;

public class DeerHeadBlockItemRenderer extends GeoItemRenderer<DeerHeadBlockItem> {
    public DeerHeadBlockItemRenderer() {
        super(new DeerHeadItemModel());
    }

    @Override
    public RenderType getRenderType(DeerHeadBlockItem animatable, float partialTicks, PoseStack stack, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(textureLocation);
    }
}
