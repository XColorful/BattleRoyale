package xiao.battleroyale.api.game.zone.special;

import xiao.battleroyale.api.client.render.level.IClientSimpleZoneRenderer;
import xiao.battleroyale.api.event.IRenderLevelStageEvent;
import xiao.battleroyale.client.game.data.ClientSingleZoneData;

/**
 * 所有区域特殊功能, 未重载则无功能
 */
public interface IZoneSpecialClient {

    default void additionalZoneRender(IRenderLevelStageEvent event, IClientSimpleZoneRenderer clientZoneRenderer, ClientSingleZoneData zoneData) {}
}
