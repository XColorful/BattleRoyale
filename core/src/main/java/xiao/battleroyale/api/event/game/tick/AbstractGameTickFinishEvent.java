package xiao.battleroyale.api.event.game.tick;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.minecraft.CommandLevel;

public abstract class AbstractGameTickFinishEvent extends AbstractGameStatsEvent {

    protected final int gameTickTime;

    public AbstractGameTickFinishEvent(IGameManager gameManager, int gameTickTime) {
        super(gameManager);
        this.gameTickTime = gameTickTime;
    }

    public int getGameTickTime() {
        return this.gameTickTime;
    }

    @Override
    public @NotNull CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                gameManager.getGlobalCenterOffset(),
                Vec2.ZERO,
                gameManager.getServerLevel(),
                CommandLevel.permission(4),
                this.getTextName(),
                this.getDisplayName(),
                getGameManager().getServerLevel().getServer(),
                null
        );
    }
}
