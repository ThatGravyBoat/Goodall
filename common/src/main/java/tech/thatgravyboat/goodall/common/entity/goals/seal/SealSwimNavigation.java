package tech.thatgravyboat.goodall.common.entity.goals.seal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import tech.thatgravyboat.goodall.common.entity.SealEntity;

public class SealSwimNavigation extends WaterBoundPathNavigation {

    public SealSwimNavigation(SealEntity owner, Level level) {
        super(owner, level);
    }

    @Override
    protected boolean canUpdatePath() {
        return true;
    }

    @Override
    protected PathFinder createPathFinder(int range) {
        this.nodeEvaluator = new AmphibiousNodeEvaluator(false);
        return new PathFinder(this.nodeEvaluator, range);
    }

    @Override
    public boolean isStableDestination(BlockPos pos) {
        return !this.level.getBlockState(pos.below()).isAir();
    }
}
