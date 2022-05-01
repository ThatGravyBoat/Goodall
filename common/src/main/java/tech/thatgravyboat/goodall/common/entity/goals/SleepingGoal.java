package tech.thatgravyboat.goodall.common.entity.goals;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import tech.thatgravyboat.goodall.common.entity.base.Sleeping;

import java.util.EnumSet;

public class SleepingGoal<T extends MobEntity & Sleeping> extends Goal {

    private final T sleepingEntity;

    public SleepingGoal(T sleepingEntity) {
        this.sleepingEntity = sleepingEntity;
        this.setControls(EnumSet.of(Control.MOVE, Control.JUMP, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return sleepingEntity.isSleeping();
    }

    @Override
    public void start() {
        sleepingEntity.getNavigation().stop();
    }
}
