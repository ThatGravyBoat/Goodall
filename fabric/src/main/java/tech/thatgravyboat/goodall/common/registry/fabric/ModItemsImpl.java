package tech.thatgravyboat.goodall.common.registry.fabric;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import tech.thatgravyboat.goodall.common.item.DeerHeadBlockItem;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModItemsImpl {

    public static final Map<String, Supplier<Item>> ITEMS = new LinkedHashMap<>();

    public static <T extends Mob> Supplier<SpawnEggItem> registerSpawnEgg(String id, Supplier<EntityType<T>> entity, int primary, int secondary, Item.Properties properties) {
        SpawnEggItem item = new SpawnEggItem(entity.get(), primary, secondary, properties);
        ITEMS.put(id, () -> item);
        return () -> item;
    }

    public static <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
        var item1 = item.get();
        ITEMS.put(id, () -> item1);
        return () -> item1;
    }

    public static DeerHeadBlockItem createDeerHeadBlock(Block block, Item.Properties properties) {
        return new DeerHeadBlockItem(block, properties);
    }

    public static <T extends Entity>  Supplier<MobBucketItem> registerBucket(String id, Supplier<EntityType<T>> entity, Supplier<Fluid> fluid, Supplier<SoundEvent> soundEvent, Item.Properties properties) {
        MobBucketItem item1 = new MobBucketItem(entity.get(), fluid.get(), soundEvent.get(), properties);
        ITEMS.put(id, () -> item1);
        return () -> item1;
    }
}
