package xiao.battleroyale.compat.neoforge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.player.EntityItemPickupEvent;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IItemEntityPickupEvent;
import xiao.battleroyale.api.minecraft.CommandLevel;
import xiao.battleroyale.api.minecraft.TriResult;
import xiao.battleroyale.compat.neoforge.minecraft.TriResultHelper;

public class NeoItemEntityPickupEvent extends NeoEvent implements IItemEntityPickupEvent {

    protected EntityItemPickupEvent itemEntityPickupEvent;

    public NeoItemEntityPickupEvent(Event event) {
        super(event);
        if (event instanceof EntityItemPickupEvent eventIn) {
            this.itemEntityPickupEvent = eventIn;
        } else {
            throw new RuntimeException("Expected EntityItemPickupEvent but received: " + event.getClass().getName());
        }
    }

    @Override
    public EventType getType() {
        return EventType.ITEM_ENTITY_PICKUP_EVENT;
    }

    @Override public boolean isCanceled() {
        return super.isCanceled() || this.canPickup().isDenied();
    }

    @Override public void setCanceled(boolean cancel) {
        super.setCanceled(cancel);
        if (cancel) {
            this.setCanPickup(TriResult.DENY);
        }
    }

    @Override
    public Player getPlayer() {
        return itemEntityPickupEvent.getEntity();
    }

    @Override
    public ItemEntity getItemEntity() {
        return itemEntityPickupEvent.getItem();
    }

    @Override
    public void setCanPickup(TriResult state) {
        itemEntityPickupEvent.setResult(TriResultHelper.convert(state));
    }

    @Override
    public TriResult canPickup() {
        return TriResultHelper.convert(itemEntityPickupEvent.getResult());
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        Player player = this.getPlayer();
        Level level = player.level();
        if (level != null && level.isClientSide()) return null;
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                player.position(),
                player.getRotationVector(),
                (ServerLevel) level,
                CommandLevel.permission(4),
                this.getTextName(),
                this.getDisplayName(),
                level.getServer(),
                player
        );
    }

    @Override
    public String getTextName() {
        return this.getPlayer().getName().getString();
    }

    @Override
    public Component getDisplayName() {
        return this.getPlayer().getDisplayName();
    }
}