package xiao.battleroyale.api.event;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import xiao.battleroyale.api.minecraft.TriResult;

public interface IItemEntityPickupEvent extends IEvent {

    Player getPlayer();

    ItemEntity getItemEntity();

    void setCanPickup(TriResult state);

    TriResult canPickup();
}