package xiao.battleroyale.api.event.register;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
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

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        return this.source != null ? this.source : new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                Vec3.ZERO,
                Vec2.ZERO,
                BattleRoyale.getGameManager().getServerLevel(),
                4,
                this.getTextName(),
                this.getDisplayName(),
                BattleRoyale.getMinecraftServer(),
                null
        );
    }
    public String getTextName() {
        return "CBR RegisterManagerEvent";
    }
    public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}
