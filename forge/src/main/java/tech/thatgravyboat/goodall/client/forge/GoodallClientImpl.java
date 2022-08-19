package tech.thatgravyboat.goodall.client.forge;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import tech.thatgravyboat.goodall.client.GoodallClient;
import tech.thatgravyboat.goodall.client.renderer.deerhead.DeerHeadBlockModel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GoodallClientImpl {

    public static final Map<Supplier<SimpleParticleType>, GoodallClient.SpriteAwareFactory<SimpleParticleType>> PARTICLES = new HashMap<>();

    public static <T extends Entity> void registerEntityRenderer(EntityType<T> type, EntityRendererProvider<T> factory) {
        EntityRenderers.register(type, factory);
    }

    public static void registerBlockLayer(Block block, RenderType layer) {
        ItemBlockRenderTypes.setRenderLayer(block, layer);
    }

    public static void registerDeerHeadBlockRenderer(BlockEntityType<?> block) {
        BlockEntityRenderers.register(block, context -> new DeerHeadBlockRenderer(context, new DeerHeadBlockModel()));
    }

    public static void registerItemRenderer(Item item, GeoItemRenderer renderer) {
        //Do nothing as forge handles this differently
    }

    public static void registerParticleFactory(Supplier<SimpleParticleType> particle, GoodallClient.SpriteAwareFactory<SimpleParticleType> factory) {
        PARTICLES.put(particle, factory);
    }
}
