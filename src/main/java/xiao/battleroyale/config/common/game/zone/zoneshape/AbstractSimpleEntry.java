package xiao.battleroyale.config.common.game.zone.zoneshape;

import com.google.gson.JsonObject;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.shape.IZoneShapeEntry;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;

import javax.annotation.Nullable;

public abstract class AbstractSimpleEntry implements IZoneShapeEntry {

    protected final StartEntry startEntry;
    protected final EndEntry endEntry;

    public AbstractSimpleEntry(StartEntry startEntry, EndEntry endEntry) {
        this.startEntry = startEntry;
        this.endEntry = endEntry;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ZoneShapeTag.TYPE_NAME, getType());
        jsonObject.add(ZoneShapeTag.START, startEntry.toJson());
        jsonObject.add(ZoneShapeTag.END, endEntry.toJson());
        return jsonObject;
    }

    public static StartEntry readStartEntry(JsonObject jsonObject) {
        JsonObject startEntryObject = jsonObject.has(ZoneShapeTag.START) ? jsonObject.getAsJsonObject(ZoneShapeTag.START) : null;
        if (startEntryObject == null) {
            BattleRoyale.LOGGER.info("Shape entry missing start or end member, skipped");
            return null;
        }
        StartEntry startEntry = StartEntry.fromJson(startEntryObject);
        if (startEntry == null) {
            BattleRoyale.LOGGER.info("Invalid startEntry, skipped");
            return null;
        }
        return startEntry;
    }

    public static EndEntry readEndEntry(JsonObject jsonObject) {
        JsonObject endEntryObject = jsonObject.has(ZoneShapeTag.END) ? jsonObject.getAsJsonObject(ZoneShapeTag.END) : null;
        if (endEntryObject == null) {
            return null;
        }
        EndEntry endEntry = EndEntry.fromJson(endEntryObject);
        if (endEntry == null) {
            BattleRoyale.LOGGER.info("Invalid endEntry for HexagonEntry, skipped");
            return null;
        }
        return endEntry;
    }
}
