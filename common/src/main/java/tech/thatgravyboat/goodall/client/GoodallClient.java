package tech.thatgravyboat.goodall.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import tech.thatgravyboat.goodall.client.renderer.base.BaseModel;
import tech.thatgravyboat.goodall.client.renderer.base.BaseRenderer;
import tech.thatgravyboat.goodall.client.renderer.deerhead.DeerHeadBlockItemRenderer;
import tech.thatgravyboat.goodall.client.renderer.dumbo.DumboRenderer;
import tech.thatgravyboat.goodall.client.renderer.fennecfox.FennecFoxRenderer;
import tech.thatgravyboat.goodall.client.renderer.flamingo.FlamingoRenderer;
import tech.thatgravyboat.goodall.client.renderer.manatee.ManateeRenderer;
import tech.thatgravyboat.goodall.client.renderer.rhino.RhinoRenderer;
import tech.thatgravyboat.goodall.client.renderer.seal.SealRenderer;
import tech.thatgravyboat.goodall.client.renderer.whitedeer.WhiteDeerRenderer;
import tech.thatgravyboat.goodall.common.entity.base.IEntityModel;
import tech.thatgravyboat.goodall.common.registry.ModBlocks;
import tech.thatgravyboat.goodall.common.registry.ModEntities;
import tech.thatgravyboat.goodall.common.registry.ModItems;

public class GoodallClient {

    public static void init() {
        registerEntityRenderer(ModEntities.RHINO.get(), RhinoRenderer::new);
        registerEntityRenderer(ModEntities.DUMBO.get(), DumboRenderer::new);
        registerEntityRenderer(ModEntities.BOOBY.get(), createRenderer(new BaseModel<>()));
        registerEntityRenderer(ModEntities.FENNEC_FOX.get(), FennecFoxRenderer::new);
        registerEntityRenderer(ModEntities.KIWI.get(), createRenderer(new BaseModel<>()));
        registerEntityRenderer(ModEntities.MANATEE.get(), ManateeRenderer::new);
        registerEntityRenderer(ModEntities.SEAL.get(), SealRenderer::new);
        registerEntityRenderer(ModEntities.WHITE_DEER.get(), WhiteDeerRenderer::new);
        registerEntityRenderer(ModEntities.RED_DEER.get(), createRenderer(new BaseModel<>()));
        registerEntityRenderer(ModEntities.FLAMINGO.get(), FlamingoRenderer::new);
        registerDeerHeadBlockRenderer(ModBlocks.DEER_HEAD_ENTITY.get());
        registerItemRenderer(ModItems.DEER_HEAD.get(), new DeerHeadBlockItemRenderer());
        registerBlockLayer(ModBlocks.CROSS.get(), RenderLayer.getTranslucent());
    }

    private static <T extends MobEntity & IAnimatable & IEntityModel> EntityRendererFactory<T> createRenderer(AnimatedGeoModel<T> model) {
        return ctx -> new BaseRenderer<>(ctx, model);
    }

    @ExpectPlatform
    public static void registerBlockLayer(Block block, RenderLayer layer) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Entity> void registerEntityRenderer(EntityType<T> type, EntityRendererFactory<T> factory) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerDeerHeadBlockRenderer(BlockEntityType<?> block) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerItemRenderer(Item item, GeoItemRenderer renderer) {
        throw new AssertionError();
    }
}
