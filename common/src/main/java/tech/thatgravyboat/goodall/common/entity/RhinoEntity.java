package tech.thatgravyboat.goodall.common.entity;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import tech.thatgravyboat.goodall.common.entity.base.Sleeping;
import tech.thatgravyboat.goodall.common.entity.goals.SleepingGoal;
import tech.thatgravyboat.goodall.common.entity.goals.StompFireGoal;
import tech.thatgravyboat.goodall.common.registry.ModEntities;

import java.util.UUID;

public class RhinoEntity extends AnimalEntity implements Angerable, IAnimatable, Sleeping {

    private static final TrackedData<Boolean> WHITE = DataTracker.registerData(RhinoEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> CHARGING = DataTracker.registerData(RhinoEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SLEEPING = DataTracker.registerData(RhinoEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> STOMPING = DataTracker.registerData(RhinoEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final UniformIntProvider ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);
    private static final UniformIntProvider SCARED_TIME = TimeHelper.betweenSeconds(25, 30);
    private static final UniformIntProvider SLEEP_TIME = TimeHelper.betweenSeconds(10, 60);

    private static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(Items.DEAD_BUSH);

    private final AnimationFactory factory = new AnimationFactory(this);

    private int eatGrassTimer;
    private EatGrassGoal eatGrassGoal;

    private int angerTime;
    @Nullable
    private UUID angryAt;

    private int sleepTime = -1;
    private int isScared;

    private boolean immuneToFire = false;

    public RhinoEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.stepHeight = 1f;
    }

    public static DefaultAttributeContainer.Builder createRhinoAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0D)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new SleepingGoal<>(this));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0D));
        this.goalSelector.add(3, new TemptGoal(this, 1.0D, BREEDING_INGREDIENT, false));
        this.goalSelector.add(4, new MeleeAttackGoal(this, 1.3D, false));
        this.goalSelector.add(5, new FollowParentGoal(this, 0.45D));
        this.goalSelector.add(6, (this.eatGrassGoal = new EatGrassGoal(this)));
        this.goalSelector.add(7, new StompFireGoal(this, 0.8D, 3));
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 0.6D));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(10, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, PlayerEntity.class, true, this::shouldAngerAt));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, PillagerEntity.class, true, this::shouldAngerAt));
        this.targetSelector.add(5, new UniversalAngerGoal<>(this, false));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(WHITE, false);
        this.dataTracker.startTracking(SLEEPING, false);
        this.dataTracker.startTracking(CHARGING, false);
        this.dataTracker.startTracking(STOMPING, false);
    }

    //region Tick
    @Override
    public void tick() {
        super.tick();

        if (!this.world.isClient) {
            this.tickAngerLogic((ServerWorld)this.world, true);
            if (this.isScared > 0) this.isScared--;

            if (this.world.isNight() && this.isScared == 0) {
                if (this.sleepTime > 0) this.sleepTime--;
                if (this.sleepTime == -1) {
                    this.sleepTime = SLEEP_TIME.get(this.random);
                }

                if (this.sleepTime == 0) {
                    getDataTracker().set(SLEEPING, true);
                }
            } else {
                this.sleepTime = -1;
                getDataTracker().set(SLEEPING, false);
            }

            if (this.random.nextInt(1000) == 0 && this.angryAt == null) {
                PlayerEntity nearestPlayer = this.world.getClosestPlayer(getX(), getY(), getZ(), 6, true);
                if (nearestPlayer != null && this.random.nextInt(10) == 0) {
                    this.setTarget(nearestPlayer);
                }
            }
        }
    }

    @Override
    protected void mobTick() {
        this.eatGrassTimer = this.eatGrassGoal.getTimer();
        super.mobTick();
    }

    @Override
    public void tickMovement() {
        if (this.world.isClient) {
            this.eatGrassTimer = Math.max(0, this.eatGrassTimer - 1);
        }

        super.tickMovement();
    }
    //endregion

    @Override
    public void handleStatus(byte status) {
        if (status == 10) {
            this.eatGrassTimer = 40;
        } else {
            super.handleStatus(status);
        }
    }

    @Override
    public void onEatingGrass() {

        this.heal(6f);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        EntityData data = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
        this.setWhite(this.random.nextInt(50000) == 0);
        return data;
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity player) {
        return false;
    }

    //region Damage
    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.isInvulnerableTo(source)) {
            this.isScared = SCARED_TIME.get(this.random);
        }
        if (source.isFire()) {
            amount = amount / 2;
        }
        return super.damage(source, amount);
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
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        RhinoEntity rhino = ModEntities.RHINO.get().create(world);
        if (rhino != null && entity instanceof RhinoEntity rhinoEntity) {
            rhino.setWhite(rhinoEntity.isWhite() && this.isWhite());
        }
        return rhino;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return BREEDING_INGREDIENT.test(stack);
    }
    //endregion

    //region Nbt
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.readAngerFromNbt(this.world, nbt);
        this.dataTracker.set(WHITE, nbt.getBoolean("White"));
        this.dataTracker.set(SLEEPING, nbt.getBoolean("Sleeping"));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        this.writeAngerToNbt(nbt);
        nbt.putBoolean("White", isWhite());
        nbt.putBoolean("Sleeping", isSleeping());
    }
    //endregion

    //region State Management
    public boolean isWhite() {
        return this.dataTracker.get(WHITE);
    }

    public void setWhite(boolean white) {
        this.dataTracker.set(WHITE, white);
    }

    public boolean isSleeping() {
        return this.dataTracker.get(SLEEPING);
    }

    public boolean isCharging() {
        return this.dataTracker.get(CHARGING);
    }

    public boolean isStomping() {
        return this.dataTracker.get(STOMPING);
    }

    public void setStomping(boolean stomping) {
        this.dataTracker.set(STOMPING, stomping);
    }

    public void setImmuneToFire(boolean immuneToFire) {
        this.immuneToFire = immuneToFire;
    }

    //endregion

    //region Anger Control
    @Override
    public int getAngerTime() {
        return this.angerTime;
    }

    @Override
    public void setAngerTime(int angerTime) {
        this.angerTime = angerTime;
    }

    @Nullable
    @Override
    public UUID getAngryAt() {
        return this.angryAt;
    }

    @Override
    public void setAngryAt(@Nullable UUID angryAt) {
        this.angryAt = angryAt;
    }

    @Override
    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target);
        getDataTracker().set(CHARGING, target != null);
    }
    //endregion

    //region Animation
    private <E extends IAnimatable> PlayState walkCycle(AnimationEvent<E> event) {
        var builder = new AnimationBuilder();
        if (event.isMoving()) {
            builder.addAnimation("animation.rhino.walk", true);
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
        data.addAnimationController(new AnimationController<>(this, "walk_controller", 10, this::walkCycle));
        data.addAnimationController(new AnimationController<>(this, "action_controller", 15, this::actionCycle));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    //endregion
}
