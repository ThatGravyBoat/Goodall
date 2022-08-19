package tech.thatgravyboat.goodall.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import tech.thatgravyboat.goodall.common.entity.base.EntityModel;
import tech.thatgravyboat.goodall.common.entity.base.IEntityModel;
import tech.thatgravyboat.goodall.common.lib.DumboVariant;
import tech.thatgravyboat.goodall.common.registry.ModItems;

public class DumboEntity extends Squid implements Bucketable, IAnimatable, IEntityModel {

    private static final EntityDataAccessor<Boolean> BUCKET = SynchedEntityData.defineId(DumboEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(DumboEntity.class, EntityDataSerializers.INT);

    private final AnimationFactory factory = new AnimationFactory(this);

    private int healTicks;

    public DumboEntity(EntityType<? extends Squid> entityType, Level level) {
        super(entityType, level);
    }

    public static boolean canDumboSpawn(EntityType<? extends Mob> type, LevelAccessor world, MobSpawnType spawnReason, BlockPos pos, RandomSource random) {
        if (spawnReason == MobSpawnType.SPAWNER) return true;
        int i = world.getSeaLevel() - 15;
        int j = i - 13;
        return pos.getY() >= j && pos.getY() <= i && world.getFluidState(pos.below()).is(FluidTags.WATER) && world.getBlockState(pos.above()).is(Blocks.WATER);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BUCKET, false);
        this.entityData.define(VARIANT, 0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.healTicks++;
        if (this.healTicks == 100) {
            this.healTicks = 0;
            this.heal(1f);
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType spawnReason, @Nullable SpawnGroupData entityData, @Nullable CompoundTag tag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnReason, entityData, tag);
        if (!spawnReason.equals(MobSpawnType.BUCKET)) this.entityData.set(VARIANT, DumboVariant.random(this.random).ordinal());
        return data;
    }

    @Override
    public boolean canBeLeashed(@NotNull Player player) {
        return false;
    }

    //region NBT
    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(VARIANT, tag.getInt("Variant"));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        tag.putInt("Variant", this.entityData.get(VARIANT));
    }
    //endregion

    //region Bucketable
    @Override
    public boolean fromBucket() {
        return this.entityData.get(BUCKET);
    }

    @Override
    public void setFromBucket(boolean fromBucket) {
        this.entityData.set(BUCKET, fromBucket);
    }

    @Override
    public void saveToBucketTag(@NotNull ItemStack stack) {
        Bucketable.saveDefaultDataToBucketTag(this, stack);
        CompoundTag nbtCompound = stack.getOrCreateTag();
        nbtCompound.putInt("Variant", this.entityData.get(VARIANT));
    }

    @Override
    public void loadFromBucketTag(@NotNull CompoundTag tag) {
        Bucketable.loadDefaultDataFromBucketTag(this, tag);
        this.entityData.set(VARIANT, Math.max(0, Math.min(2, tag.getInt("Variant"))));
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(ModItems.DUMBO_BUCKET.get());
    }

    @Override
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }

    @Override
    public InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        return Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
    }
    //endregion

    //region State Management
    public DumboVariant getVariant() {
        return DumboVariant.getVariant(this.entityData.get(VARIANT));
    }
    //endregion

    //region Animations
    private <E extends IAnimatable> PlayState walkCycle(AnimationEvent<E> event) {
        var builder = new AnimationBuilder();
        if (Mth.lerp(event.getPartialTick(), this.oldTentacleAngle, this.tentacleAngle) > 0.1f) {
            builder.addAnimation("animation.dumbo.swim", true);
        } else {
            builder.addAnimation("animation.dumbo.idle", true);
        }
        if (builder.getRawAnimationList().isEmpty()) {
            event.getController().markNeedsReload();
            return PlayState.STOP;
        }
        event.getController().setAnimation(builder);
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "walk_controller", 5, this::walkCycle));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public EntityModel getEntityModel() {
        return EntityModel.DUMBO;
    }

    @Override
    public ResourceLocation getITexture() {
        return getVariant().texture;
    }

    //endregion
}
