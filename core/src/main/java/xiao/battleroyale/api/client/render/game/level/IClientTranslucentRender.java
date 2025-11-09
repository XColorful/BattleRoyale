package xiao.battleroyale.api.client.render.game.level;

import xiao.battleroyale.api.client.event.IRenderLevelStageEvent;
import xiao.battleroyale.api.client.render.game.IClientRendererName;

public interface IClientTranslucentRender extends IClientRendererName {

    void onAfterTranslucentBlocks(IRenderLevelStageEvent event);
}
