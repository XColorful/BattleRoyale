package xiao.battleroyale.api.event;

import net.minecraft.world.entity.player.Player;

public interface IPlayerLoggedOutEvent extends IEvent {

    Player getEntity();
}
