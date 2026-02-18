package xiao.battleroyale.api.event.register;

import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.CustomEvent;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.util.StringUtils;

public class RegisterManagerEvent extends CustomEvent {

    private final @Nullable CommandSourceStack source;
    private final StringUtils.ProtocolString protocolString;

    public RegisterManagerEvent(@Nullable CommandSourceStack source, String protocol) {
        this.source = source;
        this.protocolString = new StringUtils.ProtocolString(protocol);
    }

    public @Nullable CommandSourceStack getSource() {
        return source;
    }

    @Deprecated(forRemoval = false)
    public String getProtocol() {
        return protocolString.raw;
    }
    public StringUtils.ProtocolString getProtocolString() {
        return protocolString;
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.REGISTER_MANAGER_EVENT;
    }
}
