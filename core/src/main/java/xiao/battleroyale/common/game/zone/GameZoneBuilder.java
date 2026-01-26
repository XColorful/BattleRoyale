package xiao.battleroyale.common.game.zone;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.common.game.zone.func.IZoneFuncEntry;
import xiao.battleroyale.api.game.zone.gamezone.IAdditionalZone;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.api.config.common.game.zone.shape.IZoneShapeEntry;
import xiao.battleroyale.api.config.common.game.zone.special.IZoneSpecialEntry;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager.ZoneConfig;

import javax.annotation.Nullable;

public class GameZoneBuilder {
    private int zoneId;
    private String zoneName;
    private String zoneColor;
    private int preZoneDelayId;
    private int zoneDelay;
    private int zoneTime;
    private IZoneFuncEntry funcEntry;
    private IZoneShapeEntry shapeEntry;
    private @Nullable IZoneSpecialEntry specialEntry;

    public GameZoneBuilder withZoneConfig(ZoneConfig zoneConfig) {
        this.zoneId = zoneConfig.getZoneId();
        this.zoneName = zoneConfig.getZoneName();
        this.zoneColor = zoneConfig.getZoneColor();
        this.preZoneDelayId = zoneConfig.getPreZoneDelayId();
        this.zoneDelay = zoneConfig.getZoneDelay();
        this.zoneTime = zoneConfig.getZoneTime();
        this.funcEntry = zoneConfig.getZoneFuncEntry();
        this.shapeEntry = zoneConfig.getZoneShapeEntry();
        this.specialEntry = zoneConfig.getZoneSpecialEntry();
        return this;
    }

    @Nullable
    public IGameZone build() {
        if (funcEntry == null || shapeEntry == null) {
            BattleRoyale.LOGGER.warn("Missing funcEntry or shapeEntry for creating GameZone");
            return null;
        }
        ITickableZone tickableZone = funcEntry.createTickableZone();
        ISpatialZone spatialZone = shapeEntry.createSpatialZone();
        IAdditionalZone additionalZone = specialEntry != null ? specialEntry.createAdditionalZone() : null;
        return new GameZone(zoneId, zoneName, zoneColor, preZoneDelayId, zoneDelay, zoneTime, tickableZone, spatialZone, additionalZone);
    }
}