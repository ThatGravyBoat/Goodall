package tech.thatgravyboat.goodall.common.config.spawns;

import tech.thatgravyboat.goodall.config.PropertyType;
import tech.thatgravyboat.goodall.config.annotations.Category;
import tech.thatgravyboat.goodall.config.annotations.Property;

@Category("White Tailed Deer")
public class WhiteDeer {

    @Property(type = PropertyType.INT, description = "The spawn weighting. Note: +5 is added for flower forests.")
    public int weight = 20;

    @Property(type = PropertyType.INT, description = "The min group size.")
    public int min = 1;

    @Property(type = PropertyType.INT, description = "The max group size.")
    public int max = 3;
}
