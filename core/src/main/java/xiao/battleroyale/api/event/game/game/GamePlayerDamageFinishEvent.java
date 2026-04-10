package xiao.battleroyale.api.event.game.game;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ICustomEvent;
import xiao.battleroyale.api.event.ICustomEventHandler;
import xiao.battleroyale.api.event.ILivingDamageEvent;
import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.event.EventDispatcher;
import xiao.battleroyale.util.GameUtils;

public class GamePlayerDamageFinishEvent extends AbstractGameStatsEvent {

    protected @NotNull final GamePlayer gamePlayer;
    protected @Nullable final LivingEntity livingEntity;
    protected @Nullable final ILivingDamageEvent livingDamageEvent;

    public GamePlayerDamageFinishEvent(IGameManager gameManager, @NotNull GamePlayer gamePlayer, @NotNull ILivingDamageEvent livingDamageEvent) {
        super(gameManager);
        this.gamePlayer = gamePlayer;
        this.livingEntity = GameUtils.getLivingEntity(gameManager.getServerLevel(), gamePlayer.getPlayerUUID());
        this.livingDamageEvent = livingDamageEvent;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_PLAYER_DAMAGE_FINISH_EVENT;
    }

    public @NotNull GamePlayer getGamePlayer() {
        return this.gamePlayer;
    }

    public @Nullable LivingEntity getLivingEntity() {
        return this.livingEntity;
    }

    public @Nullable ILivingDamageEvent getLivingDamageEvent() {
        return this.livingDamageEvent;
    }

    @Override
    public @NotNull CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        if (livingEntity == null) return super.createCommandSourceStack(source);
        return super.createCommandSourceStack(source)
                .withPosition(livingEntity.position())
                .withRotation(livingEntity.getRotationVector())
                .withEntity(livingEntity);
    }

    @Override public String getTextName() {
        return livingEntity != null ? livingEntity.getName().getString() : String.format("CBR %s GamePlayerDamageFinishEvent", gamePlayer.getNameWithId());
    }
    @Override public Component getDisplayName() {
        return livingEntity != null ? livingEntity.getDisplayName() : Component.literal(getTextName());
    }

    private static final EventDispatcher<ICustomEventHandler, ICustomEvent, CustomEventType> _EVENT_DISPATCHER = BattleRoyale.getEventPoster().getEventDispatcher(GamePlayerDamageFinishEvent.class);
    @Override public @NotNull EventDispatcher<ICustomEventHandler, ICustomEvent, CustomEventType> getEventDispatcher() {
        return _EVENT_DISPATCHER;
    }
}
