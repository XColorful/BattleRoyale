package xiao.battleroyale.api.event.game.game;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ILivingDeathEvent;
import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.util.GameUtils;

public class GamePlayerDownFinishEvent extends AbstractGameStatsEvent {

    protected @NotNull final GamePlayer gamePlayer;
    protected @Nullable final LivingEntity livingEntity;
    protected final ILivingDeathEvent livingDeathEvent;

    public GamePlayerDownFinishEvent(IGameManager gameManager, @NotNull GamePlayer gamePlayer, ILivingDeathEvent event) {
        super(gameManager);
        this.gamePlayer = gamePlayer;
        this.livingEntity = GameUtils.getLivingEntity(gameManager.getServerLevel(), gamePlayer.getPlayerUUID());
        this.livingDeathEvent = event;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_PLAYER_DOWN_FINISH_EVENT;
    }

    public @NotNull GamePlayer getGamePlayer() {
        return this.gamePlayer;
    }

    public @Nullable LivingEntity getLivingEntity() {
        return this.livingEntity;
    }

    public ILivingDeathEvent getLivingDeathEvent() {
        return this.livingDeathEvent;
    }

    @Override
    public @NotNull CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        if (livingEntity == null) return super.createCommandSourceStack(source);
        return super.createCommandSourceStack(source)
                .withPosition(livingEntity.position())
                .withEntity(livingEntity);
    }

    @Override public String getTextName() {
        return livingEntity != null ? livingEntity.getName().getString() : String.format("CBR %s GamePlayerDownFinishEvent", gamePlayer.getNameWithId());
    }
    @Override public Component getDisplayName() {
        return livingEntity != null ? livingEntity.getDisplayName() : Component.literal(getTextName());
    }
}
