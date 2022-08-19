package tech.thatgravyboat.goodall.common.entity.goals;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import tech.thatgravyboat.goodall.common.entity.RhinoEntity;

import java.util.ArrayList;
import java.util.List;

public class StompFireGoal extends MoveToBlockGoal {

    private final List<BlockPos> offsets = new ArrayList<>();
    private final RhinoEntity rhino;
    private int counter = 0;

    public StompFireGoal(RhinoEntity mob, double speed, int maxYDifference) {
        super(mob, speed, maxYDifference);
        this.rhino = mob;

        //WE cache this list of offsets instead of making it each time, kinda stupid Mojang dont do this,
        //like if it needs to be dynamic enough just recreate it when the values update not when it's used.
        for(int k = 0; k <= maxYDifference; k = k > 0 ? -k : 1 - k) {
            for(int l = 0; l < 24; ++l) {
                for(int m = 0; m <= l; m = m > 0 ? -m : 1 - m) {
                    for(int n = m < l && m > -l ? l : 0; n <= l; n = n > 0 ? -n : 1 - n) {
                        offsets.add(new BlockPos(m, k - 1, n));
                    }
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (isReachedTarget()) {
            this.rhino.lookAt(EntityAnchorArgument.Anchor.FEET, Vec3.atCenterOf(this.blockPos));
            this.rhino.setImmuneToFire(true);
            this.rhino.setStomping(true);
            if (this.counter == 0) {
                this.counter = 50;
            }

            Level level = this.rhino.level;

            if (this.counter == 2 && isValidTarget(level, this.blockPos)) {
                if (level instanceof ServerLevel serverLevel) {
                    BlockState state = level.getBlockState(this.blockPos.below());
                    serverLevel.sendParticles(
                            new BlockParticleOption(ParticleTypes.BLOCK, state),
                            this.rhino.getX(), this.rhino.getY(), this.rhino.getZ(),
                            100,
                            0.75, 0, 0.75,
                            5
                    );
                    serverLevel.playSound(null, this.blockPos, SoundEvents.HOGLIN_STEP, SoundSource.BLOCKS, 1f, 1f);
                }
            }

            if (this.counter == 1 && isValidTarget(level, this.blockPos)) {
                BlockPos.betweenClosedStream(new AABB(this.blockPos).inflate(1)).forEach(pos -> {
                    if (level.getBlockState(pos).is(Blocks.FIRE)) {
                        level.removeBlock(pos, false);
                        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.4f, 0.4f);
                        level.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX(), pos.getY(), pos.getZ(), 0.0D, 0.0D, 0.0D);
                    }
                });
            }
            this.counter--;
        }
    }

    @Override
    public void stop() {
        super.stop();
        this.rhino.setImmuneToFire(false);
        this.rhino.setStomping(false);
    }

    @Override
    public double acceptedDistance() {
        return 3;
    }

    @Override
    protected boolean isValidTarget(LevelReader world, BlockPos pos) {
        ChunkAccess chunk = world.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()), ChunkStatus.FULL, false);
        if (chunk == null) {
            return false;
        } else {
            return chunk.getBlockState(pos).is(Blocks.FIRE) && chunk.getBlockState(pos.above()).isAir();
        }
    }

    @Override
    protected boolean findNearestBlock() {
        BlockPos blockPos = this.mob.blockPosition();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (BlockPos offset : this.offsets) {
            mutable.setWithOffset(blockPos, offset);
            if (this.mob.isWithinRestriction(mutable) && this.isValidTarget(this.mob.level, mutable)) {
                this.blockPos = mutable;
                return true;
            }
        }

        return false;
    }
}
