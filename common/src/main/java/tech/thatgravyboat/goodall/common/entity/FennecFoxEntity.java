package tech.thatgravyboat.goodall.common.entity;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
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
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.common.entity.base.EntityModel;
import tech.thatgravyboat.goodall.common.entity.base.IEntityModel;
import tech.thatgravyboat.goodall.common.entity.base.ItemPicker;
import tech.thatgravyboat.goodall.common.entity.goals.PickupItemGoal;

import java.util.function.Predicate;

public class FennecFoxEntity extends TamableAnimal implements ItemPicker, IAnimatable, IEntityModel {

    private static final Predicate<LivingEntity> ATTACK_PREDICATE = entity -> entity instanceof Chicken || entity instanceof Rabbit;
    private static final Predicate<LivingEntity> FLEE_PREDICATE = (entity) -> !entity.isShiftKeyDown() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity);

    private final AnimationFactory factory = new AnimationFactory(this);

    private int eatingTime;

    public FennecFoxEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.setCanPickUpLoot(true);
    }

    public static AttributeSupplier.Builder createFennecFoxAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.5D, false));
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.1D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1D));
        this.goalSelector.addGoal(6, new AvoidEntityGoal<>(this, Player.class, 16.0F, 1.5D, 1.3D, player -> FLEE_PREDICATE.test(player) && !this.isOwnedBy(player)));
        this.goalSelector.addGoal(7, new PickupItemGoal<>(this, 1.1D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Animal.class, true, ATTACK_PREDICATE));
    }

    //region Taming
    @Override
    public InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (this.level.isClientSide) {
            boolean sitCondition = this.isOwnedBy(player) || this.isTame();
            boolean tamingCondition = itemStack.is(Items.CHICKEN) && !this.isTame() && this.getLastHurtByMob() == null && this.getTarget() == null;
            return sitCondition || tamingCondition ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else {
            if (this.isTame()) {
                InteractionResult actionResult = super.mobInteract(player, hand);
                if (!actionResult.consumesAction() && this.isOwnedBy(player)) {
                    if (player.isShiftKeyDown()) {
                        ItemStack dropStack = this.getItemBySlot(EquipmentSlot.MAINHAND).copy();
                        if (!dropStack.isEmpty() && !this.level.isClientSide) {
                            ItemEntity itemEntity = new ItemEntity(this.level, this.getX() + this.getLookAngle().x, this.getY() + 1.0D, this.getZ() + this.getLookAngle().z, dropStack);
                            itemEntity.setPickUpDelay(40);
                            itemEntity.setThrower(this.getUUID());
                            this.level.addFreshEntity(itemEntity);
                        }
                        this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    } else {
                        this.setOrderedToSit(!this.isOrderedToSit());
                        this.jumping = false;
                        this.navigation.stop();
                        this.setTarget(null);
                    }
                    return InteractionResult.SUCCESS;
                }

                return actionResult;
            } else if (itemStack.is(Items.CHICKEN) && this.getLastHurtByMob() == null && this.getTarget() == null) {
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }

                if (this.random.nextInt(3) == 0) {
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
    }
    //endregion

    //region Eating
    @Override
    public void aiStep() {
        if (!this.level.isClientSide && this.isAlive() && this.isEffectiveAi()) {
            ++this.eatingTime;
            ItemStack stack = this.getItemBySlot(EquipmentSlot.MAINHAND);
            if (stack.is(Items.COOKED_CHICKEN) || stack.is(Items.SWEET_BERRIES) || stack.is(Items.GLOW_BERRIES)) {
                if (this.eatingTime > 600) {
                    ItemStack itemStack2 = stack.finishUsingItem(this.level, this);
                    if (!itemStack2.isEmpty()) {
                        this.setItemSlot(EquipmentSlot.MAINHAND, itemStack2);
                    }

                    FoodProperties food = stack.getItem().getFoodProperties();
                    if (food != null) {
                        this.heal(food.getNutrition()/2f);
                    }

                    this.eatingTime = 0;
                } else if (this.eatingTime > 560 && this.random.nextFloat() < 0.1F) {
                    this.playSound(this.getEatingSound(stack), 1.0F, 1.0F);
                    this.level.broadcastEntityEvent(this, (byte)45);
                }
            }
        }
        super.aiStep();
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == 45) {
            ItemStack itemStack = this.getMainHandItem();
            if (!itemStack.isEmpty()) {
                for(int i = 0; i < 8; ++i) {
                    Vec3 vec3d = new Vec3((this.random.nextDouble() - 0.5D) * 0.1D, this.random.nextDouble() * 0.1D + 0.1D, 0.0D)
                            .xRot(-this.getXRot() * 0.017453292F)
                            .yRot(-this.getYRot() * 0.017453292F);
                    this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, itemStack), this.getX() + this.getLookAngle().x / 2.0D, this.getY(), this.getZ() + this.getLookAngle().z / 2.0D, vec3d.x, vec3d.y + 0.05D, vec3d.z);
                }
            }
        } else {
            super.handleEntityEvent(status);
        }
    }
    //endregion

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob entity) {
        return null;
    }

    @Override
    public boolean canPickUpItems() {
        return this.eatingTime == 0 && this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty();
    }

    //region Animation
    private <E extends IAnimatable> PlayState walkCycle(AnimationEvent<E> event) {
        var builder = new AnimationBuilder();
        if (this.isInSittingPose()) {
            builder.addAnimation("animation.fennec_fox.sleep", true);
        } else if (event.isMoving()) {
            builder.addAnimation("animation.fennec_fox.walking", true);
        } else {
            builder.addAnimation("animation.fennec_fox.idle", true);
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

    //region Texture
    @Override
    public EntityModel getEntityModel() {
        return EntityModel.FENNEC;
    }

    @Override
    public ResourceLocation getITexture() {
        return isSleeping() ? new ResourceLocation(Goodall.MOD_ID, "textures/entity/fennec_fox/sleeping.png") : new ResourceLocation(Goodall.MOD_ID, "textures/entity/fennec_fox/normal.png");
    }
    //endregion
}
