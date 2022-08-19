package tech.thatgravyboat.goodall.client.renderer.dumbo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import tech.thatgravyboat.goodall.client.renderer.base.BaseModel;
import tech.thatgravyboat.goodall.client.renderer.base.BaseRenderer;
import tech.thatgravyboat.goodall.common.entity.DumboEntity;

public class DumboRenderer extends BaseRenderer<DumboEntity> {

    public DumboRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BaseModel<>(), false);
    }

    @Override
    protected void applyRotations(DumboEntity dumbo, PoseStack stack, float ageInTicks, float rotationYaw, float partialTicks) {
        float i = Mth.lerp(partialTicks, dumbo.xBodyRotO, dumbo.xBodyRot);
        float j = Mth.lerp(partialTicks, dumbo.zBodyRotO, dumbo.zBodyRot);
        stack.translate(0.0D, 0.5D, 0.0D);
        stack.mulPose(Vector3f.YP.rotationDegrees(180.0F - rotationYaw));
        stack.mulPose(Vector3f.XP.rotationDegrees(i));
        stack.mulPose(Vector3f.YP.rotationDegrees(j));
        stack.translate(0.0D, -0.2D, 0.0D);
    }
}
