package xiao.battleroyale.config.common.game.zone.zoneshape;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.game.zone.shape.IZoneShapeEntry;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.common.game.zone.spatial.SquareShape;

public class SquareEntry extends AbstractSimpleEntry {

    public SquareEntry(StartEntry startEntry, EndEntry endEntry) {
        super(startEntry, endEntry);
    }

    @Override
    public String getType() {
        return ZoneShapeTag.SQUARE;
    }

    @Override
    public ZoneShapeType getZoneShapeType() {
        return ZoneShapeType.SQUARE;
    }

    @Override
    public ISpatialZone createSpatialZone() {
        return new SquareShape(startEntry, endEntry);
    }

    @Nullable
    public static SquareEntry fromJson(JsonObject jsonObject) {
        StartEntry startEntry = AbstractSimpleEntry.readStartEntry(jsonObject);
        if (startEntry == null) {
            BattleRoyale.LOGGER.info("Invalid startEntry for HexagonEntry, skipped");
            return null;
        }

        EndEntry endEntry = AbstractSimpleEntry.readEndEntry(jsonObject);
        if (endEntry == null) {
            BattleRoyale.LOGGER.info("Invalid endEntry for HexagonEntry, skipped");
            return null;
        }

        return new SquareEntry(startEntry, endEntry);
    }
}