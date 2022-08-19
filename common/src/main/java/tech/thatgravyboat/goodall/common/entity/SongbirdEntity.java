package tech.thatgravyboat.goodall.common.entity;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import tech.thatgravyboat.goodall.common.entity.base.BirdEntity;
import tech.thatgravyboat.goodall.common.entity.base.EntityModel;
import tech.thatgravyboat.goodall.common.entity.base.IEntityModel;
import tech.thatgravyboat.goodall.common.lib.DeerVariant;
import tech.thatgravyboat.goodall.common.lib.SongBirdVariant;

public class SongbirdEntity extends BirdEntity implements IAnimatable, IEntityModel {

    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(SongbirdEntity.class, EntityDataSerializers.INT);

    private final AnimationFactory factory = new AnimationFactory(this);
    private int healTicker = 0;

    public SongbirdEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType spawnReason, @Nullable SpawnGroupData entityData, @Nullable CompoundTag tag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnReason, entityData, tag);
        Holder<Biome> biome = level.getBiome(this.blockPosition());
        if (biome.isBound()) {
            ResourceKey<Biome> key = biome.unwrapKey().orElse(null);
            setVariant(Biomes.DARK_FOREST.equals(key) ? SongBirdVariant.BLUE : SongBirdVariant.random(random));
        }
        return data;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.healTicker++;
        if (this.healTicker + this.random.nextInt(15) >= 100) {
            this.healTicker = 0;
            this.heal(2f);
        }
    }

    //region State Management
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, 0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", getVariant().ordinal());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setVariant(SongBirdVariant.getVariant(tag.getInt("Variant")));
    }

    public SongBirdVariant getVariant() {
        return SongBirdVariant.getVariant(this.entityData.get(VARIANT));
    }

    public void setVariant(SongBirdVariant variant) {
        this.entityData.set(VARIANT, variant.ordinal());
    }
    //endregion

    //region Animation
    private <E extends IAnimatable> PlayState animation(AnimationEvent<E> event) {
        if (isFlying()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.songbird.fly", true));
        } else if (event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.songbird.walking", true));
        } else  {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.songbird.idle", true));
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

    //region Texturing
    @Override
    public EntityModel getEntityModel() {
        return EntityModel.SONGBIRD;
    }

    @Override
    public ResourceLocation getITexture() {
        return SongBirdVariant.getVariantForName(this.getName().getString()).orElse(getVariant()).texture;
    }
    //endregion
}
