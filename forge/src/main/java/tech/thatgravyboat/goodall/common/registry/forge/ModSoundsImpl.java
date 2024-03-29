package tech.thatgravyboat.goodall.common.registry.forge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import tech.thatgravyboat.goodall.Goodall;

import java.util.function.Supplier;

public class ModSoundsImpl {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Goodall.MOD_ID);

    public static Supplier<SoundEvent> register(String id) {
        return SOUNDS.register(id, () -> new SoundEvent(new ResourceLocation(Goodall.MOD_ID, id)));
    }
}
