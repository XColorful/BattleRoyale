package xiao.battleroyale.api.event.game.spawn;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;

public class GameLobbyTeleportFinishEvent extends AbstractGameStatsEvent {

    protected @NotNull final LivingEntity livingEntity;

    public GameLobbyTeleportFinishEvent(IGameManager gameManager, @NotNull LivingEntity livingEntity) {
        super(gameManager);
        this.livingEntity = livingEntity;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_LOBBY_TELEPORT_FINISH_EVENT;
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
}
