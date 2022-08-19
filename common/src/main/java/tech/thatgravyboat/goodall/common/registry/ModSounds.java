package tech.thatgravyboat.goodall.common.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class ModSounds {

    public static final Supplier<SoundEvent> FLAMINGO_AMBIENT = register("flamingo.ambient");
    public static final Supplier<SoundEvent> FLAMINGO_HURT = register("flamingo.hurt");
    public static final Supplier<SoundEvent> PELICAN_AMBIENT = register("pelican.ambient");
    public static final Supplier<SoundEvent> PELICAN_HURT = register("pelican.hurt");
    public static final Supplier<SoundEvent> SEAL_AMBIENT = register("seal.ambient");
    public static final Supplier<SoundEvent> SEAL_HURT = register("seal.hurt");

    public static void register() {
        //Initialize class.
    }

    @ExpectPlatform
    public static Supplier<SoundEvent> register(String id) {
        throw new AssertionError();
    }
}
