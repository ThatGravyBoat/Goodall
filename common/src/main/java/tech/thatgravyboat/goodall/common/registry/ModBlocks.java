package tech.thatgravyboat.goodall.common.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import tech.thatgravyboat.goodall.common.block.*;

import java.util.List;
import java.util.function.Supplier;

public class ModBlocks {

    public static final Supplier<CrossBlock> CROSS = registerBlock("treasure_cross", () -> new CrossBlock(AbstractBlock.Settings.of(Material.AIR).dropsNothing()));
    public static final Supplier<AnimalCrateBlock> ANIMAL_CRATE = registerBlock("animal_crate", () -> new AnimalCrateBlock(AbstractBlock.Settings.of(Material.WOOD)));
    public static final Supplier<DeerHeadBlock> DEER_HEAD = registerBlock("deer_head", () -> new DeerHeadBlock(AbstractBlock.Settings.of(Material.WOOD).hardness(2f)));
    public static final Supplier<BlockEntityType<AnimalCrateBlockEntity>> ANIMAL_CRATE_ENTITY = registerBlockEntity("animal_crate", AnimalCrateBlockEntity::new, List.of(ANIMAL_CRATE));
    public static final Supplier<BlockEntityType<DeerHeadBlockEntity>> DEER_HEAD_ENTITY = registerBlockEntity("deer_head", DeerHeadBlockEntity::new, List.of(DEER_HEAD));

    public static void register() {
        //Initialize class
    }

    public static <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String id, BlockEntityFactory<T> factory, List<Supplier<? extends Block>> blocks) {
        return registerBlockEntity(id,
                () -> createBlockEntityType(id, factory, blocks.stream()
                .map(Supplier::get)
                .toList()
                .toArray(new Block[]{}))
        );
    }

    @ExpectPlatform
    public static <T extends Block> Supplier<T> registerBlock(String id, Supplier<T> block) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String id, Supplier<BlockEntityType<T>> block) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(String id, BlockEntityFactory<T> factory, Block... blocks) {
        throw new AssertionError();
    }

    @FunctionalInterface
    public interface BlockEntityFactory<T extends BlockEntity> {
        @NotNull T create(BlockPos blockPos, BlockState blockState);
    }
}
