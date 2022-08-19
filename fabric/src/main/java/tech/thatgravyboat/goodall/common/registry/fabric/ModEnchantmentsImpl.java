package tech.thatgravyboat.goodall.common.registry.fabric;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import tech.thatgravyboat.goodall.Goodall;

import java.util.function.Supplier;

public class ModEnchantmentsImpl {
    public static <T extends Enchantment> Supplier<T> registerEnchant(String id, Supplier<T> enchantment) {
        var register = Registry.register(Registry.ENCHANTMENT, new ResourceLocation(Goodall.MOD_ID, id), enchantment.get());
        return () -> register;
    }
}
