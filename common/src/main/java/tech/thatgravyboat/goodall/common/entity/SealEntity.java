package tech.thatgravyboat.goodall.common.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.control.YawAdjustingLookControl;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import tech.thatgravyboat.goodall.common.entity.goals.seal.LandAndSeaWanderGoal;
import tech.thatgravyboat.goodall.common.entity.goals.seal.SealSwimNavigation;

public class SealEntity extends PathAwareEntity implements IAnimatable {

    private static final TrackedData<Boolean> WHITE = DataTracker.registerData(SealEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private final AnimationFactory factory = new AnimationFactory(this);
    private int healTimer = 100;

    //Client use
    private boolean hasSleepingTexture = false;

    public SealEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DOOR_IRON_CLOSED, -1.0F);
        this.setPathfindingPenalty(PathNodeType.DOOR_WOOD_CLOSED, -1.0F);
        this.setPathfindingPenalty(PathNodeType.DOOR_OPEN, -1.0F);
        this.moveControl = new AquaticMoveControl(this, 85, 10, 0.02F, 0.5F, true);
        this.lookControl = new YawAdjustingLookControl(this, 10);
        this.stepHeight = 1.0F;
    }

    public static DefaultAttributeContainer.Builder createSealAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.6D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new EscapeDangerGoal(this, 0.9D));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, PolarBearEntity.class, 16f, 1, 1.2));
        this.goalSelector.add(2, new LandAndSeaWanderGoal(this, 0.8D));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(WHITE, false);
    }

    @Override
    public void tick() {
        super.tick();
        if ((this.healTimer--) == 0) {
            this.heal(1f);
            this.healTimer = 100;
        }
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        if (world.getBiome(this.getBlockPos()).value().getPrecipitation().equals(Biome.Precipitation.SNOW)) {
            this.dataTracker.set(WHITE, true);
        }
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    //region State Management
    public boolean isWhite() {
        return this.dataTracker.get(WHITE);
    }

    public boolean hasSleepingTexture() {
        return this.hasSleepingTexture;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("White", isWhite());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(WHITE, nbt.getBoolean("White"));
    }
    //endregion

    //region Sea Animal Configuration
    @Override
    public boolean isPushedByFluids() {
        return false;
    }

    @Override
    public boolean canBreatheInWater() {
        return true;
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.AQUATIC;
    }
    //endregion

    //region Movement
    @Override
    protected EntityNavigation createNavigation(World world) {
        return new SealSwimNavigation(this, world);
    }

    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return this.isTouchingWater() == world.getFluidState(pos).isIn(FluidTags.WATER) ? 10.0F : world.getBrightness(pos) - 0.5F;
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (this.canMoveVoluntarily() && this.isTouchingWater()) {
            this.updateVelocity(0.1F, movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.9D));
            if (this.getTarget() == null) {
                this.addVelocity(0D, -0.005D, 0D);
            }
        } else {
            super.travel(movementInput);
        }
    }
    //endregion

    //region Animation
    private <E extends IAnimatable> PlayState walkCycle(AnimationEvent<E> event) {
        var builder = new AnimationBuilder();
        this.hasSleepingTexture = false;
        if (event.isMoving()) {
            if (this.isSubmergedInWater()) {
                builder.addAnimation("animation.seal.swim", true);
            } else {
                builder.addAnimation("animation.seal.flop", true);
            }
        } else {
            if (this.isOnGround() && !this.isSubmergedInWater()) {
                this.hasSleepingTexture = true;
                builder.addAnimation("animation.seal.sleep", true);
            } else {
                builder.addAnimation("animation.seal.idle", true);
            }
        }
        event.getController().setAnimation(builder);
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "walk_controller", 10, this::walkCycle));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
    //endregion
}
