package tech.thatgravyboat.goodall.common.entity.goals;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.ConfiguredStructureFeatureTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import tech.thatgravyboat.goodall.common.entity.BoobyEntity;
import tech.thatgravyboat.goodall.common.registry.ModBlocks;

import java.util.EnumSet;

public class FindTreasure extends Goal {

    private final BoobyEntity mob;
    private boolean noPathToStructure;

    public FindTreasure(BoobyEntity dolphin) {
        this.mob = dolphin;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    public boolean canStop() {
        return false;
    }

    public boolean canStart() {
        return this.mob.hasFish();
    }

    public boolean shouldContinue() {
        BlockPos blockPos = this.mob.getTreasurePos();
        return !(new BlockPos(blockPos.getX(), this.mob.getY(), blockPos.getZ())).isWithinDistance(this.mob.getPos(), 4.0D) && !this.noPathToStructure;
    }

    public void start() {
        if (this.mob.world instanceof ServerWorld serverWorld) {
            this.noPathToStructure = false;
            this.mob.getNavigation().stop();
            BlockPos blockPos2 = serverWorld.locateStructure(ConfiguredStructureFeatureTags.ON_TREASURE_MAPS, this.mob.getBlockPos(), 50, false);
            if (blockPos2 != null) {
                blockPos2 = goUpTillAir(serverWorld, blockPos2, 10);
            }
            if (blockPos2 != null) {
                this.mob.setTreasurePos(blockPos2);
            } else {
                this.noPathToStructure = true;
            }
        }
    }

    public void stop() {
        World world = this.mob.world;
        BlockPos blockPos = this.mob.getTreasurePos();
        if ((new BlockPos(blockPos.getX(), this.mob.getY(), blockPos.getZ())).isWithinDistance(this.mob.getPos(), 4.0D) || this.noPathToStructure) {
            if (!this.noPathToStructure) {
                if (this.mob.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                    BlockState down = world.getBlockState(blockPos.down());
                    if (world.isTopSolid(blockPos.down(), this.mob) && world.isAir(blockPos) && !down.isOf(ModBlocks.CROSS.get())) {
                        world.setBlockState(blockPos, ModBlocks.CROSS.get().getDefaultState());
                    }
                }
                world.sendEntityStatus(this.mob, (byte)38);
            }
            this.mob.setFish(false);
        }
    }

    public void tick() {
        if (!isNearTarget() || this.mob.getNavigation().isIdle()) {
            Vec3d vec3d = Vec3d.ofCenter(this.mob.getTreasurePos());
            this.mob.getLookControl().lookAt(vec3d.x, vec3d.y, vec3d.z, (float)(this.mob.getMaxHeadRotation() + 20), (float)this.mob.getMaxLookPitchChange());
            this.mob.getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, 1D);
        }
    }

    protected boolean isNearTarget() {
        BlockPos blockPos = this.mob.getNavigation().getTargetPos();
        return blockPos != null && blockPos.isWithinDistance(this.mob.getPos(), 12.0D);
    }

    public BlockPos goUpTillAir(World world, BlockPos pos, int maxAmount) {
        BlockPos topMost = new BlockPos(pos.getX(), world.getTopY(Heightmap.Type.WORLD_SURFACE, pos.getX(), pos.getZ()), pos.getZ());
        if (!world.isAir(topMost)) return null;
        BlockPos.Mutable newPos = topMost.mutableCopy();

        for (int i = 0; i < maxAmount; i++) {
            BlockEntity possibleChest = world.getBlockEntity(newPos.move(Direction.DOWN));
            if (possibleChest instanceof LootableContainerBlockEntity)
                return topMost;
        }
        return null;
    }
}
