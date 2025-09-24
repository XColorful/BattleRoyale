package xiao.battleroyale.api.game;

import xiao.battleroyale.config.common.game.gamerule.type.GameEntry;

public interface IGameConfigGetter {

    GameEntry getGameEntry();
    int getGameruleConfigId();
    int getSpawnConfigId();
    int getBotConfigId();
    String getGameruleConfigName(int gameId);
    String getSpawnConfigName(int id);
    String getBotConfigName(int id);
    String getZoneConfigFileName();
}
