package tech.thatgravyboat.goodall.forge;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import tech.thatgravyboat.goodall.client.GoodallClient;

public class GoodallForgeClient {

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(GoodallForgeClient::onClientInit);
    }

    public static void onClientInit(FMLCommonSetupEvent event) {
        GoodallClient.init();
    }
}
