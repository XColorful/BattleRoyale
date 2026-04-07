package xiao.battleroyale.api.event;

import net.minecraft.world.entity.player.Player;

public interface IPlayerRespawnEvent extends IEvent {

    Player getEntity();

    boolean isEndConquered();
}
