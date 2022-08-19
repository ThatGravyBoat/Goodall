package tech.thatgravyboat.goodall.common.entity;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
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
import tech.thatgravyboat.goodall.common.entity.base.NonBreedingAnimal;
import tech.thatgravyboat.goodall.common.entity.base.Sleeping;
import tech.thatgravyboat.goodall.common.entity.goals.SleepingGoal;
import tech.thatgravyboat.goodall.common.lib.DeerVariant;

import java.util.UUID;

public class DeerEntity extends NonBreedingAnimal implements Sleeping, NeutralMob, IAnimatable, IEntityModel {

    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(DeerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(DeerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(DeerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final UniformInt ANGER_TIME_RANGE = TimeUtil.rangeOfSeconds(20, 39);
    private static final UniformInt SLEEP_TIME = TimeUtil.rangeOfSeconds(10, 60);
    private static final UniformInt SCARED_TIME = TimeUtil.rangeOfSeconds(25, 30);


    private final AnimationFactory factory = new AnimationFactory(this);

    private int angerTime;
    @Nullable
    private UUID angryAt;

    private int eatGrassTimer;
    private EatBlockGoal eatGrassGoal;

    private int sleepTime = -1;
    private int isScared;

    public DeerEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.maxUpStep = 1f;
    }

    public static AttributeSupplier.Builder createDeerAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20D)
                .add(Attributes.ATTACK_DAMAGE, 6D)
                .add(Attributes.MOVEMENT_SPEED, 0.4D)
                .add(Attributes.FOLLOW_RANGE, 16D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SleepingGoal<>(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.1D, false));
        this.goalSelector.addGoal(2, (this.eatGrassGoal = new EatBlockGoal(this)));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true, this::isAngryAt));
        this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType spawnReason, @Nullable SpawnGroupData entityData, @Nullable CompoundTag tag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnReason, entityData, tag);
        Holder<Biome> biome = level.getBiome(this.blockPosition());
        if (biome.isBound()) {
            Biome value = biome.value();
            ResourceKey<Biome> key = biome.unwrapKey().orElse(null);
            if (Biomes.DARK_FOREST.equals(key)) setVariant(DeerVariant.DARK_BROWN);
            else if (Biomes.FLOWER_FOREST.equals(key) || Biomes.MEADOW.equals(key)) setVariant(DeerVariant.WHITE_TAILED);
            else if (biome.is(BiomeTags.IS_TAIGA)) setVariant(DeerVariant.RED);
            else if (Biome.Precipitation.SNOW.equals(value.getPrecipitation())) setVariant(DeerVariant.WHITE);
        }
        return data;
    }

    //region Charging
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CHARGING, false);
        this.entityData.define(SLEEPING, false);
        this.entityData.define(VARIANT, 0);
    }

    public boolean isCharging() {
        return this.entityData.get(CHARGING);
    }
    //endregion

    //region Eating
    @Override
    public void handleEntityEvent(byte status) {
        if (status == 10) {
            this.eatGrassTimer = 40;
        } else {
            super.handleEntityEvent(status);
        }
    }

    @Override
    public void ate() {
        this.heal(2f);
    }
    //endregion

    //region Tick
    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            this.updatePersistentAnger((ServerLevel) this.level, true);
            if (this.isScared > 0) this.isScared--;
            if (this.level.isNight() && this.isScared == 0) {
                if (this.sleepTime > 0) this.sleepTime--;
                if (this.sleepTime == -1) {
                    this.sleepTime = SLEEP_TIME.sample(this.random);
                }

                if (this.sleepTime == 0) {
                    getEntityData().set(SLEEPING, true);
                }
            } else {
                this.sleepTime = -1;
                getEntityData().set(SLEEPING, false);
            }
        }
    }

    @Override
    protected void customServerAiStep() {
        this.eatGrassTimer = this.eatGrassGoal.getEatAnimationTick();
        super.customServerAiStep();
    }

    @Override
    public void aiStep() {
        if (this.level.isClientSide) {
            this.eatGrassTimer = Math.max(0, this.eatGrassTimer - 1);
        }
        super.aiStep();
    }
    //endregion

    //region Sleeping
    @Override
    public boolean isSleeping() {
        return this.entityData.get(SLEEPING);
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (!this.isInvulnerableTo(source)) {
            this.isScared = SCARED_TIME.sample(this.random);
        }
        return super.hurt(source, amount);
    }
    //endregion

    //region Anger Management
    @Override
    public int getRemainingPersistentAngerTime() {
        return this.angerTime;
    }

    @Override
    public void setRemainingPersistentAngerTime(int angerTime) {
        this.angerTime = angerTime;
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.angryAt;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID angryAt) {
        this.angryAt = angryAt;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(ANGER_TIME_RANGE.sample(this.random));
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (!getVariant().passive) {
            super.setTarget(target);
            entityData.set(CHARGING, target != null);
        }
    }
    //endregion

    //region Animation
    private <E extends IAnimatable> PlayState walkCycle(AnimationEvent<E> event) {
        var builder = new AnimationBuilder();
        if (event.isMoving()) {
            builder.addAnimation("animation.deer.walking", true);
        } else if (!isSleeping()) {
            builder.addAnimation("animation.deer.idle", true);
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
        if (this.isSleeping()) {
            builder.addAnimation("animation.deer.sleeping", true);
        } else if (this.isCharging()) {
            builder.addAnimation("animation.deer.charge", true);
        } else if (this.eatGrassTimer > 0 && this.eatGrassTimer < 26) {
            builder.addAnimation("animation.deer.grazing", true);
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
        data.addAnimationController(new AnimationController<>(this, "action_controller", 15, this::actionCycle));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
    //endregion

    //region Serialization
    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", getVariant().ordinal());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setVariant(DeerVariant.getVariant(tag.getInt("Variant")));
    }
    //endregion

    //region Variant
    public DeerVariant getVariant() {
        return DeerVariant.getVariant(this.entityData.get(VARIANT));
    }

    public void setVariant(DeerVariant variant) {
        this.entityData.set(VARIANT, variant.ordinal());
    }
    //endregion

    //region Texture
    @Override
    public EntityModel getEntityModel() {
        return EntityModel.DEER;
    }

    @Override
    public ResourceLocation getITexture() {
        return DeerVariant.getVariantForName(this.getName().getString()).orElse(getVariant()).getTexture(this.isSleeping());
    }
    //endregion
}
