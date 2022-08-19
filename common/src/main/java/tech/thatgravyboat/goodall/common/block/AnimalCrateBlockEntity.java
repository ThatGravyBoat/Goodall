package tech.thatgravyboat.goodall.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.thatgravyboat.goodall.common.registry.ModBlocks;

public class AnimalCrateBlockEntity extends BlockEntity {

    private int timer;
    private CompoundTag entity;
    @Nullable
    private SoundEvent entitySound;

    public AnimalCrateBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.ANIMAL_CRATE_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AnimalCrateBlockEntity blockEntity) {
        if (blockEntity.entitySound != null) {
            blockEntity.timer++;
            if (blockEntity.timer + (level.random.nextInt(20)) >= 200) {
                blockEntity.timer = 0;
                level.playSound(null, pos, blockEntity.entitySound, SoundSource.BLOCKS, 1f, 1f);
            }
        }
    }

    public void summonEntity(Level level, BlockPos pos) {
        if (this.entity == null) return;
        if (!(level instanceof ServerLevel serverLevel)) return;
        EntityType.by(this.entity)
            .map(type -> type.create(serverLevel))
            .ifPresent(entity -> {
                entity.load(this.entity);
                entity.absMoveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
                serverLevel.addFreshEntity(entity);
            });
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("Entity")) {
            this.entity = nbt.getCompound("Entity");
        }
        if (nbt.contains("Sound")) {
            this.entitySound = Registry.SOUND_EVENT.getOptional(ResourceLocation.tryParse(nbt.getString("Sound"))).orElse(null);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        if (entity != null) {
            nbt.put("Entity", entity);
        }
        if (entitySound != null) {
            nbt.putString("Sound", entitySound.getLocation().toString());
        }
    }
}
