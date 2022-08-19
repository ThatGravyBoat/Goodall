package tech.thatgravyboat.goodall.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
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
import tech.thatgravyboat.goodall.common.entity.base.TameableBirdEntity;
import tech.thatgravyboat.goodall.common.lib.ToucanVariant;

import java.util.function.Predicate;

public class ToucanEntity extends TameableBirdEntity implements IAnimatable, IEntityModel {

    public static final Predicate<Entity> EFFECTED_ENTITIES = EntitySelector.LIVING_ENTITY_STILL_ALIVE.and(EntitySelector.NO_SPECTATORS).and(entity -> !(entity instanceof Enemy));
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(ToucanEntity.class, EntityDataSerializers.INT);
    private final AnimationFactory factory = new AnimationFactory(this);
    private int healTicker;

    public ToucanEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if ((this.healTicker++) + this.random.nextInt(15) >= 200) {
            this.heal(2f);
            this.healTicker = 0;
        }
    }

    //region State Management
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, 0);
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType spawnReason, @Nullable SpawnGroupData entityData, @Nullable CompoundTag tag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnReason, entityData, tag);
        this.entityData.set(VARIANT, ToucanVariant.random(random).ordinal());
        return data;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", getVariant().ordinal());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setVariant(ToucanVariant.getVariant(tag.getInt("Variant")));
    }

    public ToucanVariant getVariant() {
        return ToucanVariant.getVariant(this.entityData.get(VARIANT));
    }

    public void setVariant(ToucanVariant variant) {
        this.entityData.set(VARIANT, variant.ordinal());
    }
    //endregion

    @Override
    public InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (this.level.isClientSide()) {
            boolean bl = this.isOwnedBy(player) || this.isTame() || itemStack.is(Items.APPLE) && !this.isTame();
            return bl ? InteractionResult.CONSUME : InteractionResult.PASS;
        }

        if (this.isTame()) {
            InteractionResult actionResult = super.mobInteract(player, hand);
            if (!actionResult.consumesAction() && this.isOwnedBy(player)) {
                if (itemStack.is(Items.GOLDEN_APPLE)) {
                    level.getEntities(this, this.getBoundingBox().inflate(5), EFFECTED_ENTITIES).forEach(entity -> {
                        if (entity instanceof LivingEntity livingEntity) {
                            livingEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 10*20));
                        }
                    });
                } else if (itemStack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
                    level.getEntities(this, this.getBoundingBox().inflate(5), EFFECTED_ENTITIES).forEach(entity -> {
                        if (entity instanceof LivingEntity livingEntity) {
                            livingEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 30*20));
                            livingEntity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 60*20));
                            livingEntity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60*20));
                        }
                    });
                } else {
                    this.setOrderedToSit(!this.isOrderedToSit());
                    this.jumping = false;
                    this.navigation.stop();
                    this.setTarget(null);
                }

                return InteractionResult.SUCCESS;
            }
            return actionResult;
        }

        if (itemStack.is(Items.APPLE)) {
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }

            if (this.random.nextInt(5) == 0) {
                this.tame(player);
                this.navigation.stop();
                this.setTarget(null);
                this.setOrderedToSit(true);
                this.level.broadcastEntityEvent(this, (byte)7);
            } else {
                this.level.broadcastEntityEvent(this, (byte)6);
            }

            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    //region Animation
    private <E extends IAnimatable> PlayState animation(AnimationEvent<E> event) {
        if (this.isInSittingPose()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.toucan.sit", true));
        } else if (isFlying()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.toucan.fly", true));
        } else if (event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.toucan.walking", true));
        } else {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.toucan.idle", true));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 5, this::animation));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
    //endregion

    //region Texturing
    @Override
    public EntityModel getEntityModel() {
        return EntityModel.TOUCAN;
    }

    @Override
    public ResourceLocation getITexture() {
        return ToucanVariant.getVariantForName(this.getName().getString()).orElse(getVariant()).texture;
    }
    //endregion

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob entity) {
        return null;
    }
}
