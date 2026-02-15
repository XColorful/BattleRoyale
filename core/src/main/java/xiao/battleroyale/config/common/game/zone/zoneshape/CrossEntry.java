package xiao.battleroyale.config.common.game.zone.zoneshape;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.config.common.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.common.game.zone.spatial.CrossShape;

public class CrossEntry extends AbstractSimpleEntry {

    public CrossEntry(StartEntry startEntry, EndEntry endEntry, boolean badShape) {
        super(startEntry, endEntry, badShape);
    }

    @Override
    public @NotNull CrossEntry copy() {
        return new CrossEntry(startEntry.copy(), endEntry.copy(), badShape);
    }

    @Override
    public String getType() {
        return ZoneShapeTag.CROSS;
    }

    @Override
    public ZoneShapeType getZoneShapeType() {
        return ZoneShapeType.CROSS;
    }

    @Override
    public ISpatialZone createSpatialZone() {
        return new CrossShape(startEntry.copy(), endEntry.copy(), badShape);
    }

    @Nullable
    public static CrossEntry fromJson(JsonObject jsonObject) {
        StartEntry startEntry = AbstractSimpleEntry.readStartEntry(jsonObject);
        if (startEntry == null) {
            BattleRoyale.LOGGER.info("Invalid startEntry for CrossEntry, skipped");
            return null;
        }

        EndEntry endEntry = AbstractSimpleEntry.readEndEntry(jsonObject);
        if (endEntry == null) {
            BattleRoyale.LOGGER.info("Invalid endEntry for CrossEntry, skipped");
            return null;
        }

        boolean badShape = AbstractSimpleEntry.readBadShape(jsonObject);

        return new CrossEntry(startEntry, endEntry, badShape);
    }
}