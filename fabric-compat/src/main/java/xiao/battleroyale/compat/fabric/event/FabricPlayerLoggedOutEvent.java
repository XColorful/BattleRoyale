package xiao.battleroyale.compat.fabric.event;

import net.minecraft.world.entity.player.Player;
import xiao.battleroyale.api.event.IPlayerLoggedOutEvent;

public class FabricPlayerLoggedOutEvent extends FabricEvent implements IPlayerLoggedOutEvent {
    private final Player player;

    public FabricPlayerLoggedOutEvent(Player player) {
        super(false);
        this.player = player;
    }

    @Override
    public Player getEntity() {
        return player;
    }
}