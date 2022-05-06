package tech.thatgravyboat.goodall.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.thatgravyboat.goodall.common.item.AnimalCrateBlockItem;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interactWithItem", at = @At("HEAD"), cancellable = true)
    private void onSpecialInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() instanceof AnimalCrateBlockItem) {
            ActionResult result = stack.useOnEntity(player, this, hand);
            if (result.isAccepted()) {
                cir.setReturnValue(result);
            }
        }
        //noinspection ConstantConditions
        if (stack.isOf(Items.FEATHER) && ((Object)this) instanceof PandaEntity panda) {
            panda.setSneezing(true);
        }
    }

}
