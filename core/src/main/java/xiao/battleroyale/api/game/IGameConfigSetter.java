package xiao.battleroyale.api.game;

public interface IGameConfigSetter {

    boolean setGameruleConfigId(int gameId);
    boolean setSpawnConfigId(int id);
    boolean setStatsConfigId(int id);
    boolean setBotConfigId(int id);
}
