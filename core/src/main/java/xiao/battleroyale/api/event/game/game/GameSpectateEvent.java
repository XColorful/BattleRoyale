package xiao.battleroyale.api.event.game.game;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;

public class GameSpectateEvent extends AbstractGameEvent {

    protected @NotNull final ServerPlayer player;
    protected final GameSpectateResult spectateResult;

    public GameSpectateEvent(IGameManager gameManager, @NotNull ServerPlayer player, GameSpectateResult result) {
        super(gameManager);
        this.player = player;
        this.spectateResult = result;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_SPECTATE_EVENT;
    }

    public @NotNull ServerPlayer getPlayer() {
        return this.player;
    }

    public GameSpectateResult getSpectateResult() {
        return spectateResult;
    }

    @Override
    public @NotNull CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        return super.createCommandSourceStack(source)
                .withPosition(player.position())
                .withEntity(player);
    }

    @Override public String getTextName() {
        return player.getName().getString();
    }
    @Override public Component getDisplayName() {
        return player.getDisplayName();
    }
}
