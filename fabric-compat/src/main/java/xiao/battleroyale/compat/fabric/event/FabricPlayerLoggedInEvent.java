package xiao.battleroyale.compat.fabric.event;

import net.minecraft.world.entity.player.Player;
import xiao.battleroyale.api.event.IPlayerLoggedInEvent;

public class FabricPlayerLoggedInEvent extends FabricEvent implements IPlayerLoggedInEvent {
    private final Player player;

    public FabricPlayerLoggedInEvent(Player player) {
        super(false);
        this.player = player;
    }

    @Override
    public Player getEntity() {
        return player;
    }
}