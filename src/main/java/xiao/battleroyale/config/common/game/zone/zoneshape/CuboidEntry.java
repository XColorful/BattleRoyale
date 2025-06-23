package xiao.battleroyale.config.common.game.zone.zoneshape;

import com.google.gson.JsonObject;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.game.zone.shape.ZoneShapeTag;
import xiao.battleroyale.common.game.zone.spatial.CuboidShape;

import javax.annotation.Nullable;

public class CuboidEntry extends AbstractSimpleEntry {

    public CuboidEntry(StartEntry startEntry, EndEntry endEntry, boolean badShape) {
        super(startEntry, endEntry, badShape);
    }

    @Override
    public String getType() {
        return ZoneShapeTag.CUBOID;
    }

    @Override
    public ZoneShapeType getZoneShapeType() {
        return ZoneShapeType.CUBOID;
    }

    @Override
    public ISpatialZone createSpatialZone() {
        return new CuboidShape(startEntry, endEntry, badShape);
    }

    @Nullable
    public static CuboidEntry fromJson(JsonObject jsonObject) {
        StartEntry startEntry = AbstractSimpleEntry.readStartEntry(jsonObject);
        if (startEntry == null) {
            BattleRoyale.LOGGER.info("Invalid startEntry for CuboidEntry, skipped");
            return null;
        }

        EndEntry endEntry = AbstractSimpleEntry.readEndEntry(jsonObject);
        if (endEntry == null) {
            BattleRoyale.LOGGER.info("Invalid startEntry or endEntry for CuboidEntry, skipped");
            return null;
        }

        boolean badShape = AbstractSimpleEntry.readBadShape(jsonObject);

        return new CuboidEntry(startEntry, endEntry, badShape);
    }
}
