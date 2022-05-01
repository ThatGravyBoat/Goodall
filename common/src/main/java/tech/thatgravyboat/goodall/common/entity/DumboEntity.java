package tech.thatgravyboat.goodall.common.entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import tech.thatgravyboat.goodall.common.entity.goals.DumboShyGoal;
import tech.thatgravyboat.goodall.common.lib.DumboVariant;
import tech.thatgravyboat.goodall.common.registry.ModItems;

import java.util.Random;

public class DumboEntity extends SquidEntity implements Bucketable, IAnimatable {

    private static final TrackedData<Boolean> SHY = DataTracker.registerData(DumboEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> BUCKET = DataTracker.registerData(DumboEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(DumboEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private final AnimationFactory factory = new AnimationFactory(this);

    private int healTicks;

    public DumboEntity(EntityType<? extends SquidEntity> entityType, World world) {
        super(entityType, world);
    }

    public static boolean canDumboSpawn(EntityType<? extends MobEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        if (spawnReason == SpawnReason.SPAWNER) return true;
        int i = world.getSeaLevel() - 15;
        int j = i - 13;
        return pos.getY() >= j && pos.getY() <= i && world.getFluidState(pos.down()).isIn(FluidTags.WATER) && world.getBlockState(pos.up()).isOf(Blocks.WATER);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SHY, false);
        this.dataTracker.startTracking(BUCKET, false);
        this.dataTracker.startTracking(VARIANT, 0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new DumboShyGoal(this));
        super.initGoals();
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (isShy()) {
            setVelocity(0, 0, 0);
        }
        this.healTicks++;
        if (this.healTicks == 100) {
            this.healTicks = 0;
            this.heal(1f);
        }
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        EntityData data = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
        if (!spawnReason.equals(SpawnReason.BUCKET)) this.dataTracker.set(VARIANT, DumboVariant.random(this.random).ordinal());
        return data;
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity player) {
        return false;
    }

    //region NBT
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(VARIANT, nbt.getInt("Variant"));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.dataTracker.get(VARIANT));
    }
    //endregion

    //region Bucketable
    @Override
    public boolean isFromBucket() {
        return this.dataTracker.get(BUCKET);
    }

    @Override
    public void setFromBucket(boolean fromBucket) {
        this.dataTracker.set(BUCKET, fromBucket);
    }

    @Override
    public void copyDataToStack(ItemStack stack) {
        Bucketable.copyDataToStack(this, stack);
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        nbtCompound.putInt("Variant", this.dataTracker.get(VARIANT));
    }

    @Override
    public void copyDataFromNbt(NbtCompound nbt) {
        Bucketable.copyDataFromNbt(this, nbt);
        this.dataTracker.set(VARIANT, Math.max(0, Math.min(2, nbt.getInt("Variant"))));
    }

    @Override
    public ItemStack getBucketItem() {
        return new ItemStack(ModItems.DUMBO_BUCKET.get());
    }

    @Override
    public SoundEvent getBucketedSound() {
        return SoundEvents.ITEM_BUCKET_FILL_FISH;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        return Bucketable.tryBucket(player, hand, this).orElse(super.interactMob(player, hand));
    }
    //endregion

    //region State Management
    public boolean isShy() {
        return this.getAttacker() == null && this.dataTracker.get(SHY);
    }

    public void setShy(boolean shy) {
        this.dataTracker.set(SHY, shy);
    }

    public DumboVariant getVariant() {
        return DumboVariant.getVariant(this.dataTracker.get(VARIANT));
    }
    //endregion

    //region Animations
    private <E extends IAnimatable> PlayState walkCycle(AnimationEvent<E> event) {
        var builder = new AnimationBuilder();
        if (MathHelper.lerp(event.getPartialTick(), this.prevTentacleAngle, this.tentacleAngle) > 0.1f) {
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

    private <E extends IAnimatable> PlayState actionCycle(AnimationEvent<E> event) {
        var builder = new AnimationBuilder();
        if (this.isShy()) {
            builder.addAnimation("animation.dumbo.shy").addAnimation("animation.dumbo.shy-end", true);
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
        data.addAnimationController(new AnimationController<>(this, "walk_controller", 10, this::walkCycle));
        data.addAnimationController(new AnimationController<>(this, "action_controller", 0, this::actionCycle));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
    //endregion
}
