package tech.thatgravyboat.goodall.common.entity.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import tech.thatgravyboat.goodall.common.entity.base.FleeingEntity;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class LeapingFleeGoal<T extends LivingEntity, M extends PathAwareEntity & FleeingEntity> extends Goal {

    protected final M mob;
    private final double slowSpeed;
    private final double fastSpeed;
    @Nullable
    protected T targetEntity;
    protected final float fleeDistance;
    @Nullable
    protected Path fleePath;
    protected final EntityNavigation fleeingEntityNavigation;
    protected final Class<T> classToFleeFrom;
    protected final Predicate<LivingEntity> extraInclusionSelector;
    protected final Predicate<LivingEntity> inclusionSelector;
    private final TargetPredicate withinRangePredicate;

    public LeapingFleeGoal(M mob, Class<T> fleeFromType, Predicate<LivingEntity> extraInclusionSelector, float distance, double slowSpeed, double fastSpeed, Predicate<LivingEntity> inclusionSelector) {
        this.mob = mob;
        this.classToFleeFrom = fleeFromType;
        this.extraInclusionSelector = extraInclusionSelector;
        this.fleeDistance = distance;
        this.slowSpeed = slowSpeed;
        this.fastSpeed = fastSpeed;
        this.inclusionSelector = inclusionSelector;
        this.fleeingEntityNavigation = mob.getNavigation();
        this.setControls(EnumSet.of(Control.MOVE));
        this.withinRangePredicate = TargetPredicate.createNonAttackable().setBaseMaxDistance(distance).setPredicate(inclusionSelector.and(extraInclusionSelector));
    }

    public LeapingFleeGoal(M fleeingEntity, Class<T> classToFleeFrom, float fleeDistance, double fleeSlowSpeed, double fleeFastSpeed, Predicate<LivingEntity> inclusionSelector) {
        this(fleeingEntity, classToFleeFrom, (livingEntity) -> true, fleeDistance, fleeSlowSpeed, fleeFastSpeed, inclusionSelector);
    }

    @Override
    public boolean canStart() {
        List<T> nearbyEntities = this.mob.world.getEntitiesByClass(this.classToFleeFrom, this.mob.getBoundingBox().expand(this.fleeDistance, 3.0D, this.fleeDistance), (livingEntity) -> true);
        this.targetEntity = this.mob.world.getClosestEntity(nearbyEntities, this.withinRangePredicate, this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ());
        if (this.targetEntity != null)  {
            Vec3d vec3d = NoPenaltyTargeting.findFrom(this.mob, 16, 7, this.targetEntity.getPos());
            if (vec3d == null) {
                return false;
            } else if (this.targetEntity.squaredDistanceTo(vec3d.x, vec3d.y, vec3d.z) < this.targetEntity.squaredDistanceTo(this.mob)) {
                return false;
            } else {
                this.fleePath = this.fleeingEntityNavigation.findPathTo(vec3d.x, vec3d.y, vec3d.z, 0);
                return this.fleePath != null;
            }
        }

        return false;
    }

    @Override
    public boolean shouldContinue() {
        return !this.fleeingEntityNavigation.isIdle();
    }

    @Override
    public void start() {
        this.fleeingEntityNavigation.startMovingAlong(this.fleePath, this.slowSpeed);
        this.mob.setFleeing(true);
    }

    @Override
    public void stop() {
        this.targetEntity = null;
        this.mob.setFleeing(false);
    }

    @Override
    public void tick() {
        this.mob.getNavigation().setSpeed(this.mob.squaredDistanceTo(this.targetEntity) < 49.0D ? this.fastSpeed : this.slowSpeed);
    }
}
