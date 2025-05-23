package xiao.battleroyale.api.game.zone;

/**
 * 获取供游戏运行所需的信息
 * 并非游戏进行时更新的对象
 */
public interface IZoneData {

    ZoneDataType getDataType();
}