package tech.thatgravyboat.goodall.client.renderer.fennecfox;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.util.RenderUtils;
import tech.thatgravyboat.goodall.client.renderer.MobEntityRenderer;
import tech.thatgravyboat.goodall.client.renderer.base.BaseModel;
import tech.thatgravyboat.goodall.client.renderer.base.BaseRenderer;
import tech.thatgravyboat.goodall.common.entity.FennecFoxEntity;

public class FennecFoxRenderer extends BaseRenderer<FennecFoxEntity> {

    public FennecFoxRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new BaseModel<>());
    }

    @Override
    public void renderRecursively(GeoBone bone, MatrixStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (bone.name.equals("head") && !this.mainHand.isEmpty()) {
            stack.push();
            RenderUtils.translate(bone, stack);
            RenderUtils.moveToPivot(bone, stack);
            RenderUtils.rotate(bone, stack);
            RenderUtils.scale(bone, stack);
            RenderUtils.moveBackFromPivot(bone, stack);

            stack.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(90));
            stack.translate(-0.275, -0.1, -0.6);
            stack.scale(0.5f, 0.5f, 0.5f);

            MinecraftClient.getInstance().getItemRenderer().renderItem(this.mainHand, ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND, packedLightIn, packedOverlayIn, stack, this.rtb, 0);

            stack.pop();

            bufferIn = rtb.getBuffer(RenderLayer.getEntityTranslucent(whTexture));
        }
        super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}
