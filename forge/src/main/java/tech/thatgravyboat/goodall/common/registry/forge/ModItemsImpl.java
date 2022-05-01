package tech.thatgravyboat.goodall.common.registry.forge;

import net.minecraft.block.Block;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.EntityBucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.sound.SoundEvent;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.client.renderer.deerhead.DeerHeadBlockItemRenderer;
import tech.thatgravyboat.goodall.common.item.DeerHeadBlockItem;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModItemsImpl {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Goodall.MOD_ID);

    public static <T extends MobEntity> Supplier<SpawnEggItem> registerSpawnEgg(String id, Supplier<EntityType<T>> entity, int primary, int secondary, Item.Settings settings) {
        return ITEMS.register(id, () -> new ForgeSpawnEggItem(entity, primary, secondary, settings));
    }

    public static <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
        return ITEMS.register(id, item);
    }

    public static DeerHeadBlockItem createDeerHeadBlock(Block block, Item.Settings settings) {
        return new DeerHeadBlockItem(block, settings) {
            @Override
            public void initializeClient(Consumer<IItemRenderProperties> consumer) {
                super.initializeClient(consumer);
                consumer.accept(new IItemRenderProperties() {
                    private final BuiltinModelItemRenderer renderer = new DeerHeadBlockItemRenderer();

                    @Override
                    public BuiltinModelItemRenderer getItemStackRenderer() {
                        return renderer;
                    }
                });
            }
        };
    }

    public static <T extends Entity>  Supplier<EntityBucketItem> registerBucket(String id, Supplier<EntityType<T>> entity, Supplier<Fluid> fluid, Supplier<SoundEvent> soundEvent, Item.Settings settings) {
        return ITEMS.register(id, () -> new EntityBucketItem(entity, fluid, soundEvent, settings));
    }
}
