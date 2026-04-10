package xiao.battleroyale.api.event.server.utility;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ICustomEvent;
import xiao.battleroyale.api.event.ICustomEventHandler;
import xiao.battleroyale.api.minecraft.CommandLevel;
import xiao.battleroyale.api.server.IServerManager;
import xiao.battleroyale.event.EventDispatcher;

public class SurvivalLobbyTeleportEvent extends AbstractUtilityEvent {

    protected @NotNull final LivingEntity livingEntity;

    public SurvivalLobbyTeleportEvent(IServerManager serverManager, @NotNull LivingEntity livingEntity) {
        super(serverManager);
        this.livingEntity = livingEntity;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.SURVIVAL_LOBBY_TELEPORT_EVENT;
    }

    public @NotNull LivingEntity getLivingEntity() {
        return this.livingEntity;
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        Level level = livingEntity.level();
        if (level != null && level.isClientSide()) return null;
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                livingEntity.position(),
                livingEntity.getRotationVector(),
                (ServerLevel) level,
                CommandLevel.permission(4),
                this.getTextName(),
                this.getDisplayName(),
                level.getServer(),
                livingEntity
        );
    }

    @Override public String getTextName() {
        return livingEntity.getName().getString();
    }

    @Override public Component getDisplayName() {
        return livingEntity.getDisplayName();
    }

    private static final EventDispatcher<ICustomEventHandler, ICustomEvent, CustomEventType> _EVENT_DISPATCHER = BattleRoyale.getEventPoster().getEventDispatcher(SurvivalLobbyTeleportEvent.class);
    @Override public @NotNull EventDispatcher<ICustomEventHandler, ICustomEvent, CustomEventType> getEventDispatcher() {
        return _EVENT_DISPATCHER;
    }
}
