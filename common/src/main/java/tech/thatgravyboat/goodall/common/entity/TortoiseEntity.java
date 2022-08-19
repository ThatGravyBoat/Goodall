package tech.thatgravyboat.goodall.common.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
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
import tech.thatgravyboat.goodall.common.entity.goals.tortoise.LayEggGoal;
import tech.thatgravyboat.goodall.common.entity.goals.tortoise.MateGoal;
import tech.thatgravyboat.goodall.common.registry.ModEntities;
import tech.thatgravyboat.goodall.common.registry.ModItems;

public class TortoiseEntity extends Animal implements IAnimatable, IEntityModel {

    public static final EntityDataAccessor<Boolean> HAS_EGG = SynchedEntityData.defineId(TortoiseEntity.class, EntityDataSerializers.BOOLEAN);

    private static final Ingredient BREEDING_ITEM = Ingredient.of(Items.CACTUS);

    private final AnimationFactory factory = new AnimationFactory(this);

    public TortoiseEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createTortoiseAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.MOVEMENT_SPEED, 0.15D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.1));
        this.goalSelector.addGoal(1, new MateGoal(this, 1));
        this.goalSelector.addGoal(2, new LayEggGoal(this, 1));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, BREEDING_ITEM, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1));
    }

    //region State Management
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_EGG, false);
    }

    public boolean hasEgg() {
        return this.entityData.get(HAS_EGG);
    }

    public void setHasEgg(boolean egg) {
        this.entityData.set(HAS_EGG, egg);
    }
    //endregion


    @Override
    public boolean isFood(@NotNull ItemStack stack) {
        return BREEDING_ITEM.test(stack);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob entity) {
        return ModEntities.TORTOISE.get().create(level);
    }

    @Override
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        if (!this.isBaby() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.spawnAtLocation(ModItems.TORTOISE_SCUTE.get(), 1);
        }
    }

    //region Animation
    private <E extends IAnimatable> PlayState animation(AnimationEvent<E> event) {
        if (event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.tortoise.walking", true));
        } else {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.tortoise.idle", true));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 5, this::animation));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
    //endregion

    @Override
    public EntityModel getEntityModel() {
        return EntityModel.TORTOISE;
    }
}
