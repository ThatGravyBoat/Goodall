package tech.thatgravyboat.goodall.common.registry.fabric;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import tech.thatgravyboat.goodall.common.registry.ModBlocks;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModBlocksImpl {

    public static final Map<String, Supplier<Block>> BLOCKS = new LinkedHashMap<>();
    public static final Map<String, Supplier<BlockEntityType<?>>> BLOCK_ENTITIES = new LinkedHashMap<>();

    public static <T extends Block> Supplier<T> registerBlock(String id, Supplier<T> block) {
        var object = block.get();
        BLOCKS.put(id, () -> object);
        return () -> object;
    }

    public static <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String id, Supplier<BlockEntityType<T>> block) {
        var object = block.get();
        BLOCK_ENTITIES.put(id, () -> object);
        return () -> object;
    }

    public static <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(String id, ModBlocks.BlockEntityFactory<T> factory, Block... blocks) {
        return FabricBlockEntityTypeBuilder.create(factory::create, blocks).build();
    }
}
