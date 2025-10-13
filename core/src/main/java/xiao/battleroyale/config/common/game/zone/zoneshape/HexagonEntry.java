package xiao.battleroyale.config.common.game.zone.zoneshape;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.common.game.zone.spatial.HexagonShape;

public class HexagonEntry extends AbstractSimpleEntry {

    public HexagonEntry(StartEntry startEntry, EndEntry endEntry, boolean badShape) {
        super(startEntry, endEntry, badShape);
    }
    @Override public @NotNull HexagonEntry copy() {
        return new HexagonEntry(startEntry.copy(), endEntry.copy(), badShape);
    }

    @Override
    public String getType() {
        return ZoneShapeTag.HEXAGON;
    }

    @Override
    public ZoneShapeType getZoneShapeType() {
        return ZoneShapeType.HEXAGON;
    }

    @Override
    public ISpatialZone createSpatialZone() {
        return new HexagonShape(startEntry.copy(), endEntry.copy(), badShape);
    }


    @Nullable
    public static HexagonEntry fromJson(JsonObject jsonObject) {
        StartEntry startEntry = AbstractSimpleEntry.readStartEntry(jsonObject);
        if (startEntry == null) {
            BattleRoyale.LOGGER.info("Invalid startEntry for HexagonEntry, skipped");
            return null;
        }

        EndEntry endEntry = AbstractSimpleEntry.readEndEntry(jsonObject);
        if (endEntry == null) {
            BattleRoyale.LOGGER.info("Invalid startEntry or endEntry for HexagonEntry, skipped");
            return null;
        }

        boolean badShape = AbstractSimpleEntry.readBadShape(jsonObject);
        
        return new HexagonEntry(startEntry, endEntry, badShape);
    }
}
