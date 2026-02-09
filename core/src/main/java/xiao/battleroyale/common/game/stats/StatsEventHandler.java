package xiao.battleroyale.common.game.stats;

import xiao.battleroyale.api.event.game.finish.GameStopFinishEvent;
import xiao.battleroyale.api.event.game.game.GamePlayerDamageFinishEvent;
import xiao.battleroyale.api.event.game.game.GamePlayerDeathFinishEvent;
import xiao.battleroyale.api.event.game.game.GamePlayerDownFinishEvent;
import xiao.battleroyale.api.event.game.game.GamePlayerReviveFinishEvent;
import xiao.battleroyale.api.event.game.starter.GameStartFinishEvent;

public class StatsEventHandler {

    protected static void onGameStart(StatsManager statsManager, GameStartFinishEvent event) {
        ;
    }

    protected static void onGamePlayerDamage(StatsManager statsManager, GamePlayerDamageFinishEvent event) {
        ;
    }

    protected static void onGamePlayerDown(StatsManager statsManager, GamePlayerDownFinishEvent event) {
        ;
    }

    protected static void onGamePlayerRevive(StatsManager statsManager, GamePlayerReviveFinishEvent event) {
        ;
    }

    protected static void onGamePlayerDeath(StatsManager statsManager, GamePlayerDeathFinishEvent event) {
        ;
    }

    protected static void onGameStop(StatsManager statsManager, GameStopFinishEvent event) {
        ;
    }
}