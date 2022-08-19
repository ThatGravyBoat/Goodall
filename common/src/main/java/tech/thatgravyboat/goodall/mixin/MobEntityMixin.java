package tech.thatgravyboat.goodall.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.thatgravyboat.goodall.common.item.AnimalCrateBlockItem;

@Mixin(Mob.class)
public abstract class MobEntityMixin extends LivingEntity {

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "checkAndHandleImportantInteractions", at = @At("HEAD"), cancellable = true)
    private void onSpecialInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof AnimalCrateBlockItem) {
            InteractionResult result = stack.interactLivingEntity(player, this, hand);
            if (result.consumesAction()) {
                cir.setReturnValue(result);
            }
        }
        //noinspection ConstantConditions
        if (stack.is(Items.FEATHER) && ((Object)this) instanceof Panda panda) {
            panda.sneeze(true);
        }
    }

}
