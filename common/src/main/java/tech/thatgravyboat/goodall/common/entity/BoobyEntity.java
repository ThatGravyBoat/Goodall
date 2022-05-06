package tech.thatgravyboat.goodall.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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
import tech.thatgravyboat.goodall.common.entity.goals.FindTreasure;
import tech.thatgravyboat.goodall.common.registry.ModSounds;

public class BoobyEntity extends NonBreedingAnimal implements IAnimatable, IEntityModel {

    private static final TrackedData<Boolean> FISH = DataTracker.registerData(BoobyEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<BlockPos> TREASURE = DataTracker.registerData(BoobyEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);

    private final AnimationFactory factory = new AnimationFactory(this);

    public float flapProgress;
    public float maxWingDeviation;
    public float prevMaxWingDeviation;
    public float prevFlapProgress;
    public float flapSpeed = 1.0F;
    private float nextFlap = 1.0F;

    public BoobyEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createBoobyAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 4.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(FISH, false);
        this.dataTracker.startTracking(TREASURE, BlockPos.ORIGIN);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.4D));
        this.goalSelector.add(2, new FindTreasure(this));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(5, new LookAroundGoal(this));
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (!itemStack.isEmpty() && itemStack.isOf(Items.TROPICAL_FISH)) {
            spawnConsumptionEffects(itemStack, 16);
            this.setFish(true);
            if (!player.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }

            return ActionResult.success(this.world.isClient);
        }
        return super.interactMob(player, hand);
    }

    public void handleStatus(byte status) {
        if (status == 38) {
            for(int i = 0; i < 7; ++i) {
                double d = this.random.nextGaussian() * 0.01D;
                double e = this.random.nextGaussian() * 0.01D;
                double f = this.random.nextGaussian() * 0.01D;
                this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getParticleX(1.0D), this.getRandomBodyY() + 0.2D, this.getParticleZ(1.0D), d, e, f);
            }
        } else {
            super.handleStatus(status);
        }
    }

    //region State Management
    public boolean hasFish() {
        return this.dataTracker.get(FISH);
    }

    public void setFish(boolean fish) {
        this.dataTracker.set(FISH, fish);
    }

    public BlockPos getTreasurePos() {
        return this.dataTracker.get(TREASURE);
    }

    public void setTreasurePos(@Nullable BlockPos pos) {
        this.dataTracker.set(TREASURE, pos);
    }
    //endregion

    //region NBT
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Fish", hasFish());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setFish(nbt.getBoolean("Fish"));
    }
    //endregion

    //region Wings
    @Override
    public void tickMovement() {
        super.tickMovement();
        this.prevFlapProgress = this.flapProgress;
        this.prevMaxWingDeviation = this.maxWingDeviation;
        this.maxWingDeviation = MathHelper.clamp(this.maxWingDeviation + (this.onGround ? -0.3F : 1.2F), 0.0F, 1.0F);
        if (!this.onGround && this.flapSpeed < 1.0F) {
            this.flapSpeed = 1.0F;
        }

        this.flapSpeed *= 0.9F;
        Vec3d vec3d = this.getVelocity();
        if (!this.onGround && vec3d.y < 0.0D) {
            this.setVelocity(vec3d.multiply(1.0D, 0.6D, 1.0D));
        }

        this.flapProgress += this.flapSpeed * 2.0F;
    }

    @Override
    protected boolean hasWings() {
        return this.speed > this.nextFlap;
    }

    @Override
    protected void addFlapEffects() {
        this.nextFlap = this.speed + this.maxWingDeviation / 2.0F;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }
    //endregion

    //region Sounds
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.BOOBY_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.BOOBY_HURT.get();
    }
    //endregion

    //region Animations
    private <E extends IAnimatable> PlayState walkCycle(AnimationEvent<E> event) {
        var builder = new AnimationBuilder();
        if (event.isMoving() && this.onGround) {
            builder.addAnimation("animation.booby.walk", true);
        } else if (!this.onGround) {
            builder.addAnimation("animation.booby.glide", true);
        } else {
            builder.addAnimation("animation.booby.idle", true);
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

    @Override
    public EntityModel getEntityModel() {
        return EntityModel.BOOBY;
    }
    //endregion
}
