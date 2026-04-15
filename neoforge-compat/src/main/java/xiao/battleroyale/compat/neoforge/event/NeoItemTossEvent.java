package xiao.battleroyale.compat.neoforge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IItemTossEvent;
import xiao.battleroyale.api.minecraft.CommandLevel;

public class NeoItemTossEvent extends NeoEvent implements IItemTossEvent {

    protected ItemTossEvent itemTossEvent;

    public NeoItemTossEvent(Event event) {
        super(event);
        if (event instanceof ItemTossEvent eventIn) {
            this.itemTossEvent = eventIn;
        } else {
            throw new RuntimeException("Expected ItemTossEvent but received: " + event.getClass().getName());
        }
    }

    @Override
    public EventType getType() {
        return EventType.ITEM_TOSS_EVENT;
    }

    @Override
    public Player getPlayer() {
        return itemTossEvent.getPlayer();
    }

    @Override
    public ItemEntity getItemEntity() {
        return itemTossEvent.getEntity();
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