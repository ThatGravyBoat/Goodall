package tech.thatgravyboat.goodall.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import tech.thatgravyboat.goodall.common.entity.base.FleeingEntity;
import tech.thatgravyboat.goodall.common.entity.base.Sleeping;
import tech.thatgravyboat.goodall.common.entity.goals.LeapingFleeGoal;
import tech.thatgravyboat.goodall.common.entity.goals.SleepingGoal;
import tech.thatgravyboat.goodall.common.registry.ModEntities;

import java.util.function.Predicate;

public class WhiteDeerEntity extends AnimalEntity implements Sleeping, IAnimatable, FleeingEntity {

    private static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(Items.DANDELION);
    private static final Predicate<LivingEntity> FLEE_PREDICATE = (entity) -> !entity.isSneaky() && EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(entity);
    private static final TrackedData<Boolean> SLEEPING = DataTracker.registerData(WhiteDeerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SHOULD_RUN = DataTracker.registerData(WhiteDeerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> FLEEING = DataTracker.registerData(WhiteDeerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final UniformIntProvider SLEEP_TIME = TimeHelper.betweenSeconds(10, 60);
    private static final UniformIntProvider SCARED_TIME = TimeHelper.betweenSeconds(25, 30);

    private final AnimationFactory factory = new AnimationFactory(this);

    private int eatGrassTimer;
    private EatGrassGoal eatGrassGoal;

    private int sleepTime = -1;
    private int isScared;

    public WhiteDeerEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.stepHeight = 1f;
    }

    public static DefaultAttributeContainer.Builder createDeerAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new SleepingGoal<>(this));
        this.goalSelector.add(2, new AnimalMateGoal(this, 0.7D));
        this.goalSelector.add(3, new TemptGoal(this, 0.7D, BREEDING_INGREDIENT, false));
        this.goalSelector.add(4, new LeapingFleeGoal<>(this, PlayerEntity.class, 16.0F, 0.9D, 1D, entity -> FLEE_PREDICATE.test(entity) && this.shouldRun()));
        this.goalSelector.add(5, new FollowParentGoal(this, 0.7D));
        this.goalSelector.add(6, (this.eatGrassGoal = new EatGrassGoal(this)));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 0.6D));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(9, new LookAroundGoal(this));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SLEEPING, false);
        this.dataTracker.startTracking(SHOULD_RUN, true);
        this.dataTracker.startTracking(FLEEING, false);
    }

    public boolean shouldRun() {
        return this.dataTracker.get(SHOULD_RUN);
    }

    public void setShouldRun(boolean shouldRun) {
        this.dataTracker.set(SHOULD_RUN, shouldRun);
    }

    @Override
    public void setFleeing(boolean fleeing) {
        this.dataTracker.set(FLEEING, fleeing);
    }

    public boolean isFleeing() {
        return this.dataTracker.get(FLEEING);
    }

    @Override
    public Vec3d getLeashOffset() {
        return super.getLeashOffset();
    }

    //region Nbt
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setShouldRun(nbt.getBoolean("ShouldRun"));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("ShouldRun", shouldRun());
    }
    //endregion

    //region Sleeping
    @Override
    public void tick() {
        super.tick();

        if (!this.world.isClient) {
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
        }
    }

    @Override
    public boolean isSleeping() {
        return this.dataTracker.get(SLEEPING);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.isInvulnerableTo(source)) {
            this.isScared = SCARED_TIME.get(this.random);
        }
        return super.damage(source, amount);
    }
    //endregion

    //region Eating Grass
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
    //endregion

    //region Breeding
    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return BREEDING_INGREDIENT.test(stack);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        WhiteDeerEntity whiteDeerEntity = ModEntities.WHITE_DEER.get().create(world);
        if (whiteDeerEntity != null) {
            whiteDeerEntity.setShouldRun(false);
        }
        return whiteDeerEntity;
    }
    //endregion

    //region Animation
    private <E extends IAnimatable> PlayState walkCycle(AnimationEvent<E> event) {
        var builder = new AnimationBuilder();
        if (event.isMoving()) {
            if (this.isFleeing()) {
                builder.addAnimation("animation.deer.leaping", true);
            }else {
                builder.addAnimation("animation.deer.walking", true);
            }
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
        data.addAnimationController(new AnimationController<>(this, "walk_controller", 10, this::walkCycle));
        data.addAnimationController(new AnimationController<>(this, "action_controller", 15, this::actionCycle));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
    //endregion
}
