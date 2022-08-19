package tech.thatgravyboat.goodall.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
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
import tech.thatgravyboat.goodall.common.entity.base.EntityModel;
import tech.thatgravyboat.goodall.common.entity.base.IEntityModel;
import tech.thatgravyboat.goodall.common.entity.base.NonBreedingAnimal;
import tech.thatgravyboat.goodall.common.entity.goals.FindTreasure;
import tech.thatgravyboat.goodall.common.registry.ModSounds;

public class PelicanEntity extends NonBreedingAnimal implements IAnimatable, IEntityModel {

    private static final EntityDataAccessor<Boolean> FISH = SynchedEntityData.defineId(PelicanEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<BlockPos> TREASURE = SynchedEntityData.defineId(PelicanEntity.class, EntityDataSerializers.BLOCK_POS);

    private final AnimationFactory factory = new AnimationFactory(this);

    public float flapProgress;
    public float maxWingDeviation;
    public float prevMaxWingDeviation;
    public float prevFlapProgress;
    public float flapSpeed = 1.0F;
    private float nextFlap = 1.0F;

    public PelicanEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createBoobyAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FISH, false);
        this.entityData.define(TREASURE, BlockPos.ZERO);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D));
        this.goalSelector.addGoal(2, new FindTreasure(this));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    public InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!itemStack.isEmpty() && itemStack.is(Items.TROPICAL_FISH)) {
            triggerItemUseEffects(itemStack, 16);
            this.setFish(true);
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }

            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == 38) {
            for(int i = 0; i < 7; ++i) {
                double d = this.random.nextGaussian() * 0.01D;
                double e = this.random.nextGaussian() * 0.01D;
                double f = this.random.nextGaussian() * 0.01D;
                this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.2D, this.getRandomZ(1.0D), d, e, f);
            }
        } else {
            super.handleEntityEvent(status);
        }
    }

    //region State Management
    public boolean hasFish() {
        return this.entityData.get(FISH);
    }

    public void setFish(boolean fish) {
        this.entityData.set(FISH, fish);
    }

    public BlockPos getTreasurePos() {
        return this.entityData.get(TREASURE);
    }

    public void setTreasurePos(@Nullable BlockPos pos) {
        this.entityData.set(TREASURE, pos);
    }
    //endregion

    //region NBT
    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Fish", hasFish());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setFish(tag.getBoolean("Fish"));
    }
    //endregion

    //region Wings
    @Override
    public void aiStep() {
        super.aiStep();
        this.prevFlapProgress = this.flapProgress;
        this.prevMaxWingDeviation = this.maxWingDeviation;
        this.maxWingDeviation = Mth.clamp(this.maxWingDeviation + (this.onGround ? -0.3F : 1.2F), 0.0F, 1.0F);
        if (!this.onGround && this.flapSpeed < 1.0F) {
            this.flapSpeed = 1.0F;
        }

        this.flapSpeed *= 0.9F;
        Vec3 vec3d = this.getDeltaMovement();
        if (!this.onGround && vec3d.y < 0.0D) {
            this.setDeltaMovement(vec3d.multiply(1.0D, 0.6D, 1.0D));
        }

        this.flapProgress += this.flapSpeed * 2.0F;
    }

    @Override
    protected boolean isFlapping() {
        return this.flyDist > this.nextFlap;
    }

    @Override
    protected void onFlap() {
        this.nextFlap = this.flyDist + this.maxWingDeviation / 2.0F;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, @NotNull DamageSource damageSource) {
        return false;
    }
    //endregion

    //region Sounds
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.PELICAN_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return ModSounds.PELICAN_HURT.get();
    }
    //endregion

    //region Animations
    private <E extends IAnimatable> PlayState walkCycle(AnimationEvent<E> event) {
        var builder = new AnimationBuilder();
        if (event.isMoving() && this.onGround) {
            builder.addAnimation("animation.pelican.walk", true);
        } else if (!this.onGround) {
            builder.addAnimation("animation.pelican.glide", true);
        } else {
            builder.addAnimation("animation.pelican.idle", true);
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
        return EntityModel.PELICAN;
    }
    //endregion
}
