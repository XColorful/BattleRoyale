package xiao.battleroyale.compat.forge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IItemTossEvent;
import xiao.battleroyale.api.minecraft.CommandLevel;

public class ForgeItemTossEvent extends ForgeEvent implements IItemTossEvent {

    protected ItemTossEvent itemTossEvent;

    public ForgeItemTossEvent(Event event) {
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