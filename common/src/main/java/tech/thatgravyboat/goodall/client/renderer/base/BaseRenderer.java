package tech.thatgravyboat.goodall.client.renderer.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Mob;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import tech.thatgravyboat.goodall.common.entity.base.IEntityModel;

public class BaseRenderer<T extends Mob & IAnimatable & IEntityModel> extends FixedRenderer<T> {

    private final boolean hasBaby;

    public BaseRenderer(EntityRendererProvider.Context ctx, AnimatedGeoModel<T> modelProvider, boolean hasBaby) {
        super(ctx, modelProvider);
        this.hasBaby = hasBaby;
    }

    public static <T extends Mob & IAnimatable & IEntityModel> BaseRenderer<T> of(EntityRendererProvider.Context ctx, AnimatedGeoModel<T> modelProvider) {
        return new BaseRenderer<>(ctx, modelProvider, false);
    }

    public static <T extends Mob & IAnimatable & IEntityModel> BaseRenderer<T> ofBase(EntityRendererProvider.Context ctx) {
        return of(ctx, new BaseModel<>());
    }

    public static <T extends Mob & IAnimatable & IEntityModel> BaseRenderer<T> ofBaby(EntityRendererProvider.Context ctx, AnimatedGeoModel<T> modelProvider) {
        return new BaseRenderer<>(ctx, modelProvider, true);
    }

    public static <T extends Mob & IAnimatable & IEntityModel> BaseRenderer<T> ofBabyBase(EntityRendererProvider.Context ctx) {
        return ofBaby(ctx, new BaseModel<>());
    }

    @Override
    public void renderEarly(T animatable, PoseStack stackIn, float ticks, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);

        if (hasBaby && animatable.isBaby()) {
            stackIn.scale(0.5f, 0.5f, 0.5f);
        }
    }
}
