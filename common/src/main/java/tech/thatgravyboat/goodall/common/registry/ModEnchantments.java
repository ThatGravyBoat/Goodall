package tech.thatgravyboat.goodall.common.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.enchantment.Enchantment;
import tech.thatgravyboat.goodall.common.enchantments.SandWalkerEnchantment;

import java.util.function.Supplier;

public class ModEnchantments {

    public static final Supplier<SandWalkerEnchantment> SAND_WALKER = registerEnchant("sand_walker", SandWalkerEnchantment::new);

    public static void register() {
        //Initialize Class
    }

    @ExpectPlatform
    public static <T extends Enchantment> Supplier<T> registerEnchant(String id, Supplier<T> enchantment) {
        throw new AssertionError();
    }
}
