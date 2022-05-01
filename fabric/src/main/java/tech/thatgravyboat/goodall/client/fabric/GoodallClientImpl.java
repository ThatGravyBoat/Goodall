package tech.thatgravyboat.goodall.client.fabric;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import tech.thatgravyboat.goodall.client.renderer.deerhead.DeerHeadBlockModel;

public class GoodallClientImpl {
    public static <T extends Entity> void registerEntityRenderer(EntityType<T> type, EntityRendererFactory<T> factory) {
        EntityRendererRegistry.register(type, factory);
    }

    public static void registerBlockLayer(Block block, RenderLayer layer) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, layer);
    }

    public static void registerDeerHeadBlockRenderer(BlockEntityType<?> block) {
        BlockEntityRendererRegistry.register(block, context -> new DeerHeadBlockRenderer(new DeerHeadBlockModel()));
    }

    public static void registerItemRenderer(Item item, GeoItemRenderer renderer) {
        GeoItemRenderer.registerItemRenderer(item, renderer);
    }
}
