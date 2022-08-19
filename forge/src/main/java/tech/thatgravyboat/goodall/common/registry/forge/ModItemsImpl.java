package tech.thatgravyboat.goodall.common.registry.forge;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.client.renderer.deerhead.DeerHeadBlockItemRenderer;
import tech.thatgravyboat.goodall.common.item.DeerHeadBlockItem;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModItemsImpl {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Goodall.MOD_ID);

    public static <T extends Mob> Supplier<SpawnEggItem> registerSpawnEgg(String id, Supplier<EntityType<T>> entity, int primary, int secondary, Item.Properties properties) {
        return ITEMS.register(id, () -> new ForgeSpawnEggItem(entity, primary, secondary, properties));
    }

    public static <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
        return ITEMS.register(id, item);
    }

    public static DeerHeadBlockItem createDeerHeadBlock(Block block, Item.Properties properties) {
        return new DeerHeadBlockItem(block, properties) {
            @Override
            public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
                super.initializeClient(consumer);
                consumer.accept(new IClientItemExtensions() {
                    private final BlockEntityWithoutLevelRenderer renderer = new DeerHeadBlockItemRenderer();

                    @Override
                    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                        return renderer;
                    }
                });
            }
        };
    }

    public static <T extends Entity>  Supplier<MobBucketItem> registerBucket(String id, Supplier<EntityType<T>> entity, Supplier<Fluid> fluid, Supplier<SoundEvent> soundEvent, Item.Properties properties) {
        return ITEMS.register(id, () -> new MobBucketItem(entity, fluid, soundEvent, properties));
    }
}
