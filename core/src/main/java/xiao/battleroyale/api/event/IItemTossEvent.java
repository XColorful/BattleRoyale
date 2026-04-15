package xiao.battleroyale.api.event;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

public interface IItemTossEvent extends IEvent {

    Player getPlayer();

    ItemEntity getItemEntity();
}
