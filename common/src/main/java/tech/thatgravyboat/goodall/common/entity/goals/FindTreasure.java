package tech.thatgravyboat.goodall.common.entity.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import tech.thatgravyboat.goodall.common.entity.PelicanEntity;
import tech.thatgravyboat.goodall.common.registry.ModBlocks;

import java.util.EnumSet;

public class FindTreasure extends Goal {

    private final PelicanEntity mob;
    private boolean noPathToStructure;

    public FindTreasure(PelicanEntity dolphin) {
        this.mob = dolphin;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public boolean canUse() {
        return this.mob.hasFish();
    }

    @Override
    public boolean canContinueToUse() {
        BlockPos blockPos = this.mob.getTreasurePos();
        return !(new BlockPos(blockPos.getX(), this.mob.getY(), blockPos.getZ())).closerToCenterThan(this.mob.position(), 4.0D) && !this.noPathToStructure;
    }

    @Override
    public void start() {
        if (this.mob.level instanceof ServerLevel serverLevel) {
            this.noPathToStructure = false;
            this.mob.getNavigation().stop();
            BlockPos blockPos2 = serverLevel.findNearestMapStructure(StructureTags.ON_TREASURE_MAPS, this.mob.blockPosition(), 50, false);
            if (blockPos2 != null) {
                blockPos2 = goUpTillAir(serverLevel, blockPos2, 10);
            }
            if (blockPos2 != null) {
                this.mob.setTreasurePos(blockPos2);
            } else {
                this.noPathToStructure = true;
            }
        }
    }

    @Override
    public void stop() {
        Level level = this.mob.level;
        BlockPos blockPos = this.mob.getTreasurePos();
        if ((new BlockPos(blockPos.getX(), this.mob.getY(), blockPos.getZ())).closerToCenterThan(this.mob.position(), 4.0D) || this.noPathToStructure) {
            if (!this.noPathToStructure) {
                if (level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                    BlockState down = level.getBlockState(blockPos.below());
                    if (level.loadedAndEntityCanStandOn(blockPos.below(), this.mob) && level.isEmptyBlock(blockPos) && !down.is(ModBlocks.CROSS.get())) {
                        level.setBlockAndUpdate(blockPos, ModBlocks.CROSS.get().defaultBlockState());
                    }
                }
                level.broadcastEntityEvent(this.mob, (byte)38);
            }
            this.mob.setFish(false);
        }
    }

    @Override
    public void tick() {
        if (!isNearTarget() || this.mob.getNavigation().isDone()) {
            Vec3 vec3d = Vec3.atCenterOf(this.mob.getTreasurePos());
            this.mob.getLookControl().setLookAt(vec3d.x, vec3d.y, vec3d.z, (float)(this.mob.getMaxHeadYRot() + 20), (float)this.mob.getMaxHeadXRot());
            this.mob.getNavigation().moveTo(vec3d.x, vec3d.y, vec3d.z, 1D);
        }
    }

    protected boolean isNearTarget() {
        BlockPos blockPos = this.mob.getNavigation().getTargetPos();
        return blockPos != null && blockPos.closerToCenterThan(this.mob.position(), 12.0D);
    }

    public BlockPos goUpTillAir(Level level, BlockPos pos, int maxAmount) {
        BlockPos topMost = new BlockPos(pos.getX(), level.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ()), pos.getZ());
        if (!level.isEmptyBlock(topMost)) return null;
        BlockPos.MutableBlockPos newPos = topMost.mutable();

        for (int i = 0; i < maxAmount; i++) {
            BlockEntity possibleChest = level.getBlockEntity(newPos.move(Direction.DOWN));
            if (possibleChest instanceof RandomizableContainerBlockEntity)
                return topMost;
        }
        return null;
    }
}
