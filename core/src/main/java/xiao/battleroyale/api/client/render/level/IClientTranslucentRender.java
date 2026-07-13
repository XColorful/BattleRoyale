package xiao.battleroyale.api.client.render.level;

import org.jetbrains.annotations.ApiStatus;
import xiao.battleroyale.api.event.IRenderLevelStageEvent;
import xiao.battleroyale.api.event.ISubmitCustomGeometryEvent;

public interface IClientTranslucentRender {

    /**
     * @deprecated Render via {@link IClientTranslucentRender#onSubmitCustomGeometry} instead
     */
    @Deprecated(since = "neoforge26.2")
    default void onAfterTranslucentBlocks(IRenderLevelStageEvent event) {};

    @ApiStatus.AvailableSince("neoforge26.2")
    void onSubmitCustomGeometry(ISubmitCustomGeometryEvent event);
}
