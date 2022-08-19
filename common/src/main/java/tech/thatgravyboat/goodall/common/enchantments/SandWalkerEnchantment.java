package tech.thatgravyboat.goodall.common.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SandWalkerEnchantment extends Enchantment {
    public SandWalkerEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[]{ EquipmentSlot.FEET });
    }
}
