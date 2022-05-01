package tech.thatgravyboat.goodall.config.forge;

import com.electronwill.nightconfig.core.AbstractConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import tech.thatgravyboat.goodall.config.AnnotationData;
import tech.thatgravyboat.goodall.config.BuiltCategory;
import tech.thatgravyboat.goodall.config.BuiltConfig;
import tech.thatgravyboat.goodall.config.PropertyData;

import java.util.HashMap;
import java.util.Map;

public class ConfigLoaderImpl {

    public static final Map<ForgeConfigSpec, BuiltConfig> CONFIGS = new HashMap<>();

    public static void initialize() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(ConfigLoaderImpl::onConfigLoad);
        bus.addListener(ConfigLoaderImpl::onConfigReload);

        ConfigLoaderImpl.CONFIGS.forEach((spec, config) -> ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, spec, config.fileName+".toml"));
    }

    public static void onConfigLoad(ModConfigEvent.Loading loading) {
        loadConfig(loading.getConfig().getSpec());
    }

    public static void onConfigReload(ModConfigEvent.Reloading loading) {
        loadConfig(loading.getConfig().getSpec());
    }

    private static void loadConfig(IConfigSpec<ForgeConfigSpec> spec) {
        BuiltConfig builtConfig = CONFIGS.get(spec.self());
        if (builtConfig == null) return;
        loadConfig(builtConfig, spec.self().getValues());
    }

    private static void loadConfig(BuiltCategory category, UnmodifiableConfig config) {
        config.valueMap().forEach((id, value) -> {
            if (value instanceof ForgeConfigSpec.ConfigValue<?> configValue) {
                category.getProperty(id).ifPresent(data -> {
                    if (configValue instanceof ForgeConfigSpec.BooleanValue booleanValue)
                        data.setBoolean(booleanValue.get());
                    if (configValue instanceof ForgeConfigSpec.IntValue intValue)
                        data.setInt(intValue.get());
                    if (configValue instanceof ForgeConfigSpec.DoubleValue doubleValue)
                        data.setDouble(doubleValue.get());
                });
            } else if (value instanceof AbstractConfig subConfig) {
                category.getCategory(id).ifPresent(cat -> loadConfig(cat, subConfig));
            }
        });
    }

    public static void registerConfig(BuiltConfig config) {
        try {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
            for (PropertyData property : config.properties) buildProperty(builder, property);
            for (BuiltCategory category : config.categories) buildCategory(builder, category);
            CONFIGS.put(builder.build(), config);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void buildCategory(ForgeConfigSpec.Builder builder, BuiltCategory category) throws IllegalAccessException {
        builder.push(category.category);
        if (category.categoryDesc != null) builder.comment(category.categoryDesc);
        for (PropertyData property : category.properties) buildProperty(builder, property);
        for (BuiltCategory builtCategory : category.categories) buildCategory(builder, builtCategory);
        builder.pop();
    }

    private static void buildProperty(ForgeConfigSpec.Builder builder, PropertyData data) throws IllegalAccessException {
        AnnotationData annotationData = data.data();
        builder.comment(annotationData.description());

        switch (annotationData.type()) {
            case BOOLEAN -> builder.define(data.getId(), data.getBoolean());
            case INT -> builder.defineInRange(data.getId(), data.getInt(), annotationData.min(), annotationData.max());
            case DOUBLE -> builder.defineInRange(data.getId(), data.getDouble(), annotationData.minD(), annotationData.maxD());
            default -> throw new IllegalAccessException("Unknown property type.");
        }
    }
}
