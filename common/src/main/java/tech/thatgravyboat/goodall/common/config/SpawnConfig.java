package tech.thatgravyboat.goodall.common.config;

import tech.thatgravyboat.goodall.common.config.spawns.*;
import tech.thatgravyboat.goodall.config.PropertyType;
import tech.thatgravyboat.goodall.config.annotations.Category;
import tech.thatgravyboat.goodall.config.annotations.Property;

@Category("Spawn Configurations")
public class SpawnConfig {

    @Property(type = PropertyType.CATEGORY, description = "Rhino Spawn Configurations")
    public Rhino rhino = new Rhino();

    @Property(type = PropertyType.CATEGORY, description = "Dumbo Octopus Spawn Configurations")
    public Dumbo dumboOctopus = new Dumbo();

    @Property(type = PropertyType.CATEGORY, description = "Blue Footed Booby Spawn Configurations")
    public Booby blueFootedBooby = new Booby();

    @Property(type = PropertyType.CATEGORY, description = "Fennec Fox Spawn Configurations")
    public FennecFox fennecFox = new FennecFox();

    @Property(type = PropertyType.CATEGORY, description = "Kiwi Spawn Configurations")
    public Kiwi kiwi = new Kiwi();

    @Property(type = PropertyType.CATEGORY, description = "Manatee Spawn Configurations")
    public Manatee manatee = new Manatee();

    @Property(type = PropertyType.CATEGORY, description = "Red Deer Spawn Configurations")
    public RedDeer redDeer = new RedDeer();

    @Property(type = PropertyType.CATEGORY, description = "White Tailed Deer Spawn Configurations")
    public WhiteDeer whiteTailedDeer = new WhiteDeer();

    @Property(type = PropertyType.CATEGORY, description = "Seal Spawn Configurations")
    public Seal seal = new Seal();

    @Property(type = PropertyType.CATEGORY, description = "Flamingo Spawn Configurations")
    public Flamingo flamingo = new Flamingo();
}
