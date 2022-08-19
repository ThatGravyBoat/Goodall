package tech.thatgravyboat.goodall.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.thatgravyboat.goodall.common.entity.TortoiseEntity;
import tech.thatgravyboat.goodall.common.registry.ModBlocks;
import tech.thatgravyboat.goodall.common.registry.ModEntities;

@SuppressWarnings("deprecation")
public class TortoiseEggBlock extends Block {

    private static final VoxelShape SMALL_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 12.0D, 7.0D, 12.0D);
    private static final VoxelShape LARGE_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 7.0D, 15.0D);

    public static final IntegerProperty HATCH = BlockStateProperties.HATCH;
    public static final IntegerProperty EGGS = BlockStateProperties.EGGS;

    public TortoiseEggBlock(Properties settings) {
        super(settings);
    }

    @Override
    public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
        if (this.breaksEgg(world, entity)) {
            if (!world.isClientSide && world.random.nextInt(100) == 0 && state.is(ModBlocks.TORTOISE_EGG.get())) {
                this.breakEgg(world, pos, state);
            }
        }
        super.stepOn(world, pos, state, entity);
    }

    private void breakEgg(Level world, BlockPos pos, BlockState state) {
        world.playSound(null, pos, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);
        int i = state.getValue(EGGS);
        if (i <= 1) {
            world.destroyBlock(pos, false);
        } else {
            world.setBlock(pos, state.setValue(EGGS, i - 1), Block.UPDATE_CLIENTS);
            world.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
        }

    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (this.shouldHatchProgress(world) && isSandBelow(world, pos)) {
            int i = state.getValue(HATCH);
            if (i < 2) {
                world.playSound(null, pos, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
                world.setBlock(pos, state.cycle(HATCH), Block.UPDATE_CLIENTS);
            } else {
                world.playSound(null, pos, SoundEvents.TURTLE_EGG_HATCH, SoundSource.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
                world.removeBlock(pos, false);

                for(int j = 0; j < state.getValue(EGGS); ++j) {
                    world.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
                    TortoiseEntity tortoise = ModEntities.TORTOISE.get().create(world);
                    if (tortoise != null) {
                        tortoise.setAge(-24000);
                        tortoise.moveTo(pos.getX() + 0.3D + j * 0.2D, pos.getY(), pos.getZ() + 0.3D, 0.0F, 0.0F);
                        world.addFreshEntity(tortoise);
                    }
                }
            }
        }

    }

    public static boolean isSandBelow(BlockGetter world, BlockPos pos) {
        return world.getBlockState(pos.below()).is(BlockTags.SAND);
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean notify) {
        if (isSandBelow(level, pos) && !level.isClientSide) {
            level.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, pos, 0);
        }
    }

    private boolean shouldHatchProgress(Level level) {
        float f = level.getTimeOfDay(1.0F);
        return f < 0.69f && f > 0.65f || level.random.nextInt(500) == 0;
    }

    @Override
    public void playerDestroy(@NotNull Level level, @NotNull Player player, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable BlockEntity blockEntity, @NotNull ItemStack stack) {
        super.playerDestroy(level, player, pos, state, blockEntity, stack);
        this.breakEgg(level, pos, state);
    }

    @Override
    public boolean canBeReplaced(@NotNull BlockState state, BlockPlaceContext context) {
        return !context.isSecondaryUseActive() && context.getItemInHand().is(this.asItem()) && state.getValue(EGGS) < 4 || super.canBeReplaced(state, context);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState blockState = ctx.getLevel().getBlockState(ctx.getClickedPos());
        return blockState.is(this) ? blockState.setValue(EGGS, Math.min(4, blockState.getValue(EGGS) + 1)) : super.getStateForPlacement(ctx);
    }

    @Override
    public VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return state.getValue(EGGS) > 1 ? LARGE_SHAPE : SMALL_SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HATCH, EGGS);
    }

    private boolean breaksEgg(Level world, Entity entity) {
        if (entity instanceof Turtle) return false;
        if (entity instanceof Bat) return false;
        if (entity instanceof TortoiseEntity) return false;
        if (entity instanceof LivingEntity) {
            return entity instanceof Player || world.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
        }
        return false;
    }
}
