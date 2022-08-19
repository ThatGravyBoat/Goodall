package tech.thatgravyboat.goodall.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
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
import tech.thatgravyboat.goodall.common.entity.base.NonBreedingAnimal;
import tech.thatgravyboat.goodall.common.entity.goals.seal.LandAndSeaWanderGoal;
import tech.thatgravyboat.goodall.common.entity.goals.seal.SealSwimNavigation;
import tech.thatgravyboat.goodall.common.registry.ModSounds;

public class SealEntity extends NonBreedingAnimal implements IAnimatable, IEntityModel {

    private static final EntityDataAccessor<Boolean> WHITE = SynchedEntityData.defineId(SealEntity.class, EntityDataSerializers.BOOLEAN);

    private final AnimationFactory factory = new AnimationFactory(this);
    private int healTimer = 100;

    public SealEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DOOR_IRON_CLOSED, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DOOR_WOOD_CLOSED, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DOOR_OPEN, -1.0F);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.5F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
        this.maxUpStep = 1.0F;
    }

    public static AttributeSupplier.Builder createSealAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.6D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new PanicGoal(this, 0.9D));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, PolarBear.class, 16f, 1, 1.2));
        this.goalSelector.addGoal(2, new LandAndSeaWanderGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WHITE, false);
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
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType spawnReason, @Nullable SpawnGroupData entityData, @Nullable CompoundTag tag) {
        if (level.getBiome(this.blockPosition()).value().getPrecipitation().equals(Biome.Precipitation.SNOW)) {
            this.entityData.set(WHITE, true);
        }
        return super.finalizeSpawn(level, difficulty, spawnReason, entityData, tag);
    }

    //region State Management
    public boolean isWhite() {
        return this.entityData.get(WHITE);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("White", isWhite());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(WHITE, tag.getBoolean("White"));
    }
    //endregion

    //region Sea Animal Configuration
    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public MobType getMobType() {
        return MobType.WATER;
    }
    //endregion

    //region Movement
    @Override
    protected PathNavigation createNavigation(@NotNull Level level) {
        return new SealSwimNavigation(this, level);
    }

    @Override
    public float getWalkTargetValue(@NotNull BlockPos pos, LevelReader level) {
        return this.isInWater() == level.getFluidState(pos).is(FluidTags.WATER) ? 10.0F : level.getPathfindingCostFromLightLevels(pos) - 0.5F;
    }

    @Override
    public void travel(@NotNull Vec3 movementInput) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(0.1F, movementInput);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if (this.getTarget() == null) {
                this.push(0D, -0.005D, 0D);
            }
        } else {
            super.travel(movementInput);
        }
    }
    //endregion

    //region Animation
    private <E extends IAnimatable> PlayState walkCycle(AnimationEvent<E> event) {
        var builder = new AnimationBuilder();
        if (event.isMoving()) {
            if (this.isUnderWater()) {
                builder.addAnimation("animation.seal.swim", true);
            } else {
                builder.addAnimation("animation.seal.flop", true);
            }
        } else {
            if (this.isOnGround() && !this.isUnderWater()) {
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
        data.addAnimationController(new AnimationController<>(this, "walk_controller", 5, this::walkCycle));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
    //endregion

    //region Sounds
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.SEAL_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return ModSounds.SEAL_HURT.get();
    }
    //endregion

    //region Texture
    @Override
    public EntityModel getEntityModel() {
        return EntityModel.SEAL;
    }

    @Override
    public ResourceLocation getITexture() {
        return this.isWhite() ? new ResourceLocation(Goodall.MOD_ID, "textures/entity/seal/white.png") : new ResourceLocation(Goodall.MOD_ID, "textures/entity/seal/gray.png");
    }
    //endregion
}
