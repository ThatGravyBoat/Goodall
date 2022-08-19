package tech.thatgravyboat.goodall.common.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.AnimationState;
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
import tech.thatgravyboat.goodall.common.registry.ModEntities;
import tech.thatgravyboat.goodall.common.registry.ModSounds;

public class FlamingoEntity extends Animal implements IAnimatable, IEntityModel {

    private static final Ingredient BREEDING_INGREDIENT = Ingredient.of(Items.COD, Items.TROPICAL_FISH, Items.SALMON);

    private final AnimationFactory factory = new AnimationFactory(this);

    public float flapProgress;
    public float maxWingDeviation;
    public float prevMaxWingDeviation;
    public float prevFlapProgress;
    public float flapSpeed = 1.0F;
    private float nextFlap = 1.0F;

    public FlamingoEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createFlamingoAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 16D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.1D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, BREEDING_INGREDIENT, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 0.45D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    public double getFluidJumpThreshold() {
        return 1.1;
    }

    //region Sounds
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.FLAMINGO_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return ModSounds.FLAMINGO_HURT.get();
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

    //region Breeding
    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob entity) {
        return ModEntities.FLAMINGO.get().create(level);
    }

    @Override
    public boolean isFood(@NotNull ItemStack stack) {
        return BREEDING_INGREDIENT.test(stack);
    }
    //endregion

    //region Animations
    private <E extends IAnimatable> PlayState walkCycle(AnimationEvent<E> event) {
        var builder = new AnimationBuilder();
        if (event.isMoving() && this.onGround) {
            builder.addAnimation("animation.flamingo.walking", true);
        } else if (!this.onGround) {
            builder.addAnimation("animation.flamingo.fly", true);
        } else {
            var currentAnimation = event.getController().getCurrentAnimation();
            if (currentAnimation != null && currentAnimation.animationName.startsWith("animation.flamingo.idle") && !event.getController().getAnimationState().equals(AnimationState.Stopped)) {
                return PlayState.CONTINUE;
            } else {
                builder.addAnimation(this.random.nextBoolean() ? "animation.flamingo.idle" : "animation.flamingo.idle2", true);
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

    @Override
    public EntityModel getEntityModel() {
        return EntityModel.FLAMINGO;
    }

    @Override
    public ResourceLocation getITexture() {
        return this.isBaby() ? new ResourceLocation(Goodall.MOD_ID, "textures/entity/flamingo/baby.png") : new ResourceLocation(Goodall.MOD_ID, "textures/entity/flamingo/normal.png");
    }

    //endregion
}
