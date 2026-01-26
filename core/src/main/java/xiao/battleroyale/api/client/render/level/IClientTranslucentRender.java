package xiao.battleroyale.api.client.render.level;

import xiao.battleroyale.api.client.event.IRenderLevelStageEvent;
import xiao.battleroyale.api.client.render.IClientRendererName;

public interface IClientTranslucentRender extends IClientRendererName {

    void onAfterTranslucentBlocks(IRenderLevelStageEvent event);
}
