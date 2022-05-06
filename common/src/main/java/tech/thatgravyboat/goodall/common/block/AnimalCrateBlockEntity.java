package tech.thatgravyboat.goodall.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import tech.thatgravyboat.goodall.common.registry.ModBlocks;

public class AnimalCrateBlockEntity extends BlockEntity {

    private int timer;
    private NbtCompound entity;
    @Nullable
    private SoundEvent entitySound;

    public AnimalCrateBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.ANIMAL_CRATE_ENTITY.get(), pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, AnimalCrateBlockEntity blockEntity) {
        if (blockEntity.entitySound != null) {
            blockEntity.timer++;
            if (blockEntity.timer + (world.random.nextInt(20)) >= 200) {
                blockEntity.timer = 0;
                world.playSound(null, pos, blockEntity.entitySound, SoundCategory.BLOCKS, 1f, 1f);
            }
        }
    }

    public void summonEntity(World world, BlockPos pos) {
        if (this.entity == null) return;
        if (!(world instanceof ServerWorld serverWorld)) return;
        EntityType.fromNbt(this.entity)
            .map(type -> type.create(serverWorld))
            .ifPresent(entity -> {
                entity.readNbt(this.entity);
                entity.updatePositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
                serverWorld.spawnEntity(entity);
            });
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("Entity")) {
            this.entity = nbt.getCompound("Entity");
        }
        if (nbt.contains("Sound")) {
            this.entitySound = Registry.SOUND_EVENT.getOrEmpty(Identifier.tryParse(nbt.getString("Sound"))).orElse(null);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (entity != null) {
            nbt.put("Entity", entity);
        }
        if (entitySound != null) {
            nbt.putString("Sound", entitySound.getId().toString());
        }
    }
}
