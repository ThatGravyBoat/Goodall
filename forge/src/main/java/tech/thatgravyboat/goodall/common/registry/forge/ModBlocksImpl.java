package tech.thatgravyboat.goodall.common.registry.forge;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.common.registry.ModBlocks;

import java.util.function.Supplier;

public class ModBlocksImpl {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Goodall.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Goodall.MOD_ID);

    public static <T extends Block> Supplier<T> registerBlock(String id, Supplier<T> block) {
        return BLOCKS.register(id, block);
    }

    public static <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(String id, ModBlocks.BlockEntityFactory<T> factory, Block... blocks) {
        return BlockEntityType.Builder.create(factory::create, blocks).build(null);
    }

    public static <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String id, Supplier<BlockEntityType<T>> block) {
        return BLOCK_ENTITIES.register(id, block);
    }
}
