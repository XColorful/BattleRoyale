package xiao.battleroyale.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public interface IPlayerOpenContainerEvent extends IEvent {

    Player getPlayer();

    AbstractContainerMenu getContainer();
}
