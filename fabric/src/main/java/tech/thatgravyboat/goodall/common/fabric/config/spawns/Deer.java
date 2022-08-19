package tech.thatgravyboat.goodall.common.fabric.config.spawns;

import tech.thatgravyboat.goodall.config.PropertyType;
import tech.thatgravyboat.goodall.config.annotations.Category;
import tech.thatgravyboat.goodall.config.annotations.Property;

@Category("Deer")
public class Deer {

    @Property(type = PropertyType.INT, description = "The spawn weighting.")
    public int weight = 10;

    @Property(type = PropertyType.INT, description = "The min group size.")
    public int min = 0;

    @Property(type = PropertyType.INT, description = "The max group size.")
    public int max = 1;
}
