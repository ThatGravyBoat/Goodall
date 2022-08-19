package tech.thatgravyboat.goodall.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GenericBlockBucket extends BlockItem implements DispensibleContainerItem {
    private final SoundEvent placeSound;

    public GenericBlockBucket(Block block, SoundEvent placeSound, Item.Properties settings) {
        super(block, settings);
        this.placeSound = placeSound;
    }

    @Override
    public InteractionResult useOn(@NotNull UseOnContext context) {
        InteractionResult actionResult = super.useOn(context);
        Player playerEntity = context.getPlayer();
        if (actionResult.consumesAction() && playerEntity != null && !playerEntity.isCreative()) {
            playerEntity.setItemInHand(context.getHand(), Items.BUCKET.getDefaultInstance());
        }

        return actionResult;
    }

    @Override
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    @Override
    protected SoundEvent getPlaceSound(@NotNull BlockState state) {
        return this.placeSound;
    }

    @Override
    public boolean emptyContents(@Nullable Player player, Level world, @NotNull BlockPos pos, @Nullable BlockHitResult hitResult) {
        if (world.isInWorldBounds(pos) && world.isEmptyBlock(pos)) {
            if (!world.isClientSide()) {
                world.setBlock(pos, this.getBlock().defaultBlockState(), Block.UPDATE_ALL);
            }

            world.playSound(player, pos, this.placeSound, SoundSource.BLOCKS, 1.0F, 1.0F);
            return true;
        }
        return false;
    }
}
