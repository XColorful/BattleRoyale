package xiao.battleroyale.api.event.register;

import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.CustomEvent;
import xiao.battleroyale.api.event.CustomEventType;

public class RegisterManagerEvent extends CustomEvent {

    private final @Nullable CommandSourceStack source;
    private final String protocol;

    public RegisterManagerEvent(@Nullable CommandSourceStack source, String protocol) {
        this.source = source;
        this.protocol = protocol;
    }

    public @Nullable CommandSourceStack getSource() {
        return source;
    }

    public String getProtocol() {
        return protocol;
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.REGISTER_MANAGER_EVENT;
    }
}
