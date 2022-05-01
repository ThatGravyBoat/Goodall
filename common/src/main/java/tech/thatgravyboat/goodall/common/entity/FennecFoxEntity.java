package tech.thatgravyboat.goodall.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
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
import tech.thatgravyboat.goodall.common.entity.base.ItemPicker;
import tech.thatgravyboat.goodall.common.entity.goals.PickupItemGoal;

import java.util.function.Predicate;

public class FennecFoxEntity extends TameableEntity implements ItemPicker, IAnimatable {

    private static final Predicate<LivingEntity> ATTACK_PREDICATE = entity -> entity instanceof ChickenEntity || entity instanceof RabbitEntity;
    private static final Predicate<LivingEntity> FLEE_PREDICATE = (entity) -> !entity.isSneaky() && EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(entity);

    private final AnimationFactory factory = new AnimationFactory(this);

    private int eatingTime;

    public FennecFoxEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.setCanPickUpLoot(true);
    }

    public static DefaultAttributeContainer.Builder createFennecFoxAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2D)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.5D));
        this.goalSelector.add(2, new SitGoal(this));
        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.5D, false));
        this.goalSelector.add(4, new FollowOwnerGoal(this, 1.1D, 10.0F, 2.0F, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1D));
        this.goalSelector.add(6, new FleeEntityGoal<>(this, PlayerEntity.class, 16.0F, 1.5D, 1.3D, player -> FLEE_PREDICATE.test(player) && !this.isOwner(player)));
        this.goalSelector.add(7, new PickupItemGoal<>(this, 1.1D));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(9, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, AnimalEntity.class, true, ATTACK_PREDICATE));
    }

    //region Taming
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (this.world.isClient) {
            boolean sitCondition = this.isOwner(player) || this.isTamed();
            boolean tamingCondition = itemStack.isOf(Items.CHICKEN) && !this.isTamed() && this.getAttacker() == null && this.getTarget() == null;
            return sitCondition || tamingCondition ? ActionResult.CONSUME : ActionResult.PASS;
        } else {
            if (this.isTamed()) {
                ActionResult actionResult = super.interactMob(player, hand);
                if (!actionResult.isAccepted() && this.isOwner(player)) {
                    if (player.isSneaking()) {
                        ItemStack dropStack = this.getEquippedStack(EquipmentSlot.MAINHAND).copy();
                        if (!dropStack.isEmpty() && !this.world.isClient) {
                            ItemEntity itemEntity = new ItemEntity(this.world, this.getX() + this.getRotationVector().x, this.getY() + 1.0D, this.getZ() + this.getRotationVector().z, dropStack);
                            itemEntity.setPickupDelay(40);
                            itemEntity.setThrower(this.getUuid());
                            this.world.spawnEntity(itemEntity);
                        }
                        this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    } else {
                        this.setSitting(!this.isSitting());
                        this.jumping = false;
                        this.navigation.stop();
                        this.setTarget(null);
                    }
                    return ActionResult.SUCCESS;
                }

                return actionResult;
            } else if (itemStack.isOf(Items.CHICKEN) && this.getAttacker() == null && this.getTarget() == null) {
                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }

                if (this.random.nextInt(3) == 0) {
                    this.setOwner(player);
                    this.navigation.stop();
                    this.setTarget(null);
                    this.setSitting(true);
                    this.world.sendEntityStatus(this, (byte)7);
                } else {
                    this.world.sendEntityStatus(this, (byte)6);
                }

                return ActionResult.SUCCESS;
            }

            return super.interactMob(player, hand);
        }
    }
    //endregion

    //region Eating
    @Override
    public void tickMovement() {
        if (!this.world.isClient && this.isAlive() && this.canMoveVoluntarily()) {
            ++this.eatingTime;
            ItemStack stack = this.getEquippedStack(EquipmentSlot.MAINHAND);
            if (stack.isOf(Items.COOKED_CHICKEN) || stack.isOf(Items.SWEET_BERRIES) || stack.isOf(Items.GLOW_BERRIES)) {
                if (this.eatingTime > 600) {
                    ItemStack itemStack2 = stack.finishUsing(this.world, this);
                    if (!itemStack2.isEmpty()) {
                        this.equipStack(EquipmentSlot.MAINHAND, itemStack2);
                    }

                    FoodComponent food = stack.getItem().getFoodComponent();
                    if (food != null) {
                        this.heal(food.getHunger()/2f);
                    }

                    this.eatingTime = 0;
                } else if (this.eatingTime > 560 && this.random.nextFloat() < 0.1F) {
                    this.playSound(this.getEatSound(stack), 1.0F, 1.0F);
                    this.world.sendEntityStatus(this, (byte)45);
                }
            }
        }
        super.tickMovement();
    }

    @Override
    public void handleStatus(byte status) {
        if (status == 45) {
            ItemStack itemStack = this.getEquippedStack(EquipmentSlot.MAINHAND);
            if (!itemStack.isEmpty()) {
                for(int i = 0; i < 8; ++i) {
                    Vec3d vec3d = (new Vec3d(((double)this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D)).rotateX(-this.getPitch() * 0.017453292F).rotateY(-this.getYaw() * 0.017453292F);
                    this.world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack), this.getX() + this.getRotationVector().x / 2.0D, this.getY(), this.getZ() + this.getRotationVector().z / 2.0D, vec3d.x, vec3d.y + 0.05D, vec3d.z);
                }
            }
        } else {
            super.handleStatus(status);
        }
    }
    //endregion

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    @Override
    public boolean canPickUpItems() {
        return this.eatingTime == 0 && this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
    }

    //region Animation
    private <E extends IAnimatable> PlayState walkCycle(AnimationEvent<E> event) {
        var builder = new AnimationBuilder();
        if (this.isInSittingPose()) {
            builder.addAnimation("animation.fennec_fox.sleep", true);
        } else if (event.isMoving()) {
            builder.addAnimation("animation.fennec_fox.walk", true);
        } else {
            builder.addAnimation("animation.fennec_fox.idle", true);
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
