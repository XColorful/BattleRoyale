package xiao.battleroyale.config.common.game.zone.zoneshape;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.config.common.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.common.game.zone.spatial.RingShape;

public class RingEntry extends AbstractSimpleEntry {

    public RingEntry(StartEntry startEntry, EndEntry endEntry, boolean badShape) {
        super(startEntry, endEntry, badShape);
    }

    @Override
    public @NotNull RingEntry copy() {
        return new RingEntry(startEntry.copy(), endEntry.copy(), badShape);
    }

    @Override
    public String getType() {
        return ZoneShapeTag.RING;
    }

    @Override
    public ZoneShapeType getZoneShapeType() {
        return ZoneShapeType.RING;
    }

    @Override
    public ISpatialZone createSpatialZone() {
        return new RingShape(startEntry.copy(), endEntry.copy(), badShape);
    }

    @Nullable
    public static RingEntry fromJson(JsonObject jsonObject) {
        StartEntry startEntry = AbstractSimpleEntry.readStartEntry(jsonObject);
        if (startEntry == null) {
            BattleRoyale.LOGGER.info("Invalid startEntry for RingEntry, skipped");
            return null;
        }

        EndEntry endEntry = AbstractSimpleEntry.readEndEntry(jsonObject);
        if (endEntry == null) {
            BattleRoyale.LOGGER.info("Invalid endEntry for RingEntry, skipped");
            return null;
        }

        boolean badShape = AbstractSimpleEntry.readBadShape(jsonObject);

        return new RingEntry(startEntry, endEntry, badShape);
    }
}