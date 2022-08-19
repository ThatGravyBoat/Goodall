package tech.thatgravyboat.goodall.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ReturnableEdibleItem extends Item {

    private final Item returningItem;

    public ReturnableEdibleItem(Item returningItem, Properties settings) {
        super(settings);
        this.returningItem = returningItem;
    }

    @Override
    public ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level world, @NotNull LivingEntity user) {
        super.finishUsingItem(stack, world, user);
        if (user instanceof ServerPlayer serverPlayerEntity) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
            serverPlayerEntity.awardStat(Stats.ITEM_USED.get(this));
        }

        ItemStack itemToReturn = new ItemStack(this.returningItem);

        if (stack.isEmpty()) return itemToReturn;

        if (user instanceof Player player && !player.getAbilities().instabuild && !player.getInventory().add(itemToReturn)) {
            player.drop(itemToReturn, false);
        }

        return stack;
    }
}
