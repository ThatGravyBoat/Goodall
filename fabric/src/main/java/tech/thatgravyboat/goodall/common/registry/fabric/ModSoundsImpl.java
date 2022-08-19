package tech.thatgravyboat.goodall.common.registry.fabric;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import tech.thatgravyboat.goodall.Goodall;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModSoundsImpl {

    public static final Map<String, Supplier<SoundEvent>> SOUNDS = new LinkedHashMap<>();

    public static Supplier<SoundEvent> register(String id) {
        SoundEvent sound = new SoundEvent(new ResourceLocation(Goodall.MOD_ID, id));
        SOUNDS.put(id, () -> sound);
        return () -> sound;
    }
}
