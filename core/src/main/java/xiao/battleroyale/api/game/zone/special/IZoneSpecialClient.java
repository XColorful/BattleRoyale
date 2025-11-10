package xiao.battleroyale.api.game.zone.special;

/**
 * 所有区域特殊功能, 未重载则无功能
 */
public interface IZoneSpecialClient {

    default void additionalZoneRender(Object clientZoneRenderer) {}
}
