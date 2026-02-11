package xiao.battleroyale.common.game.stats;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.config.common.game.stats.StatsConfigManager;
import xiao.battleroyale.config.common.game.stats.scoreboard.MainObjectiveEntry;
import xiao.battleroyale.config.common.game.stats.scoreboard.ScoreboardEntry;
import xiao.battleroyale.config.common.game.stats.scoreboard.SecondObjectiveEntry;
import xiao.battleroyale.config.common.game.stats.scoreboard.SpecialObjectiveEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatsConfigHelper {

    /**
     * 返回是否设置成功
     */
    protected static boolean updateStats(StatsManager statsManager, StatsConfigManager.StatsConfig statsConfig) {
        IGameManager gameManager = BattleRoyale.getGameManager();
        if (gameManager.isInGame()) return false;

        ScoreboardEntry scoreboardEntry = statsConfig.getScoreboardEntry();
        if (!scoreboardEntry.recordScoreboard) {
            statsManager.recordScoreboard = false;
            return true;
        }

        // 检查配置有效性：所有 ObjectiveName 必须不为空且互不相同
        // ↓AI拉的屎，反正不是我搬砖
        MainObjectiveEntry main = scoreboardEntry.mainObjective;
        SecondObjectiveEntry second = scoreboardEntry.secondObjective;
        SpecialObjectiveEntry special = scoreboardEntry.specialObjective;

        // 1. 集中收集所有 ObjectiveName
        List<String> names = new ArrayList<>();
        if (main != null) {
            names.add(main.player_to_player_damage); names.add(main.other_to_player_damage);
            names.add(main.player_damage_by_player); names.add(main.player_damage_by_other);
            names.add(main.player_knock_player); names.add(main.other_knock_player);
            names.add(main.player_down_by_player); names.add(main.player_down_by_other);
            names.add(main.player_revive);
            names.add(main.player_kill_player); names.add(main.other_kill_player);
            names.add(main.player_death_by_player); names.add(main.player_death_by_other);
            names.add(main.player_win); names.add(main.player_lose);
        }
        if (second != null) {
            names.add(second.player_attack_rate);
            names.add(second.player_kd);
            names.add(second.player_win_rate);
        }
        if (special != null) {
            names.add(special.player_journey);
        }

        // 2. 排序校验：不为空且互不相同
        if (names.isEmpty()) return false;
        Collections.sort(names);

        for (int i = 0; i < names.size(); i++) {
            String current = names.get(i);
            // 非空检查
            if (current == null || current.isEmpty()) return false;
            // 相邻唯一性检查 (排序后相同的字符串必然相邻)
            if (i > 0 && current.equals(names.get(i - 1))) return false;
        }

        // 3. 应用配置到 StatsManager (原子性赋值)
        statsManager.recordScoreboard = scoreboardEntry.recordScoreboard;
        statsManager.resetScoreboardAtStart = scoreboardEntry.resetScoreboardAtStart;
        statsManager.mcMaxHealth = scoreboardEntry.mcMaxHealth;
        statsManager.damageMultiplier = scoreboardEntry.damageMultiplier;
        statsManager.ratioBase = scoreboardEntry.ratioBase;
        statsManager.scoreboardCycleInterval = scoreboardEntry.scoreboardCycleInterval;
        statsManager.cycleObjectiveName = scoreboardEntry.cycleObjectiveNames;

        if (main != null) {
            statsManager.player_to_player_damage_ObjectiveName = main.player_to_player_damage;
            statsManager.other_to_player_damage_ObjectiveName = main.other_to_player_damage;
            statsManager.player_damage_by_player_ObjectiveName = main.player_damage_by_player;
            statsManager.player_damage_by_other_ObjectiveName = main.player_damage_by_other;
            statsManager.player_knock_player_ObjectiveName = main.player_knock_player;
            statsManager.other_knock_player_ObjectiveName = main.other_knock_player;
            statsManager.player_down_by_player_ObjectiveName = main.player_down_by_player;
            statsManager.player_down_by_other_ObjectiveName = main.player_down_by_other;
            statsManager.player_revive_ObjectiveName = main.player_revive;
            statsManager.player_kill_player_ObjectiveName = main.player_kill_player;
            statsManager.other_kill_player_ObjectiveName = main.other_kill_player;
            statsManager.player_death_by_player_ObjectiveName = main.player_death_by_player;
            statsManager.player_death_by_other_ObjectiveName = main.player_death_by_other;
            statsManager.player_win_ObjectiveName = main.player_win;
            statsManager.player_lose_ObjectiveName = main.player_lose;
        }

        if (second != null) {
            statsManager.player_attack_rate_ObjectiveName = second.player_attack_rate;
            statsManager.player_kd_ObjectiveName = second.player_kd;
            statsManager.player_win_rate_ObjectiveName = second.player_win_rate;
        }

        if (special != null) {
            statsManager.enableJourneyStats = special.enableJourneyStats;
            statsManager.journeyStatsDelay = special.journeyStatsDelay;
            statsManager.player_journey_ObjectiveName = special.player_journey;
        }

        statsManager.listObjectiveAfterGame = scoreboardEntry.listObjectiveAfterGame;
        statsManager.sidebarObjectiveAfterGame = scoreboardEntry.sidebarObjectiveAfterGame;

        return true;
    }

    public static class DefaultObjectiveName {
        public static final String OBJECTNAME_PREFIX = BattleRoyale.MOD_NAME_SHORT;

        // 原始数据记分项名
        public static final String PLAYER_TO_PLAYER_DAMAGE = String.format("%s_hurt", OBJECTNAME_PREFIX);
        public static final String OTHER_TO_PLAYER_DAMAGE = String.format("%s_otherHurt", OBJECTNAME_PREFIX);
        public static final String PLAYER_DAMAGE_BY_PLAYER = String.format("%s_damage", OBJECTNAME_PREFIX);
        public static final String PLAYER_DAMAGE_BY_OTHER = String.format("%s_otherDamage", OBJECTNAME_PREFIX);

        public static final String PLAYER_KNOCK_PLAYER = String.format("%s_knock", OBJECTNAME_PREFIX);
        public static final String OTHER_KNOCK_PLAYER = String.format("%s_otherKnock", OBJECTNAME_PREFIX);
        public static final String PLAYER_DOWN_BY_PLAYER = String.format("%s_down", OBJECTNAME_PREFIX);
        public static final String PLAYER_DOWN_BY_OTHER = String.format("%s_otherDown", OBJECTNAME_PREFIX);

        public static final String PLAYER_REVIVE = String.format("%s_revive", OBJECTNAME_PREFIX);

        public static final String PLAYER_KILL_PLAYER = String.format("%s_kill", OBJECTNAME_PREFIX);
        public static final String OTHER_KILL_PLAYER = String.format("%s_otherKill", OBJECTNAME_PREFIX);
        public static final String PLAYER_DEATH_BY_PLAYER = String.format("%s_death", OBJECTNAME_PREFIX);
        public static final String PLAYER_DEATH_BY_OTHER = String.format("%s_otherDeath", OBJECTNAME_PREFIX);

        public static final String PLAYER_WIN = String.format("%s_win", OBJECTNAME_PREFIX);
        public static final String PLAYER_LOSE = String.format("%s_lose", OBJECTNAME_PREFIX);

        // 二次计算记分项名
        public static final String PLAYER_ATTACK_RATE = String.format("%s_attackRate", OBJECTNAME_PREFIX);
        public static final String PLAYER_KD = String.format("%s_kd", OBJECTNAME_PREFIX);
        public static final String PLAYER_WIN_RATE = String.format("%s_winRate", OBJECTNAME_PREFIX);

        // 特殊统计记分项名
        public static final String PLAYER_JOURNEY = String.format("%s_journey", OBJECTNAME_PREFIX);
        public static final String PLAYER_MAX_SPEED = String.format("%s_maxSpeed", OBJECTNAME_PREFIX);
    }

    public static class DeathMatchObjectiveName {
        public static final String OBJECTNAME_PREFIX = "dm";

        // 原始数据记分项名
        public static final String PLAYER_TO_PLAYER_DAMAGE = String.format("%s_hurt", OBJECTNAME_PREFIX);
        public static final String OTHER_TO_PLAYER_DAMAGE = String.format("%s_otherHurt", OBJECTNAME_PREFIX);
        public static final String PLAYER_DAMAGE_BY_PLAYER = String.format("%s_damage", OBJECTNAME_PREFIX);
        public static final String PLAYER_DAMAGE_BY_OTHER = String.format("%s_otherDamage", OBJECTNAME_PREFIX);

        public static final String PLAYER_KNOCK_PLAYER = String.format("%s_knock", OBJECTNAME_PREFIX);
        public static final String OTHER_KNOCK_PLAYER = String.format("%s_otherKnock", OBJECTNAME_PREFIX);
        public static final String PLAYER_DOWN_BY_PLAYER = String.format("%s_down", OBJECTNAME_PREFIX);
        public static final String PLAYER_DOWN_BY_OTHER = String.format("%s_otherDown", OBJECTNAME_PREFIX);

        public static final String PLAYER_REVIVE = String.format("%s_revive", OBJECTNAME_PREFIX);

        public static final String PLAYER_KILL_PLAYER = String.format("%s_kill", OBJECTNAME_PREFIX);
        public static final String OTHER_KILL_PLAYER = String.format("%s_otherKill", OBJECTNAME_PREFIX);
        public static final String PLAYER_DEATH_BY_PLAYER = String.format("%s_death", OBJECTNAME_PREFIX);
        public static final String PLAYER_DEATH_BY_OTHER = String.format("%s_otherDeath", OBJECTNAME_PREFIX);

        public static final String PLAYER_WIN = String.format("%s_win", OBJECTNAME_PREFIX);
        public static final String PLAYER_LOSE = String.format("%s_lose", OBJECTNAME_PREFIX);

        // 二次计算记分项名
        public static final String PLAYER_ATTACK_RATE = String.format("%s_attackRate", OBJECTNAME_PREFIX);
        public static final String PLAYER_KD = String.format("%s_kd", OBJECTNAME_PREFIX);
        public static final String PLAYER_WIN_RATE = String.format("%s_winRate", OBJECTNAME_PREFIX);

        // 特殊统计记分项名
        public static final String PLAYER_JOURNEY = String.format("%s_journey", OBJECTNAME_PREFIX);
        public static final String PLAYER_MAX_SPEED = String.format("%s_maxSpeed", OBJECTNAME_PREFIX);
    }
}