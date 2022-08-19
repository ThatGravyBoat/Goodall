package tech.thatgravyboat.goodall.client.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;
import tech.thatgravyboat.goodall.common.block.DeerHeadBlockEntity;

public class DeerHeadBlockRenderer extends GeoBlockRenderer<DeerHeadBlockEntity> {
    public DeerHeadBlockRenderer(AnimatedGeoModel<DeerHeadBlockEntity> modelProvider) {
        super(modelProvider);
    }

    @Override
    public RenderType getRenderType(DeerHeadBlockEntity animatable, float partialTicks, PoseStack stack, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(textureLocation);
    }
}
