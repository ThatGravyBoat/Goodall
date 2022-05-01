package tech.thatgravyboat.goodall.common.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.common.registry.ModBlocks;
import tech.thatgravyboat.goodall.mixin.MobEntityAccessor;

import java.util.List;

public class AnimalCrateBlockItem extends BlockItem {

    public static final TagKey<EntityType<?>> ALLOWED_ANIMALS = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier(Goodall.MOD_ID, "crate_animals"));

    public AnimalCrateBlockItem(Settings settings) {
        super(ModBlocks.ANIMAL_CRATE.get(), settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        //noinspection ConstantConditions
        if ((!stack.hasNbt() || !stack.getNbt().contains("BlockEntityTag")) && entity.getType().isIn(ALLOWED_ANIMALS)) {
            NbtCompound entityTag = new NbtCompound();
            if (entity.saveNbt(entityTag)) {
                entityTag.remove("UUID");
                NbtCompound compound = stack.getOrCreateNbt();
                NbtCompound blockTag = new NbtCompound();
                blockTag.put("Entity", entityTag);
                if (entity instanceof MobEntity mob && mob instanceof MobEntityAccessor accessor) {
                    if (accessor.callGetAmbientSound() != null) {
                        blockTag.putString("Sound", accessor.callGetAmbientSound().getId().toString());
                    }
                }
                compound.put("BlockEntityTag", blockTag);
                compound.putString("EntityDisplayName", Text.Serializer.toJson(entity.getType().getName()));
                stack.setNbt(compound);
                user.setStackInHand(hand, stack);
                entity.discard();
                return ActionResult.success(user.world.isClient());
            }
            return ActionResult.FAIL;
        }
        return super.useOnEntity(stack, user, entity, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.hasNbt() && stack.getOrCreateNbt().contains("EntityDisplayName")) {
            Text display = Text.Serializer.fromJson(stack.getOrCreateNbt().getString("EntityDisplayName"));
            tooltip.add(new TranslatableText("item.goodall.animal_crate.entity_name", display).formatted(Formatting.GRAY));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
