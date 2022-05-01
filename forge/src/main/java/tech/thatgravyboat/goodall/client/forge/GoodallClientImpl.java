package tech.thatgravyboat.goodall.client.forge;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import tech.thatgravyboat.goodall.client.renderer.deerhead.DeerHeadBlockModel;

public class GoodallClientImpl {

    public static <T extends Entity> void registerEntityRenderer(EntityType<T> type, EntityRendererFactory<T> factory) {
        EntityRenderers.register(type, factory);
    }

    public static void registerBlockLayer(Block block, RenderLayer layer) {
        RenderLayers.setRenderLayer(block, layer);
    }

    public static void registerDeerHeadBlockRenderer(BlockEntityType<?> block) {
        BlockEntityRendererFactories.register(block, context -> new DeerHeadBlockRenderer(context, new DeerHeadBlockModel()));
    }

    public static void registerItemRenderer(Item item, GeoItemRenderer renderer) {
        //Do nothing as forge handles this differently
    }
}
