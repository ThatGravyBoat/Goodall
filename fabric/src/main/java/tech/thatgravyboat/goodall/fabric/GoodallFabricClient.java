package tech.thatgravyboat.goodall.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.client.GoodallClient;
import tech.thatgravyboat.goodall.common.registry.fabric.ModParticlesImpl;

public class GoodallFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientSpriteRegistryCallback.event(InventoryMenu.BLOCK_ATLAS).register((atlas, registry) ->
                ModParticlesImpl.TEXTURES.stream()
                        .map(id -> new ResourceLocation(Goodall.MOD_ID, "particle/" + id))
                        .forEachOrdered(registry::register)
        );
        GoodallClient.init();
        GoodallClient.initParticleFactories();
    }
}
