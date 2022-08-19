package tech.thatgravyboat.goodall.common.registry.fabric;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import tech.thatgravyboat.goodall.Goodall;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class ModParticlesImpl {

    public static final Set<String> TEXTURES = new HashSet<>();

    public static Supplier<SimpleParticleType> registerParticle(String name, Supplier<SimpleParticleType> particleSupplier) {
        TEXTURES.add(name);
        var register = Registry.register(Registry.PARTICLE_TYPE, new ResourceLocation(Goodall.MOD_ID, name), particleSupplier.get());
        return () -> register;
    }
}
