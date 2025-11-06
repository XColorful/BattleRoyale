package xiao.battleroyale.common.game.zone;

import org.jetbrains.annotations.ApiStatus;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;

import java.util.function.Supplier;

public class ZoneUtils {

    @ApiStatus.Internal
    public static boolean hasEnoughZoneToStart(ZoneManager zoneManager) {
        return zoneManager.zoneData.hasEnoughZoneToStart();
    }

    @ApiStatus.Internal
    public static void randomizeZoneTickOffset(ZoneManager zoneManager) {
        Supplier<Float> random = BattleRoyale.getGameManager().getRandom();
        for (IGameZone gameZone : zoneManager.zoneData.getGameZonesList()) {
            if (gameZone.getTickOffset() < 0) {
                gameZone.setTickOffset((int) (random.get() * gameZone.getTickFrequency()));
            }
        }
    }
}
