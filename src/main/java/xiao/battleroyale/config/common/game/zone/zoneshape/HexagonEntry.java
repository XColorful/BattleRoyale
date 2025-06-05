package xiao.battleroyale.config.common.game.zone.zoneshape;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.common.game.zone.spatial.HexagonShape;

public class HexagonEntry extends AbstractSimpleEntry {

    public HexagonEntry(StartEntry startEntry, EndEntry endEntry) {
        super(startEntry, endEntry);
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
        return new HexagonShape(startEntry, endEntry);
    }


    @Nullable
    public static HexagonEntry fromJson(JsonObject jsonObject) {
        JsonObject startEntryObject = jsonObject.has(ZoneShapeTag.START) ? jsonObject.getAsJsonObject(ZoneShapeTag.START) : null;
        JsonObject endEntryObject = jsonObject.has(ZoneShapeTag.END) ? jsonObject.getAsJsonObject(ZoneShapeTag.END) : null;
        if (startEntryObject == null || endEntryObject == null) {
            BattleRoyale.LOGGER.info("HexagonEntry missing start or end member, skipped");
            return null;
        }
        StartEntry startEntry = StartEntry.fromJson(startEntryObject);
        EndEntry endEntry = EndEntry.fromJson(endEntryObject);
        if (startEntry == null || endEntry == null) {
            BattleRoyale.LOGGER.info("Invalid startEntry or endEntry for HexagonEntry, skipped");
            return null;
        }
        return new HexagonEntry(startEntry, endEntry);
    }
}
