package tech.thatgravyboat.goodall.fabric;

import net.fabricmc.api.ClientModInitializer;
import tech.thatgravyboat.goodall.client.GoodallClient;

public class GoodallFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        GoodallClient.init();
    }
}
