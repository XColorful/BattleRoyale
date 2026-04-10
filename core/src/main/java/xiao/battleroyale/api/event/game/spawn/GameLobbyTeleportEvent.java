package xiao.battleroyale.api.event.game.spawn;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ICustomEvent;
import xiao.battleroyale.api.event.ICustomEventHandler;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.event.EventDispatcher;

public class GameLobbyTeleportEvent extends AbstractGameEvent {

    protected @NotNull final LivingEntity livingEntity;

    public GameLobbyTeleportEvent(IGameManager gameManager, @NotNull LivingEntity livingEntity) {
        super(gameManager);
        this.livingEntity = livingEntity;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_LOBBY_TELEPORT_EVENT;
    }

    public @NotNull LivingEntity getLivingEntity() {
        return this.livingEntity;
    }
    @Deprecated
    public @Nullable ServerPlayer getPlayer() {
        return this.livingEntity instanceof ServerPlayer player ? player : null;
    }

    @Override
    public @NotNull CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        return super.createCommandSourceStack(source)
                .withRotation(livingEntity.getRotationVector())
                .withEntity(livingEntity);
    }

    @Override public String getTextName() {
        return livingEntity.getName().getString();
    }
    @Override public Component getDisplayName() {
        return livingEntity.getDisplayName();
    }

    private static final EventDispatcher<ICustomEventHandler, ICustomEvent, CustomEventType> _EVENT_DISPATCHER = BattleRoyale.getEventPoster().getEventDispatcher(GameLobbyTeleportEvent.class);
    @Override public @NotNull EventDispatcher<ICustomEventHandler, ICustomEvent, CustomEventType> getEventDispatcher() {
        return _EVENT_DISPATCHER;
    }
}
