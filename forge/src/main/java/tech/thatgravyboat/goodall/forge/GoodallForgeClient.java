package tech.thatgravyboat.goodall.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import tech.thatgravyboat.goodall.Goodall;
import tech.thatgravyboat.goodall.client.GoodallClient;
import tech.thatgravyboat.goodall.client.forge.GoodallClientImpl;

@Mod.EventBusSubscriber(modid= Goodall.MOD_ID, value= Dist.CLIENT, bus= Mod.EventBusSubscriber.Bus.MOD)
public class GoodallForgeClient {

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(GoodallForgeClient::onClientInit);
    }

    public static void onClientInit(FMLCommonSetupEvent event) {
        GoodallClient.init();
    }

    @SubscribeEvent
    public static void particleFactoryRegistry(RegisterParticleProvidersEvent event) {
        GoodallClient.initParticleFactories();
        GoodallClientImpl.PARTICLES.forEach((particle, factory) -> event.register(particle.get(), factory::create));
    }
}
