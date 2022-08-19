package tech.thatgravyboat.goodall.common.entity.goals;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import tech.thatgravyboat.goodall.common.entity.base.Sleeping;

import java.util.EnumSet;

public class SleepingGoal<T extends Mob & Sleeping> extends Goal {

    private final T sleepingEntity;

    public SleepingGoal(T sleepingEntity) {
        this.sleepingEntity = sleepingEntity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return sleepingEntity.isSleeping();
    }

    @Override
    public void start() {
        sleepingEntity.getNavigation().stop();
    }
}
