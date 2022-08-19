package tech.thatgravyboat.goodall.common.entity.goals.tortoise;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.level.GameRules;
import tech.thatgravyboat.goodall.common.entity.TortoiseEntity;

public class MateGoal extends BreedGoal {

    private final TortoiseEntity turtle;

    public MateGoal(TortoiseEntity turtle, double speed) {
        super(turtle, speed);
        this.turtle = turtle;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && !this.turtle.hasEgg();
    }

    @Override
    protected void breed() {
        assert this.partner != null;
        ServerPlayer serverPlayerEntity = this.animal.getLoveCause();
        if (serverPlayerEntity == null && this.partner.getLoveCause() != null) {
            serverPlayerEntity = this.partner.getLoveCause();
        }

        if (serverPlayerEntity != null) {
            serverPlayerEntity.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(serverPlayerEntity, this.animal, this.partner, null);
        }

        this.turtle.setHasEgg(true);
        this.animal.resetLove();
        this.partner.resetLove();
        RandomSource random = this.animal.getRandom();
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.level.addFreshEntity(new ExperienceOrb(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), random.nextInt(7) + 1));
        }

    }

}
