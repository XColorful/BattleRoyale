package xiao.battleroyale.event;

import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.IEventPoster;

public class EventPoster {

    private static IEventPoster eventPoster;

    public static void initialize(IEventPoster eventPoster) {
        EventPoster.eventPoster = eventPoster;
    }

    public static boolean postEvent(ICustomEventData customEventData) {
        if (eventPoster == null) {
            throw new IllegalStateException("Event poster has not been initialized. Call init() first.");
        }
        return eventPoster.postEvent(customEventData);
    }
}
