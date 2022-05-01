package tech.thatgravyboat.goodall.common.entity.goals.seal;

import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class LandAndSeaWanderGoal extends WanderAroundGoal {

    public LandAndSeaWanderGoal(PathAwareEntity mob, double speed) {
        super(mob, speed);
    }

    @Nullable
    @Override
    protected Vec3d getWanderTarget() {
        return this.mob.isSubmergedInWater() ? LookTargetUtil.find(this.mob, 10, 7) : super.getWanderTarget();
    }
}
