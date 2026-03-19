package xiao.battleroyale.api.event.game;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.CustomEvent;
import xiao.battleroyale.api.game.IGameManager;

public abstract class AbstractGameEvent extends CustomEvent {

    protected final IGameManager gameManager;

    public AbstractGameEvent(IGameManager gameManager) {
        this.gameManager = gameManager;
    }

    public IGameManager getGameManager() {
        return gameManager;
    }

    public int getGameTime() {
        return gameManager.getGameTime();
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    @Override
    public @NotNull CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                gameManager.getGlobalCenterOffset(),
                Vec2.ZERO,
                gameManager.getServerLevel(),
                4,
                this.getTextName(),
                this.getDisplayName(),
                gameManager.getServerLevel().getServer(),
                null
        );
    }
    public abstract String getTextName();
    public abstract Component getDisplayName();
}
