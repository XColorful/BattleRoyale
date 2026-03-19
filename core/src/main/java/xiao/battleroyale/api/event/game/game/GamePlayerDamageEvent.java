package xiao.battleroyale.api.event.game.game;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ILivingDamageEvent;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.util.GameUtils;

public class GamePlayerDamageEvent extends AbstractGameEvent {

    protected @NotNull final GamePlayer gamePlayer;
    protected @Nullable final LivingEntity livingEntity;
    protected @Nullable final ILivingDamageEvent livingDamageEvent;

    public GamePlayerDamageEvent(IGameManager gameManager, @NotNull final GamePlayer gamePlayer, @Nullable ILivingDamageEvent event) {
        super(gameManager);
        this.gamePlayer = gamePlayer;
        this.livingEntity = GameUtils.getLivingEntity(gameManager.getServerLevel(), gamePlayer.getPlayerUUID());
        this.livingDamageEvent = event;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_PLAYER_DAMAGE_EVENT;
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
                .withEntity(livingEntity);
    }

    @Override public String getTextName() {
        return livingEntity != null ? livingEntity.getName().getString() : String.format("CBR %s GamePlayerDamageEvent", gamePlayer.getNameWithId());
    }
    @Override public Component getDisplayName() {
        return livingEntity != null ? livingEntity.getDisplayName() : Component.literal(getTextName());
    }
}
