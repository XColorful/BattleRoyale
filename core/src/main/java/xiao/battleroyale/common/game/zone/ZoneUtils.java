package xiao.battleroyale.common.game.zone;

import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.common.game.GameManager;

import java.util.function.Supplier;

public class ZoneUtils {

    public static boolean hasEnoughZoneToStart() {
        ZoneManager zoneManager = ZoneManager.get();
        return zoneManager.zoneData.hasEnoughZoneToStart();
    }

    public static void randomizeZoneTickOffset(ZoneManager zoneManager) {
        Supplier<Float> random = GameManager.get().getRandom();
        for (IGameZone gameZone : zoneManager.zoneData.getGameZonesList()) {
            if (gameZone.getTickOffset() < 0) {
                gameZone.setTickOffset((int) (random.get() * gameZone.getTickFrequency()));
            }
        }
    }
}
