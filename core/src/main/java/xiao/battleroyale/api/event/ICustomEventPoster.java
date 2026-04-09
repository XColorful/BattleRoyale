package xiao.battleroyale.api.event;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.event.EventDispatcher;

public interface ICustomEventPoster {

    boolean postCustomEvent(ICustomEvent customEvent);

    @ApiStatus.Internal
    @NotNull EventDispatcher getEventDispatcher(Class<? extends ICustomEvent> eventClass);
}
