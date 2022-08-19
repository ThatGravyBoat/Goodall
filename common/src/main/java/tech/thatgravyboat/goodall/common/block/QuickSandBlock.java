package tech.thatgravyboat.goodall.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.NotNull;
import tech.thatgravyboat.goodall.common.entity.FennecFoxEntity;
import tech.thatgravyboat.goodall.common.entity.TortoiseEntity;
import tech.thatgravyboat.goodall.common.registry.ModEnchantments;
import tech.thatgravyboat.goodall.common.registry.ModItems;

import java.util.Optional;

@SuppressWarnings("deprecation")
public class QuickSandBlock extends Block implements BucketPickup {

    private static final VoxelShape FALLING_SHAPE = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 0.9D, 1.0D);

    public QuickSandBlock(Properties settings) {
        super(settings);
    }

    @Override
    public boolean skipRendering(@NotNull BlockState state, BlockState stateFrom, @NotNull Direction direction) {
        return stateFrom.is(this) || super.skipRendering(state, stateFrom, direction);
    }

    @Override
    public VoxelShape getOcclusionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        if (!(entity instanceof LivingEntity) || entity.getFeetBlockState().is(this)) {
            entity.makeStuckInBlock(state, new Vec3(0.25D, 0.05D, 0.25D));
            if (level.isClientSide) {
                RandomSource random = level.getRandom();
                boolean bl = entity.xOld != entity.getX() || entity.zOld != entity.getZ();
                if (bl && random.nextBoolean()) {
                    level.addParticle(new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.SAND.defaultBlockState()),
                            entity.getX(), pos.getY() + 1, entity.getZ(),
                            Mth.randomBetween(random, -1.0F, 1.0F) * 0.08F,
                            0.05D,
                            Mth.randomBetween(random, -1.0F, 1.0F) * 0.08F
                    );
                }
            }
            float f = entity.getBbWidth() * 0.8F;
            AABB box = AABB.ofSize(entity.getEyePosition(), f, 1.0E-6D, f);
            if (BlockPos.betweenClosedStream(box).anyMatch((bpos) -> Shapes.joinIsNotEmpty(Shapes.block().move(pos.getX(), pos.getY(), pos.getZ()), Shapes.create(box), BooleanOp.AND))) {
                entity.hurt(DamageSource.IN_WALL, 1.0F);
            }
        }
    }

    @Override
    public void fallOn(@NotNull Level level, @NotNull BlockState state, @NotNull BlockPos pos, @NotNull Entity entity, float fallDistance) {
        if (!(fallDistance < 4.0f) && entity instanceof LivingEntity livingEntity) {
            LivingEntity.Fallsounds fallSounds = livingEntity.getFallSounds();
            entity.playSound(fallDistance < 7.0f ? fallSounds.small() : fallSounds.big(), 1.0F, 1.0F);
        }
    }

    @Override
    public VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if (context instanceof EntityCollisionContext entityShapeContext) {
            Entity entity = entityShapeContext.getEntity();
            if (entity != null) {
                if (entity.fallDistance > 2.5F) {
                    return FALLING_SHAPE;
                }

                if (entity instanceof FallingBlockEntity || canWalkOnQuickSand(entity) && context.isAbove(Shapes.block(), pos, false) && !context.isDescending()) {
                    return super.getCollisionShape(state, level, pos, context);
                }
            }
        }

        return Shapes.empty();
    }

    @Override
    public VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    public static boolean canWalkOnQuickSand(Entity entity) {
        if (entity instanceof TortoiseEntity) return true;
        if (entity instanceof Husk) return true;
        if (entity instanceof Rabbit) return true;
        if (entity instanceof FennecFoxEntity) return true;
        return entity instanceof LivingEntity livingEntity && EnchantmentHelper.getEnchantmentLevel(ModEnchantments.SAND_WALKER.get(), livingEntity) > 0;
    }

    @Override
    public ItemStack pickupBlock(LevelAccessor world, @NotNull BlockPos pos, @NotNull BlockState state) {
        world.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_IMMEDIATE);
        if (!world.isClientSide()) {
            world.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
        }

        return new ItemStack(ModItems.QUICKSAND_BUCKET.get());
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.SAND_HIT);
    }

    @Override
    public boolean isPathfindable(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull PathComputationType type) {
        return true;
    }
}
