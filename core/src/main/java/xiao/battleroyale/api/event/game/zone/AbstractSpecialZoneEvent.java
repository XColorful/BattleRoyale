package xiao.battleroyale.api.event.game.zone;

import com.google.gson.JsonObject;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.common.game.zone.ZoneManager;

public abstract class AbstractSpecialZoneEvent extends AbstractGameEvent {

    protected @NotNull final ZoneManager.ZoneTickContext zoneTickContext;
    protected final String protocol;
    protected @NotNull final JsonObject jsonTag;

    public AbstractSpecialZoneEvent(IGameManager gameManager, @NotNull ZoneManager.ZoneTickContext zoneTickContext,
                                    String protocol, @NotNull JsonObject jsonTag) {
        super(gameManager);
        this.zoneTickContext = zoneTickContext;
        this.protocol = protocol;
        this.jsonTag = jsonTag;
    }

    public @NotNull ZoneManager.ZoneTickContext getZoneTickContext() {
        return this.zoneTickContext;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public @NotNull JsonObject getJsonTag() {
        return this.jsonTag;
    }

    @Deprecated
    public @NotNull JsonObject getTag() {
        return getJsonTag();
    }

    @Override
    public @NotNull CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        @Nullable IGameZone gameZone = zoneTickContext.zoneManager.getGameZone(zoneTickContext.zoneId);
        int gameTime = BattleRoyale.getGameManager().getGameTime();
        @Nullable Vec3 zoneCenter = gameZone != null ? gameZone.getCenterPos(gameZone.getShapeProgress(gameTime)) : null;
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                zoneCenter != null ? zoneCenter : Vec3.ZERO,
                Vec2.ZERO,
                zoneTickContext.serverLevel,
                4,
                this.getTextName(),
                this.getDisplayName(),
                zoneTickContext.serverLevel.getServer(),
                null
        );
    }
}
