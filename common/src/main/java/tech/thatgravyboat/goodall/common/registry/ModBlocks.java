package tech.thatgravyboat.goodall.common.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.jetbrains.annotations.NotNull;
import tech.thatgravyboat.goodall.common.block.*;

import java.util.List;
import java.util.function.Supplier;

public class ModBlocks {

    public static final Supplier<CrossBlock> CROSS = registerBlock("treasure_cross", () -> new CrossBlock(BlockBehaviour.Properties.of(Material.AIR).noLootTable()));
    public static final Supplier<AnimalCrateBlock> ANIMAL_CRATE = registerBlock("animal_crate", () -> new AnimalCrateBlock(BlockBehaviour.Properties.of(Material.WOOD)));
    public static final Supplier<DeerHeadBlock> DEER_HEAD = registerBlock("deer_head", () -> new DeerHeadBlock(BlockBehaviour.Properties.of(Material.WOOD).destroyTime(2f)));
    public static final Supplier<TortoiseEggBlock> TORTOISE_EGG = registerBlock("tortoise_egg", () -> new TortoiseEggBlock(BlockBehaviour.Properties.copy(Blocks.TURTLE_EGG)));
    public static final Supplier<QuickSandBlock> QUICKSAND = registerBlock("quicksand", () -> new QuickSandBlock(BlockBehaviour.Properties.copy(Blocks.SAND).dynamicShape().isSuffocating((s, w, p) -> true).isViewBlocking((s, w, p) -> true)));
    public static final Supplier<Block> SCUTE_BLOCK = registerBlock("tortoise_scute_block", () -> new Block(BlockBehaviour.Properties.of(Material.SHULKER_SHELL, MaterialColor.TERRACOTTA_BROWN).strength(2.5f, 2.5f)));
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
