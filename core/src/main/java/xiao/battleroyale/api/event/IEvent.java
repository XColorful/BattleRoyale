package xiao.battleroyale.api.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public interface IEvent {

    EventType getType();

    default boolean isCancelable() {
        return true;
    }

    boolean isCanceled();

    void setCanceled(boolean cancel);

    default Object getEvent() {
        return this;
    }

    @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source);
    String getTextName();
    Component getDisplayName();
}
