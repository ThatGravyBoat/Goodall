package tech.thatgravyboat.goodall.common.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import tech.thatgravyboat.goodall.common.item.AnimalCrateBlockItem;
import tech.thatgravyboat.goodall.common.item.DeerHeadBlockItem;
import tech.thatgravyboat.goodall.common.item.GenericBlockBucket;
import tech.thatgravyboat.goodall.common.item.ReturnableEdibleItem;

import java.util.function.Supplier;

public class ModItems {

    public static final Supplier<SpawnEggItem> RHINO_SPAWN_EGG = registerSpawnEgg("rhino_spawn_egg", ModEntities.RHINO, 0x847C72, 0x62554E, new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    public static final Supplier<SpawnEggItem> DUMBO_SPAWN_EGG = registerSpawnEgg("dumbo_octopus_spawn_egg", ModEntities.DUMBO, 0xFFD334, 0xF09014, new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    public static final Supplier<SpawnEggItem> PELICAN_SPAWN_EGG = registerSpawnEgg("pelican_spawn_egg", ModEntities.PELICAN, 0xFAEFE3, 0xE29417, new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    public static final Supplier<SpawnEggItem> FENNEC_FOX_SPAWN_EGG = registerSpawnEgg("fennec_fox_spawn_egg", ModEntities.FENNEC_FOX, 0xD3B692, 0xE6AE9D, new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    public static final Supplier<SpawnEggItem> KIWI_SPAWN_EGG = registerSpawnEgg("kiwi_spawn_egg", ModEntities.KIWI, 0x62503D, 0xE6CB9F, new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    public static final Supplier<SpawnEggItem> MANATEE_SPAWN_EGG = registerSpawnEgg("manatee_spawn_egg", ModEntities.MANATEE, 0x727171, 0xBABAB9, new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    public static final Supplier<SpawnEggItem> SEAL_SPAWN_EGG = registerSpawnEgg("seal_spawn_egg", ModEntities.SEAL, 0x584E4E, 0x76706E, new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    public static final Supplier<SpawnEggItem> DEER_SPAWN_EGG = registerSpawnEgg("deer_spawn_egg", ModEntities.DEER, 0x936B50, 0xE6D89B, new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    public static final Supplier<SpawnEggItem> FLAMINGO_SPAWN_EGG = registerSpawnEgg("flamingo_spawn_egg", ModEntities.FLAMINGO, 0xF099AC, 0x31282B, new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    public static final Supplier<SpawnEggItem> SONGBIRD_SPAWN_EGG = registerSpawnEgg("songbird_spawn_egg", ModEntities.SONGBIRD, 0x5EABE6, 0x36332F, new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    public static final Supplier<SpawnEggItem> TOUCAN_SPAWN_EGG = registerSpawnEgg("toucan_spawn_egg", ModEntities.TOUCAN, 0x2C2926, 0xCE6F32, new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    public static final Supplier<SpawnEggItem> TORTOISE_SPAWN_EGG = registerSpawnEgg("tortoise_spawn_egg", ModEntities.TORTOISE, 0xC4A573, 0x4E312D, new Item.Properties().tab(CreativeModeTab.TAB_MISC));

    public static final Supplier<MobBucketItem> DUMBO_BUCKET = registerBucket("dumbo_octopus_bucket", ModEntities.DUMBO, () -> Fluids.WATER, () -> SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    public static final Supplier<AnimalCrateBlockItem> ANIMAL_CRATE = registerItem("animal_crate", () -> new AnimalCrateBlockItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(1)));
    public static final Supplier<DeerHeadBlockItem> DEER_HEAD = registerItem("deer_head", () -> createDeerHeadBlock(ModBlocks.DEER_HEAD.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));
    public static final Supplier<Item> RAW_VENISON = registerItem("raw_venison", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_FOOD).food(Foods.BEEF)));
    public static final Supplier<Item> COOKED_VENISON = registerItem("cooked_venison", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_FOOD).food(Foods.COOKED_BEEF)));
    public static final Supplier<Item> TORTOISE_SCUTE = registerItem("tortoise_scute", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final Supplier<Item> TORTOISE_SCUTE_BLOCK = registerItem("tortoise_scute_block", () -> new BlockItem(ModBlocks.SCUTE_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));
    public static final Supplier<Item> QUICKSAND_BUCKET = registerItem("quicksand_bucket", () -> new GenericBlockBucket(ModBlocks.QUICKSAND.get(), SoundEvents.SAND_PLACE, new Item.Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(1)));
    public static final Supplier<Item> TORTOISE_EGG = registerItem("tortoise_egg", () -> new BlockItem(ModBlocks.TORTOISE_EGG.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final Supplier<Item> KRILL_BUCKET = registerItem("bucket_of_krill", () -> new ReturnableEdibleItem(Items.BUCKET, new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).build())));

    public static void register() {
        //Initialize class
    }

    @ExpectPlatform
    public static <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Mob> Supplier<SpawnEggItem> registerSpawnEgg(String id, Supplier<EntityType<T>> entity, int primary, int secondary, Item.Properties settings) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Entity> Supplier<MobBucketItem> registerBucket(String id, Supplier<EntityType<T>> entity, Supplier<Fluid> fluid, Supplier<SoundEvent> soundEvent, Item.Properties settings) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static DeerHeadBlockItem createDeerHeadBlock(Block block, Item.Properties settings) {
        throw new AssertionError();
    }
}
