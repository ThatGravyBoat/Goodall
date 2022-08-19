package tech.thatgravyboat.goodall.common.entity.goals;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import tech.thatgravyboat.goodall.common.entity.base.ItemPicker;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class PickupItemGoal<T extends Mob & ItemPicker> extends Goal {

    private int cooldown;

    private final double speed;
    private final double range;
    private final Predicate<ItemEntity> pickupPredicate;
    private final T mob;

    public PickupItemGoal(T mob, double speed) {
        this(mob, speed, item -> !item.hasPickUpDelay() && item.isAlive() && mob.canHoldItem(item.getItem()));
    }

    public PickupItemGoal(T mob, double speed, Predicate<ItemEntity> pickupPredicate) {
        this(mob, speed, pickupPredicate, 8D);
    }

    public PickupItemGoal(T mob, double speed, Predicate<ItemEntity> pickupPredicate, double range) {
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.mob = mob;
        this.pickupPredicate = pickupPredicate;
        this.range = range;
        this.speed = speed;

        this.cooldown = 100;
    }

    @Override
    public boolean canUse() {
        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }

        if (mob.canPickUpItems() && mob.getTarget() == null && mob.getLastHurtMob() == null) {
            return !this.mob.level.getEntitiesOfClass(ItemEntity.class, this.mob.getBoundingBox().inflate(8.0D), pickupPredicate).isEmpty();
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return mob.canPickUpItems() && mob.getTarget() == null && mob.getLastHurtMob() == null;
    }

    @Override
    public void tick() {
        goToNearbyItems();
    }

    @Override
    public void start() {
        goToNearbyItems();
    }

    @Override
    public void stop() {
        this.cooldown = 100;
    }

    public void goToNearbyItems() {
        List<ItemEntity> itemsNearby = getItemsNearby();
        if (!itemsNearby.isEmpty()) this.mob.getNavigation().moveTo(itemsNearby.get(0), this.speed);
    }

    private List<ItemEntity> getItemsNearby() {
        return this.mob.level.getEntitiesOfClass(ItemEntity.class, this.mob.getBoundingBox().inflate(this.range), pickupPredicate);
    }
}
