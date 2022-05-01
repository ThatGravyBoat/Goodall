package tech.thatgravyboat.goodall.common.registry.fabric;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.EntityBucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.sound.SoundEvent;
import tech.thatgravyboat.goodall.common.item.DeerHeadBlockItem;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModItemsImpl {

    public static final Map<String, Supplier<Item>> ITEMS = new LinkedHashMap<>();

    public static <T extends MobEntity> Supplier<SpawnEggItem> registerSpawnEgg(String id, Supplier<EntityType<T>> entity, int primary, int secondary, Item.Settings settings) {
        SpawnEggItem item = new SpawnEggItem(entity.get(), primary, secondary, settings);
        ITEMS.put(id, () -> item);
        return () -> item;
    }

    public static <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
        var item1 = item.get();
        ITEMS.put(id, () -> item1);
        return () -> item1;
    }

    public static DeerHeadBlockItem createDeerHeadBlock(Block block, Item.Settings settings) {
        return new DeerHeadBlockItem(block, settings);
    }

    public static <T extends Entity>  Supplier<EntityBucketItem> registerBucket(String id, Supplier<EntityType<T>> entity, Supplier<Fluid> fluid, Supplier<SoundEvent> soundEvent, Item.Settings settings) {
        EntityBucketItem item1 = new EntityBucketItem(entity.get(), fluid.get(), soundEvent.get(), settings);
        ITEMS.put(id, () -> item1);
        return () -> item1;
    }
}
