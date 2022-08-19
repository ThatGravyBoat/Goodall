package tech.thatgravyboat.goodall.common.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class DeerHeadBlockItem extends BlockItem implements IAnimatable {

    private final AnimationFactory factory = new AnimationFactory(this);

    public DeerHeadBlockItem(Block block, Properties settings) {
        super(block, settings);
    }

    @Override
    public void registerControllers(AnimationData animationData) {}

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
