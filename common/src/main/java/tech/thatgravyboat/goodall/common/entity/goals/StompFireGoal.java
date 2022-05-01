package tech.thatgravyboat.goodall.common.entity.goals;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import tech.thatgravyboat.goodall.common.entity.RhinoEntity;

import java.util.ArrayList;
import java.util.List;

public class StompFireGoal extends MoveToTargetPosGoal {

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
        if (hasReached()) {
            this.rhino.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, Vec3d.ofCenter(this.targetPos));
            this.rhino.setImmuneToFire(true);
            this.rhino.setStomping(true);
            if (this.counter == 0) {
                this.counter = 50;
            }

            World world = this.rhino.world;

            if (this.counter == 2 && isTargetPos(world, this.targetPos)) {
                if (world instanceof ServerWorld serverWorld) {
                    BlockState state = world.getBlockState(this.targetPos.down());
                    serverWorld.spawnParticles(
                            new BlockStateParticleEffect(ParticleTypes.BLOCK, state),
                            this.rhino.getX(), this.rhino.getY(), this.rhino.getZ(),
                            100,
                            0.75, 0, 0.75,
                            5
                    );
                    serverWorld.playSound(null, this.targetPos, SoundEvents.ENTITY_HOGLIN_STEP, SoundCategory.BLOCKS, 1f, 1f);
                }
            }

            if (this.counter == 1 && isTargetPos(world, this.targetPos)) {
                BlockPos.stream(new Box(this.targetPos).expand(1)).forEach(pos -> {
                    if (world.getBlockState(pos).isOf(Blocks.FIRE)) {
                        world.removeBlock(pos, false);
                        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.4f, 0.4f);
                        world.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX(), pos.getY(), pos.getZ(), 0.0D, 0.0D, 0.0D);
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
    public double getDesiredDistanceToTarget() {
        return 3;
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        Chunk chunk = world.getChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()), ChunkStatus.FULL, false);
        if (chunk == null) {
            return false;
        } else {
            return chunk.getBlockState(pos).isOf(Blocks.FIRE) && chunk.getBlockState(pos.up()).isAir();
        }
    }

    @Override
    protected boolean findTargetPos() {
        BlockPos blockPos = this.mob.getBlockPos();
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (BlockPos offset : this.offsets) {
            mutable.set(blockPos, offset);
            if (this.mob.isInWalkTargetRange(mutable) && this.isTargetPos(this.mob.world, mutable)) {
                this.targetPos = mutable;
                return true;
            }
        }

        return false;
    }
}
