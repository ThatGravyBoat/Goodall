package tech.thatgravyboat.goodall.common.entity.goals;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import tech.thatgravyboat.goodall.common.entity.DumboEntity;

import java.util.EnumSet;

public class DumboShyGoal extends Goal {

    private int cooldown = 60;
    private PlayerEntity entity;

    private final DumboEntity dumbo;

    public DumboShyGoal(DumboEntity dumbo) {
        this.dumbo = dumbo;
        setControls(EnumSet.of(Control.MOVE, Control.JUMP, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }
        return findNearestPlayer();
    }

    @Override
    public boolean shouldContinue() {
        return this.entity.squaredDistanceTo(this.dumbo) < 16;
    }

    @Override
    public void start() {
        this.dumbo.setShy(true);
    }

    @Override
    public void stop() {
        this.entity = null;
        this.cooldown = 60;
        this.dumbo.setShy(false);
    }

    private boolean findNearestPlayer() {
        this.entity = this.dumbo.world.getClosestPlayer(this.dumbo, 4);
        return this.entity != null;
    }


}
