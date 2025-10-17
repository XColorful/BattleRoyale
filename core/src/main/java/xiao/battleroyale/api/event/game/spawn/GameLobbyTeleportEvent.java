package xiao.battleroyale.api.event.game.spawn;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;

public class GameLobbyTeleportEvent extends AbstractGameEvent {

    protected @NotNull
    final LivingEntity livingEntity;

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
}
