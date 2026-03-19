package xiao.battleroyale.api.event.game.zone;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;

public abstract class AbstractZoneEvent extends AbstractGameStatsEvent {

    protected @NotNull final IGameZone gameZone;

    public AbstractZoneEvent(IGameManager gameManager, @NotNull IGameZone gameZone) {
        super(gameManager);
        this.gameZone = gameZone;
    }

    public @NotNull IGameZone getGameZone() {
        return this.gameZone;
    }

    @Override
    public @NotNull CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        Vec3 zoneCenter = gameZone.getCenterPos(gameZone.getShapeProgress(gameManager.getGameTime()));
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                zoneCenter != null ? zoneCenter : Vec3.ZERO,
                Vec2.ZERO,
                gameManager.getServerLevel(),
                4,
                this.getTextName(),
                this.getDisplayName(),
                gameManager.getServerLevel().getServer(),
                null
        );
    }
}
