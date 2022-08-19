package tech.thatgravyboat.goodall.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import tech.thatgravyboat.goodall.client.particles.KrillParticle;
import tech.thatgravyboat.goodall.client.renderer.base.BaseRenderer;
import tech.thatgravyboat.goodall.client.renderer.deerhead.DeerHeadBlockItemRenderer;
import tech.thatgravyboat.goodall.client.renderer.dumbo.DumboRenderer;
import tech.thatgravyboat.goodall.client.renderer.fennecfox.FennecFoxRenderer;
import tech.thatgravyboat.goodall.common.registry.ModBlocks;
import tech.thatgravyboat.goodall.common.registry.ModEntities;
import tech.thatgravyboat.goodall.common.registry.ModItems;
import tech.thatgravyboat.goodall.common.registry.ModParticles;

import java.util.function.Supplier;

public class GoodallClient {

    public static void init() {
        registerEntityRenderer(ModEntities.RHINO.get(), BaseRenderer::ofBabyBase);
        registerEntityRenderer(ModEntities.DUMBO.get(), DumboRenderer::new);
        registerEntityRenderer(ModEntities.PELICAN.get(), BaseRenderer::ofBase);
        registerEntityRenderer(ModEntities.FENNEC_FOX.get(), FennecFoxRenderer::new);
        registerEntityRenderer(ModEntities.KIWI.get(), BaseRenderer::ofBase);
        registerEntityRenderer(ModEntities.MANATEE.get(), BaseRenderer::ofBabyBase);
        registerEntityRenderer(ModEntities.SEAL.get(), BaseRenderer::ofBase);
        registerEntityRenderer(ModEntities.DEER.get(), BaseRenderer::ofBase);
        registerEntityRenderer(ModEntities.FLAMINGO.get(), BaseRenderer::ofBabyBase);
        registerEntityRenderer(ModEntities.SONGBIRD.get(), BaseRenderer::ofBase);
        registerEntityRenderer(ModEntities.TOUCAN.get(), BaseRenderer::ofBase);
        registerEntityRenderer(ModEntities.TORTOISE.get(), BaseRenderer::ofBabyBase);
        registerEntityRenderer(ModEntities.KRILL.get(), NoopRenderer::new);
        registerDeerHeadBlockRenderer(ModBlocks.DEER_HEAD_ENTITY.get());
        registerItemRenderer(ModItems.DEER_HEAD.get(), new DeerHeadBlockItemRenderer());
        registerBlockLayer(ModBlocks.CROSS.get(), RenderType.translucent());
    }

    public static void initParticleFactories() {
        registerParticleFactory(ModParticles.KRILL, KrillParticle.Factory::new);
    }

    @ExpectPlatform
    public static void registerBlockLayer(Block block, RenderType layer) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Entity> void registerEntityRenderer(EntityType<T> type, EntityRendererProvider<T> factory) {
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

    @ExpectPlatform
    public static void registerParticleFactory(Supplier<SimpleParticleType> particle, SpriteAwareFactory<SimpleParticleType> factory) {
        throw new AssertionError();
    }

    @FunctionalInterface
    @Environment(EnvType.CLIENT)
    public interface SpriteAwareFactory<T extends ParticleOptions> {
        @NotNull ParticleProvider<T> create(SpriteSet spriteProvider);
    }
}
