package tech.thatgravyboat.goodall.common.registry.forge;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import tech.thatgravyboat.goodall.Goodall;

import java.util.function.Supplier;

public class ModEnchantmentsImpl {

    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Goodall.MOD_ID);

    public static <T extends Enchantment> Supplier<T> registerEnchant(String id, Supplier<T> enchantment) {
        return ENCHANTMENTS.register(id, enchantment);
    }
}
