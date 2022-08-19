package tech.thatgravyboat.goodall.client.fabric;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import tech.thatgravyboat.goodall.client.GoodallClient;
import tech.thatgravyboat.goodall.client.renderer.deerhead.DeerHeadBlockModel;

import java.util.function.Supplier;

public class GoodallClientImpl {
    public static <T extends Entity> void registerEntityRenderer(EntityType<T> type, EntityRendererProvider<T> factory) {
        EntityRendererRegistry.register(type, factory);
    }

    public static void registerBlockLayer(Block block, RenderType layer) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, layer);
    }

    public static void registerDeerHeadBlockRenderer(BlockEntityType<?> block) {
        BlockEntityRendererRegistry.register(block, context -> new DeerHeadBlockRenderer(new DeerHeadBlockModel()));
    }

    public static void registerItemRenderer(Item item, GeoItemRenderer renderer) {
        GeoItemRenderer.registerItemRenderer(item, renderer);
    }

    public static void registerParticleFactory(Supplier<SimpleParticleType> particle, GoodallClient.SpriteAwareFactory<SimpleParticleType> factory) {
        ParticleFactoryRegistry.getInstance().register(particle.get(), factory::create);
    }
}
