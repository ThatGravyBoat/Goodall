package tech.thatgravyboat.goodall.common.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import tech.thatgravyboat.goodall.common.item.AnimalCrateBlockItem;
import tech.thatgravyboat.goodall.common.item.DeerHeadBlockItem;

import java.util.function.Supplier;

public class ModItems {

    public static final Supplier<SpawnEggItem> RHINO_SPAWN_EGG = registerSpawnEgg("rhino_spawn_egg", ModEntities.RHINO, 0x847C72, 0x62554E, new Item.Settings().group(ItemGroup.MISC));
    public static final Supplier<SpawnEggItem> DUMBO_SPAWN_EGG = registerSpawnEgg("dumbo_octopus_spawn_egg", ModEntities.DUMBO, 0xFFD334, 0xF09014, new Item.Settings().group(ItemGroup.MISC));
    public static final Supplier<SpawnEggItem> BOOBY_SPAWN_EGG = registerSpawnEgg("blue_footed_booby_spawn_egg", ModEntities.BOOBY, 0x714F39, 0x55C9F0, new Item.Settings().group(ItemGroup.MISC));
    public static final Supplier<SpawnEggItem> FENNEC_FOX_SPAWN_EGG = registerSpawnEgg("fennec_fox_spawn_egg", ModEntities.FENNEC_FOX, 0xD3B692, 0xE6AE9D, new Item.Settings().group(ItemGroup.MISC));
    public static final Supplier<SpawnEggItem> KIWI_SPAWN_EGG = registerSpawnEgg("kiwi_spawn_egg", ModEntities.KIWI, 0x62503D, 0xE6CB9F, new Item.Settings().group(ItemGroup.MISC));
    public static final Supplier<SpawnEggItem> MANATEE_SPAWN_EGG = registerSpawnEgg("manatee_spawn_egg", ModEntities.MANATEE, 0x727171, 0xBABAB9, new Item.Settings().group(ItemGroup.MISC));
    public static final Supplier<SpawnEggItem> SEAL_SPAWN_EGG = registerSpawnEgg("seal_spawn_egg", ModEntities.SEAL, 0x584E4E, 0x76706E, new Item.Settings().group(ItemGroup.MISC));
    public static final Supplier<SpawnEggItem> WHITE_DEER_SPAWN_EGG = registerSpawnEgg("white_tailed_deer_spawn_egg", ModEntities.WHITE_DEER, 0xCEA97B, 0xD3BCB3, new Item.Settings().group(ItemGroup.MISC));
    public static final Supplier<SpawnEggItem> RED_DEER_SPAWN_EGG = registerSpawnEgg("red_deer_spawn_egg", ModEntities.RED_DEER, 0x622A2A, 0xB1A191, new Item.Settings().group(ItemGroup.MISC));
    public static final Supplier<SpawnEggItem> FLAMINGO_SPAWN_EGG = registerSpawnEgg("flamingo_spawn_egg", ModEntities.FLAMINGO, 0xF099AC, 0x31282B, new Item.Settings().group(ItemGroup.MISC));
    public static final Supplier<EntityBucketItem> DUMBO_BUCKET = registerBucket("dumbo_octopus_bucket", ModEntities.DUMBO, () -> Fluids.WATER, () -> SoundEvents.ITEM_BUCKET_EMPTY_FISH, new Item.Settings().group(ItemGroup.MISC));
    public static final Supplier<AnimalCrateBlockItem> ANIMAL_CRATE = registerItem("animal_crate", () -> new AnimalCrateBlockItem(new Item.Settings().group(ItemGroup.MISC).maxCount(1)));
    public static final Supplier<DeerHeadBlockItem> DEER_HEAD = registerItem("deer_head", () -> createDeerHeadBlock(ModBlocks.DEER_HEAD.get(), new Item.Settings().group(ItemGroup.MISC)));
    public static final Supplier<Item> RAW_VENISON = registerItem("raw_venison", () -> new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.BEEF)));
    public static final Supplier<Item> COOKED_VENISON = registerItem("cooked_venison", () -> new Item(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.COOKED_BEEF)));

    public static void register() {
        //Initialize class
    }

    @ExpectPlatform
    public static <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends MobEntity> Supplier<SpawnEggItem> registerSpawnEgg(String id, Supplier<EntityType<T>> entity, int primary, int secondary, Item.Settings settings) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Entity> Supplier<EntityBucketItem> registerBucket(String id, Supplier<EntityType<T>> entity, Supplier<Fluid> fluid, Supplier<SoundEvent> soundEvent, Item.Settings settings) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static DeerHeadBlockItem createDeerHeadBlock(Block block, Item.Settings settings) {
        throw new AssertionError();
    }
}
