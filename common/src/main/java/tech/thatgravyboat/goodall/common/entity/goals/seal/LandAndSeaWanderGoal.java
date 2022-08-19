package tech.thatgravyboat.goodall.common.entity.goals.seal;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class LandAndSeaWanderGoal extends RandomStrollGoal {

    public LandAndSeaWanderGoal(PathfinderMob mob, double speed) {
        super(mob, speed);
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        return this.mob.isUnderWater() ? BehaviorUtils.getRandomSwimmablePos(this.mob, 10, 7) : super.getPosition();
    }
}
