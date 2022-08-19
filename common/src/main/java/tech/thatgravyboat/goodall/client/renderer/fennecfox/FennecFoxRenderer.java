package tech.thatgravyboat.goodall.client.renderer.fennecfox;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.util.RenderUtils;
import tech.thatgravyboat.goodall.client.renderer.base.BaseModel;
import tech.thatgravyboat.goodall.client.renderer.base.BaseRenderer;
import tech.thatgravyboat.goodall.common.entity.FennecFoxEntity;

public class FennecFoxRenderer extends BaseRenderer<FennecFoxEntity> {

    public FennecFoxRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BaseModel<>(), false);
    }

    @Override
    public void renderRecursively(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (bone.name.equals("head") && !this.mainHand.isEmpty()) {
            stack.pushPose();
            RenderUtils.translate(bone, stack);
            RenderUtils.moveToPivot(bone, stack);
            RenderUtils.rotate(bone, stack);
            RenderUtils.scale(bone, stack);
            RenderUtils.moveBackFromPivot(bone, stack);

            stack.mulPose(Vector3f.ZN.rotationDegrees(90));
            stack.translate(-0.275, -0.1, -0.6);
            stack.scale(0.5f, 0.5f, 0.5f);

            Minecraft.getInstance().getItemRenderer().renderStatic(this.mainHand, ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, packedLightIn, packedOverlayIn, stack, this.rtb, 0);

            stack.popPose();

            bufferIn = rtb.getBuffer(RenderType.entityTranslucent(whTexture));
        }
        super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}
