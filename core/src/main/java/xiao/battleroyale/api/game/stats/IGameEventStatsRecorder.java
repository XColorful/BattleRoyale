package xiao.battleroyale.api.game.stats;

import xiao.battleroyale.api.event.game.finish.GameCompleteFinishEvent;
import xiao.battleroyale.api.event.game.finish.GameStopFinishEvent;
import xiao.battleroyale.api.event.game.game.GamePlayerDamageFinishEvent;
import xiao.battleroyale.api.event.game.game.GamePlayerDeathFinishEvent;
import xiao.battleroyale.api.event.game.game.GamePlayerDownFinishEvent;
import xiao.battleroyale.api.event.game.game.GamePlayerReviveFinishEvent;
import xiao.battleroyale.api.event.game.starter.GameStartFinishEvent;
import xiao.battleroyale.api.event.game.tick.GameTickFinishEvent;

public interface IGameEventStatsRecorder {

    void onRecordStart(GameStartFinishEvent event);

    void onRecordGameTick(GameTickFinishEvent event);

    void onRecordPlayerDamage(GamePlayerDamageFinishEvent event);

    void onRecordPlayerDown(GamePlayerDownFinishEvent event);

    void onRecordPlayerRevive(GamePlayerReviveFinishEvent event);

    void onRecordPlayerDeath(GamePlayerDeathFinishEvent event);

    void onRecordStop(GameStopFinishEvent event);

    void onRecordComplete(GameCompleteFinishEvent event);
}
