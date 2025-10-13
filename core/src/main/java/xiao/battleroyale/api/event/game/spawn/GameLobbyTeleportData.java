package xiao.battleroyale.api.event.game.spawn;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameEventData;
import xiao.battleroyale.api.game.IGameManager;

public class GameLobbyTeleportData extends AbstractGameEventData {

    public @NotNull final LivingEntity livingEntity;

    public GameLobbyTeleportData(IGameManager gameManager, @NotNull LivingEntity player) {
        super(gameManager);
        this.livingEntity = player;
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.GAME_LOBBY_TELEPORT_EVENT;
    }
}
