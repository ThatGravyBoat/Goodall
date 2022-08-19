package tech.thatgravyboat.goodall.common.fabric.config;

import tech.thatgravyboat.goodall.config.PropertyType;
import tech.thatgravyboat.goodall.config.annotations.Config;
import tech.thatgravyboat.goodall.config.annotations.Property;

@Config("goodall")
public class GoodallConfig {

    @Property(type = PropertyType.CATEGORY, description = "Spawn Configurations for all the mobs in goodall.")
    public SpawnConfig spawnConfig = new SpawnConfig();
}
