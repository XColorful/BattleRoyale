package xiao.battleroyale.api.event.custom.zone;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEvent;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ICustomEvent;
import xiao.battleroyale.api.event.ICustomEventHandler;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.minecraft.CommandLevel;
import xiao.battleroyale.common.game.zone.ZoneManager;
import xiao.battleroyale.event.EventDispatcher;

public class DetermineZoneEvent extends CustomEvent {

    protected final ZoneManager.ZoneContext zoneContext;
    protected final ISpatialZone spatialZone;
    protected int recalculateCount = 0;

    public DetermineZoneEvent(@NotNull ZoneManager.ZoneContext zoneContext, @NotNull ISpatialZone spatialZone) {
        this.zoneContext = zoneContext;
        this.spatialZone = spatialZone;
    }

    public @NotNull ZoneManager.ZoneContext getZoneContext() {
        return zoneContext;
    }

    public @NotNull ServerLevel getServerLevel() {
        return zoneContext.serverLevel;
    }

    @ApiStatus.Internal public @NotNull ISpatialZone getSpatialZone() {
        return spatialZone;
    }
    public @Nullable Vec3 getStartCenterPos() {
        return spatialZone.getStartCenterPos();
    }
    public @Nullable Vec3 getStartDimension() {
        return spatialZone.getStartDimension();
    }
    public double getStartRotationDegree() {
        return spatialZone.getStartRotateDegree();
    }
    public @Nullable Vec3 getEndCenterPos() {
        return spatialZone.getEndCenterPos();
    }
    public @Nullable Vec3 getEndDimension() {
        return spatialZone.getEndDimension();
    }
    public double getEndRotationDegree() {
        return spatialZone.getEndRotateDegree();
    }

    public int getRecalculateCount() {
        return recalculateCount;
    }

    public void calculateShapeAgain() {
        recalculateCount++;
        spatialZone.calculateShape(zoneContext);
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        Vec3 center = spatialZone.getStartCenterPos();
        @NotNull ServerLevel serverLevel = this.getServerLevel();
        return new CommandSourceStack(
                source != null ? source : CommandSource.NULL,
                center != null ? center : Vec3.ZERO,
                Vec2.ZERO,
                serverLevel,
                CommandLevel.permission(4),
                this.getTextName(),
                this.getDisplayName(),
                serverLevel.getServer(),
                null
        );
    }

    @Override public String getTextName() {
        return "CBR Determine zone event";
    }

    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }

    private static final EventDispatcher<ICustomEventHandler, ICustomEvent, CustomEventType> _EVENT_DISPATCHER = BattleRoyale.getEventPoster().getEventDispatcher(DetermineZoneEvent.class);
    @Override public @NotNull EventDispatcher<ICustomEventHandler, ICustomEvent, CustomEventType> getEventDispatcher() {
        return _EVENT_DISPATCHER;
    }
}
