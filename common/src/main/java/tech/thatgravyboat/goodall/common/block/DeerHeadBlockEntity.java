package tech.thatgravyboat.goodall.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import tech.thatgravyboat.goodall.common.registry.ModBlocks;

public class DeerHeadBlockEntity extends BlockEntity implements IAnimatable {

    private final AnimationFactory factory = new AnimationFactory(this);

    public DeerHeadBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.DEER_HEAD_ENTITY.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimationData animationData) {}

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
