package tech.thatgravyboat.goodall.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
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
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.common.entity.base.EntityModel;
import tech.thatgravyboat.goodall.common.entity.base.IEntityModel;
import tech.thatgravyboat.goodall.common.entity.base.Sleeping;
import tech.thatgravyboat.goodall.common.entity.goals.SleepingGoal;
import tech.thatgravyboat.goodall.common.entity.goals.StompFireGoal;
import tech.thatgravyboat.goodall.common.registry.ModEntities;

import java.util.UUID;

public class RhinoEntity extends Animal implements NeutralMob, IAnimatable, Sleeping, IEntityModel {

    private static final EntityDataAccessor<Boolean> WHITE = SynchedEntityData.defineId(RhinoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(RhinoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(RhinoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> STOMPING = SynchedEntityData.defineId(RhinoEntity.class, EntityDataSerializers.BOOLEAN);

    private static final UniformInt ANGER_TIME_RANGE = TimeUtil.rangeOfSeconds(20, 39);
    private static final UniformInt SCARED_TIME = TimeUtil.rangeOfSeconds(25, 30);
    private static final UniformInt SLEEP_TIME = TimeUtil.rangeOfSeconds(10, 60);

    private static final Ingredient BREEDING_INGREDIENT = Ingredient.of(Items.DEAD_BUSH);

    private final AnimationFactory factory = new AnimationFactory(this);

    private int eatGrassTimer;
    private EatBlockGoal eatGrassGoal;

    private int angerTime;
    @Nullable
    private UUID angryAt;

    private int sleepTime = -1;
    private int isScared;

    private boolean immuneToFire = false;

    public RhinoEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.maxUpStep = 1f;
    }

    public static AttributeSupplier.Builder createRhinoAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SleepingGoal<>(this));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, BREEDING_INGREDIENT, false));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.3D, false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 0.45D));
        this.goalSelector.addGoal(6, (this.eatGrassGoal = new EatBlockGoal(this)));
        this.goalSelector.addGoal(7, new StompFireGoal(this, 0.8D, 3));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true, this::isAngryAt));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Pillager.class, true, this::isAngryAt));
        this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WHITE, false);
        this.entityData.define(SLEEPING, false);
        this.entityData.define(CHARGING, false);
        this.entityData.define(STOMPING, false);
    }

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
                    entityData.set(SLEEPING, true);
                }
            } else {
                this.sleepTime = -1;
                entityData.set(SLEEPING, false);
            }

            if (this.random.nextInt(1000) == 0 && this.angryAt == null) {
                Player nearestPlayer = this.level.getNearestPlayer(getX(), getY(), getZ(), 6, true);
                if (nearestPlayer != null && this.random.nextInt(10) == 0) {
                    this.setTarget(nearestPlayer);
                }
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
        this.heal(6f);
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType spawnReason, @Nullable SpawnGroupData entityData, @Nullable CompoundTag tag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnReason, entityData, tag);
        this.setWhite(this.random.nextInt(50000) == 0);
        return data;
    }

    @Override
    public boolean canBeLeashed(@NotNull Player player) {
        return false;
    }

    //region Damage
    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (!this.isInvulnerableTo(source)) {
            this.isScared = SCARED_TIME.sample(this.random);
        }
        if (source.isFire()) {
            amount = amount / 2;
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (damageSource.isFire() && this.immuneToFire) {
            return true;
        }
        return super.isInvulnerableTo(damageSource);
    }
    //endregion

    //region Breeding
    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob entity) {
        RhinoEntity rhino = ModEntities.RHINO.get().create(level);
        if (rhino != null && entity instanceof RhinoEntity rhinoEntity) {
            rhino.setWhite(rhinoEntity.isWhite() && this.isWhite());
        }
        return rhino;
    }

    @Override
    public boolean isFood(@NotNull ItemStack stack) {
        return BREEDING_INGREDIENT.test(stack);
    }
    //endregion

    //region Nbt
    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.readPersistentAngerSaveData(this.level, tag);
        this.entityData.set(WHITE, tag.getBoolean("White"));
        this.entityData.set(SLEEPING, tag.getBoolean("Sleeping"));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.addPersistentAngerSaveData(tag);
        tag.putBoolean("White", isWhite());
        tag.putBoolean("Sleeping", isSleeping());
    }
    //endregion

    //region State Management
    public boolean isWhite() {
        return this.entityData.get(WHITE);
    }

    public void setWhite(boolean white) {
        this.entityData.set(WHITE, white);
    }

    public boolean isSleeping() {
        return this.entityData.get(SLEEPING);
    }

    public boolean isCharging() {
        return this.entityData.get(CHARGING);
    }

    public boolean isStomping() {
        return this.entityData.get(STOMPING);
    }

    public void setStomping(boolean stomping) {
        this.entityData.set(STOMPING, stomping);
    }

    public void setImmuneToFire(boolean immuneToFire) {
        this.immuneToFire = immuneToFire;
    }

    //endregion

    //region Anger Control
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
        super.setTarget(target);
        entityData.set(CHARGING, target != null);
    }
    //endregion

    //region Animation
    private <E extends IAnimatable> PlayState walkCycle(AnimationEvent<E> event) {
        var builder = new AnimationBuilder();
        if (event.isMoving()) {
            builder.addAnimation("animation.rhino.walking", true);
        } else if (!isSleeping()) {
            builder.addAnimation("animation.rhino.idle", true);
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
        if (this.isCharging()) {
            builder.addAnimation("animation.rhino.charge", true);
        } else if (this.isSleeping()) {
            builder.addAnimation("animation.rhino.sleep", true);
        } else if (this.eatGrassTimer > 0 && this.eatGrassTimer < 26) {
            builder.addAnimation("animation.rhino.eat", true);
        } else if (this.isStomping()) {
            builder.addAnimation("animation.rhino.stomp", true);
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
        return factory;
    }
    //endregion

    //region Texture
    @Override
    public EntityModel getEntityModel() {
        return EntityModel.RHINO;
    }

    @Override
    public ResourceLocation getITexture() {
        String texture = this.isWhite() ? "white" : "black";
        if (this.isSleeping()) texture +="_sleeping";
        return new ResourceLocation(Goodall.MOD_ID, "textures/entity/rhino/"+texture+".png");
    }
    //endregion
}
