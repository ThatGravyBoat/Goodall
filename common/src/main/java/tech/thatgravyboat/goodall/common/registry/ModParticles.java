package tech.thatgravyboat.goodall.common.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.function.Supplier;

public class ModParticles {

    public static final Supplier<SimpleParticleType> KRILL = registerParticle("krill", () -> new SimpleParticleType(false){});

    public static void register() {
        //initialize class
    }

    @ExpectPlatform
    public static Supplier<SimpleParticleType> registerParticle(String name, Supplier<SimpleParticleType> particleSupplier) {
        throw new AssertionError();
    }
}
