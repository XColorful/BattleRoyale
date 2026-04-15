package xiao.battleroyale.compat.forge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IItemTooltipEvent;

import java.util.List;

public class ForgeItemTooltipEvent extends ForgeEvent implements IItemTooltipEvent {

    protected ItemTooltipEvent itemTooltipEvent;

    public ForgeItemTooltipEvent(Event event) {
        super(event);
        if (event instanceof ItemTooltipEvent eventIn) {
            this.itemTooltipEvent = eventIn;
        } else {
            throw new RuntimeException("Expected ItemTooltipEvent but received: " + event.getClass().getName());
        }
    }

    @Override
    public EventType getType() {
        return EventType.ITEM_TOOLTIP_EVENT;
    }

    @Override
    public @Nullable Player getPlayer() {
        return itemTooltipEvent.getEntity();
    }

    @Override
    public @NotNull ItemStack getItemStack() {
        return itemTooltipEvent.getItemStack();
    }

    @Override
    public @NotNull List<Component> getToolTip() {
        return itemTooltipEvent.getToolTip();
    }

    @Override
    public @NotNull TooltipFlag getFlags() {
        return itemTooltipEvent.getFlags();
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        return null;
    }

    @Override
    public String getTextName() {
        return "ForgeItemTooltipEvent";
    }

    @Override
    public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}