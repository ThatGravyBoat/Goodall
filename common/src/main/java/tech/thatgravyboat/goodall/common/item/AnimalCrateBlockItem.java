package tech.thatgravyboat.goodall.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.common.registry.ModBlocks;
import tech.thatgravyboat.goodall.mixin.MobEntityAccessor;

import java.util.List;

public class AnimalCrateBlockItem extends BlockItem {

    public static final TagKey<EntityType<?>> ALLOWED_ANIMALS = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(Goodall.MOD_ID, "crate_animals"));

    public AnimalCrateBlockItem(Properties settings) {
        super(ModBlocks.ANIMAL_CRATE.get(), settings);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, @NotNull Player user, @NotNull LivingEntity entity, @NotNull InteractionHand hand) {
        if ((!stack.hasTag() || !stack.getOrCreateTag().contains("BlockEntityTag")) && entity.getType().is(ALLOWED_ANIMALS)) {
            CompoundTag entityTag = new CompoundTag();
            if (entity.save(entityTag)) {
                entityTag.remove("UUID");
                CompoundTag compound = stack.getOrCreateTag();
                CompoundTag blockTag = new CompoundTag();
                blockTag.put("Entity", entityTag);
                if (entity instanceof Mob mob && mob instanceof MobEntityAccessor accessor) {
                    if (accessor.callGetAmbientSound() != null) {
                        blockTag.putString("Sound", accessor.callGetAmbientSound().getLocation().toString());
                    }
                }
                compound.put("BlockEntityTag", blockTag);
                compound.putString("EntityDisplayName", Component.Serializer.toJson(entity.getType().getDescription()));
                stack.setTag(compound);
                user.setItemInHand(hand, stack);
                entity.discard();
                return InteractionResult.sidedSuccess(user.level.isClientSide());
            }
            return InteractionResult.FAIL;
        }
        return super.interactLivingEntity(stack, user, entity, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag context) {
        if (stack.hasTag() && stack.getOrCreateTag().contains("EntityDisplayName")) {
            Component display = Component.Serializer.fromJson(stack.getOrCreateTag().getString("EntityDisplayName"));
            tooltip.add(Component.translatable("item.goodall.animal_crate.entity_name", display).withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, world, tooltip, context);
    }
}
