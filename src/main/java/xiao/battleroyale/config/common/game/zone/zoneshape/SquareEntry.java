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
        JsonObject startEntryObject = jsonObject.has(ZoneShapeTag.START) ? jsonObject.getAsJsonObject(ZoneShapeTag.START) : null;
        JsonObject endEntryObject = jsonObject.has(ZoneShapeTag.END) ? jsonObject.getAsJsonObject(ZoneShapeTag.END) : null;
        if (startEntryObject == null || endEntryObject == null) {
            BattleRoyale.LOGGER.info("SquareEntry missing start or end member, skipped");
            return null;
        }
        StartEntry startEntry = StartEntry.fromJson(startEntryObject);
        EndEntry endEntry = EndEntry.fromJson(endEntryObject);
        if (startEntry == null || endEntry == null) {
            BattleRoyale.LOGGER.info("Invalid startEntry or endEntry for SquareEntry, skipped");
            return null;
        }
        return new SquareEntry(startEntry, endEntry);
    }
}