package tech.thatgravyboat.goodall.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;
import tech.thatgravyboat.goodall.common.registry.ModEntities;
import tech.thatgravyboat.goodall.common.registry.ModItems;
import tech.thatgravyboat.goodall.common.registry.ModParticles;

public class KrillEntity extends Entity {
    private static final EntityDataAccessor<Float> RADIUS = SynchedEntityData.defineId(KrillEntity.class, EntityDataSerializers.FLOAT);

    private int maxAge;

    public KrillEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.maxAge = 600;
        this.noPhysics = true;
        this.setRadius(3.0F);
    }

    public KrillEntity(Level level, double x, double y, double z) {
        this(ModEntities.KRILL.get(), level);
        this.setPos(x, y, z);
    }

    @Override
    public InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.WATER_BUCKET)) {
            if (!player.level.isClientSide && getRadius() > 1) {
                player.setItemInHand(hand, new ItemStack(ModItems.KRILL_BUCKET.get()));
                setRadius(getRadius() - 1);
            }
            return InteractionResult.sidedSuccess(player.level.isClientSide);
        }
        return super.interact(player, hand);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            if (this.random.nextBoolean()) return;
            float radius = getRadius();

            for(int j = 0; j < Mth.ceil(3.1415927F * radius * radius); ++j) {
                float h = this.random.nextFloat() * 6.2831855F;
                float k = Mth.sqrt(this.random.nextFloat()) * radius;
                double d = this.getX() + (double)(Mth.cos(h) * k);
                double e = this.getY();
                double l = this.getZ() + (double)(Mth.sin(h) * k);

                this.level.addAlwaysVisibleParticle(ModParticles.KRILL.get(), d, e, l, 0, 0, 0);
            }
        } else if (this.tickCount >= this.maxAge) {
            float radius = getRadius();

            if (this.tickCount % 3 == 0) {
                setRadius(radius - 0.1f);
            }

            if (radius <= 0f) {
                this.discard();
            }

        }
    }

    //region Data Tracker
    @Override
    protected void defineSynchedData() {
        this.entityData.define(RADIUS, 0.5F);
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> data) {
        if (RADIUS.equals(data)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(data);
    }

    public void setRadius(float radius) {
        if (!this.level.isClientSide) {
            this.entityData.set(RADIUS, Mth.clamp(radius, 0.0F, 32.0F));
        }
    }

    public float getRadius() {
        return this.entityData.get(RADIUS);
    }
    //endregion

    //region NBT
    protected void readAdditionalSaveData(CompoundTag nbt) {
        this.tickCount = nbt.getInt("Age");
        this.maxAge = nbt.getInt("MaxAge");
        this.setRadius(nbt.getFloat("Radius"));
    }

    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putInt("Age", this.tickCount);
        nbt.putInt("MaxAge", this.maxAge);
        nbt.putFloat("Radius", this.getRadius());
    }
    //endregion

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public void refreshDimensions() {
        double d = this.getX();
        double e = this.getY();
        double f = this.getZ();
        super.refreshDimensions();
        this.setPos(d, e, f);
    }

    @Override
    public EntityDimensions getDimensions(@NotNull Pose pose) {
        return EntityDimensions.scalable(this.getRadius() * 2.0F, 0.5F);
    }
}
