package xiao.battleroyale.api.event;

import net.minecraft.world.entity.player.Player;

public interface IPlayerLoggedInEvent extends IEvent {

    Player getEntity();
}
