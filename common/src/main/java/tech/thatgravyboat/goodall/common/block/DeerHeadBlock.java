package tech.thatgravyboat.goodall.common.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import tech.thatgravyboat.goodall.common.registry.ModBlocks;

public class DeerHeadBlock extends BlockWithEntity {

    private static final VoxelShape EAST_SHAPE = Block.createCuboidShape(1, 1, 0, 8, 15, 15);
    private static final VoxelShape WEST_SHAPE = Block.createCuboidShape(8, 1, 1, 16, 15, 15);
    private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(1, 1, 0, 15, 15, 8);
    private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(1, 1, 8, 15, 15, 16);

    public DeerHeadBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(HorizontalFacingBlock.FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(HorizontalFacingBlock.FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(HorizontalFacingBlock.FACING, rotation.rotate(state.get(HorizontalFacingBlock.FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(HorizontalFacingBlock.FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HorizontalFacingBlock.FACING);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(HorizontalFacingBlock.FACING)) {
            case EAST -> EAST_SHAPE;
            case WEST -> WEST_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            default -> NORTH_SHAPE;
        };
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return ModBlocks.DEER_HEAD_ENTITY.get().instantiate(pos, state);
    }
}
