package tech.thatgravyboat.goodall.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class KrillParticle extends TextureSheetParticle {

    protected KrillParticle(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider, double a, double b, double c) {
        super(world, x, y, z, a, b, c);
        this.setSize(1f, 1f);
        this.lifetime = 100;
        this.friction = 0.01F;
        this.hasPhysics = false;
        this.setSpriteFromAge(spriteProvider);
    }

    @Override
    public float getQuadSize(float tickDelta) {
        return this.quadSize * (1f - ((float) this.age / this.lifetime));
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }


    public record Factory(SpriteSet spriteProvider) implements ParticleProvider<SimpleParticleType> {
        @Override
        public @NotNull Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new KrillParticle(world, x, y, z, spriteProvider, velocityX, velocityY, velocityZ);
        }
    }
}
