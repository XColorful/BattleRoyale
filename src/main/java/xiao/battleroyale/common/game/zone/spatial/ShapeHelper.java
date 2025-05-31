package xiao.battleroyale.common.game.zone.spatial;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.common.game.zone.ZoneManager;

import java.util.function.Supplier;

public class ShapeHelper {

    @Nullable
    public static Vec3 getPreviousEndCenterById(int id) {
        IGameZone gameZone = ZoneManager.get().getZoneById(id);
        if (gameZone == null) {
            BattleRoyale.LOGGER.warn("Failed to get previous gameZone end center by id: {}", id);
            return null;
        }
        return gameZone.getEndCenterPos();
    }

    @Nullable
    public static Vec3 getPreviousEndDimensionById(int id) {
        IGameZone gameZone = ZoneManager.get().getZoneById(id);
        if (gameZone == null) {
            BattleRoyale.LOGGER.warn("Failed to get previous gameZone end dimension by id: {}", id);
            return null;
        }
        return gameZone.getEndDimension();
    }

    @Nullable
    public static Vec3 getPreviousStartCenterById(int id) {
        IGameZone gameZone = ZoneManager.get().getZoneById(id);
        if (gameZone == null) {
            BattleRoyale.LOGGER.warn("Failed to get previous gameZone start center by id: {}", id);
            return null;
        }
        return gameZone.getStartCenterPos();
    }

    @Nullable
    public static Vec3 getPreviousStartDimensionById(int id) {
        IGameZone gameZone = ZoneManager.get().getZoneById(id);
        if (gameZone == null) {
            BattleRoyale.LOGGER.warn("Failed to get previous gameZone start dimension by id: {}", id);
            return null;
        }
        return gameZone.getStartDimension();
    }
}
