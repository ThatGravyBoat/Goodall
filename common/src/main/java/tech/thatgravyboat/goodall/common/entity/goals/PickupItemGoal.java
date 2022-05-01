package tech.thatgravyboat.goodall.common.entity.goals;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import tech.thatgravyboat.goodall.common.entity.base.ItemPicker;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class PickupItemGoal<T extends MobEntity & ItemPicker> extends Goal {

    private int cooldown;

    private final double speed;
    private final double range;
    private final Predicate<ItemEntity> pickupPredicate;
    private final T mob;

    public PickupItemGoal(T mob, double speed) {
        this(mob, speed, item -> !item.cannotPickup() && item.isAlive() && mob.canPickupItem(item.getStack()));
    }

    public PickupItemGoal(T mob, double speed, Predicate<ItemEntity> pickupPredicate) {
        this(mob, speed, pickupPredicate, 8D);
    }

    public PickupItemGoal(T mob, double speed, Predicate<ItemEntity> pickupPredicate, double range) {
        this.setControls(EnumSet.of(Control.MOVE));
        this.mob = mob;
        this.pickupPredicate = pickupPredicate;
        this.range = range;
        this.speed = speed;

        this.cooldown = 100;
    }

    @Override
    public boolean canStart() {
        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }

        if (mob.canPickUpItems() && mob.getTarget() == null && mob.getAttacker() == null) {
            return !this.mob.world.getEntitiesByClass(ItemEntity.class, this.mob.getBoundingBox().expand(8.0D), pickupPredicate).isEmpty();
        }
        return false;
    }

    @Override
    public boolean shouldContinue() {
        return mob.canPickUpItems() && mob.getTarget() == null && mob.getAttacker() == null;
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
        if (!itemsNearby.isEmpty()) this.mob.getNavigation().startMovingTo(itemsNearby.get(0), this.speed);
    }

    private List<ItemEntity> getItemsNearby() {
        return this.mob.world.getEntitiesByClass(ItemEntity.class, this.mob.getBoundingBox().expand(this.range), pickupPredicate);
    }
}
