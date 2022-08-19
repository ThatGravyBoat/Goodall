package tech.thatgravyboat.goodall.common.entity.goals.tortoise;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import tech.thatgravyboat.goodall.common.block.TortoiseEggBlock;
import tech.thatgravyboat.goodall.common.entity.TortoiseEntity;
import tech.thatgravyboat.goodall.common.registry.ModBlocks;

public class LayEggGoal extends MoveToBlockGoal {

    private final TortoiseEntity turtle;
    private int digging = 0;

    public LayEggGoal(TortoiseEntity turtle, double speed) {
        super(turtle, speed, 16);
        this.turtle = turtle;
    }

    @Override
    public boolean canUse() {
        return this.turtle.hasEgg() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.turtle.hasEgg();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.turtle.isInWater() && this.isReachedTarget() && this.turtle.hasEgg()) {
            if (this.digging < 1) {
                this.digging = 1;
            } else if (this.digging > this.adjustedTickDelay(200)) {
                Level level = this.turtle.level;
                level.playSound(null, this.turtle.blockPosition(), SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3F, 0.9F + level.random.nextFloat() * 0.2F);
                level.setBlock(this.blockPos.above(), ModBlocks.TORTOISE_EGG.get().defaultBlockState()
                        .setValue(TortoiseEggBlock.EGGS, this.turtle.getRandom().nextInt(4) + 1), Block.UPDATE_ALL);
                this.turtle.setHasEgg(false);
                this.digging = 0;
                this.turtle.setInLoveTime(600);
            }

            if (this.digging > 0) {
                this.digging++;
                this.turtle.level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, this.blockPos, Block.getId(this.turtle.level.getBlockState(this.blockPos)));
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        this.digging = 0;
    }

    @Override
    protected boolean isValidTarget(LevelReader world, BlockPos pos) {
        return world.isEmptyBlock(pos.above()) && TurtleEggBlock.isSand(world, pos);
    }
}
