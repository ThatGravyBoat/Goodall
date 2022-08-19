package tech.thatgravyboat.goodall.common.registry.forge;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import tech.thatgravyboat.goodall.Goodall;

import java.util.function.Supplier;

public class ModParticlesImpl {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Goodall.MOD_ID);

    public static Supplier<SimpleParticleType> registerParticle(String name, Supplier<SimpleParticleType> particleSupplier) {
        return PARTICLES.register(name, particleSupplier);
    }
}
