package tech.thatgravyboat.goodall.common.entity.goals.seal;

import net.minecraft.entity.ai.pathing.AmphibiousPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tech.thatgravyboat.goodall.common.entity.SealEntity;

public class SealSwimNavigation extends SwimNavigation {

    public SealSwimNavigation(SealEntity owner, World world) {
        super(owner, world);
    }

    @Override
    protected boolean isAtValidPosition() {
        return true;
    }

    @Override
    protected PathNodeNavigator createPathNodeNavigator(int range) {
        this.nodeMaker = new AmphibiousPathNodeMaker(false);
        return new PathNodeNavigator(this.nodeMaker, range);
    }

    @Override
    public boolean isValidPosition(BlockPos pos) {
        return !this.world.getBlockState(pos.down()).isAir();
    }
}
