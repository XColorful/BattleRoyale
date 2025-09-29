package xiao.battleroyale.event;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.ICustomEventData;

public class EventPoster {

    public static boolean postEvent(ICustomEventData customEventData) {
        return BattleRoyale.getEventPoster().postEvent(customEventData);
    }
}
