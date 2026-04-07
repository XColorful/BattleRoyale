package xiao.battleroyale.api.event.server.utility;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.server.IServerManager;

public class SurvivalLobbyTeleportFinishEvent extends SurvivalLobbyTeleportEvent {

    public SurvivalLobbyTeleportFinishEvent(IServerManager serverManager, @NotNull LivingEntity livingEntity) {
        super(serverManager, livingEntity);
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.SURVIVAL_LOBBY_TELEPORT_FINISH_EVENT;
    }
}
