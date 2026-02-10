package xiao.battleroyale.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import xiao.battleroyale.BattleRoyale;

import java.util.List;

public class ScoreUtils {

    public static int getScore(Scoreboard scoreboard, String objectiveName, String playerName) {
        Objective objective = scoreboard.getObjective(objectiveName);
        if (objective == null) return 0;
        return scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(playerName), objective).get();
    }

    /**
     * 更新比例分数，支持最小分母保底（防止数值爆炸）
     * @param minDenominator 最小分母限制（例如 KD 为 1，伤害比为 100）
     */
    public static void updateRatioScore(Scoreboard scoreboard, String playerName, String numeratorObj, String denominatorObj, String targetObj, float ratioBase, float minDenominator) {
        int numerator = getScore(scoreboard, numeratorObj, playerName);
        int denominator = getScore(scoreboard, denominatorObj, playerName);

        // 确保分母不会低于保底值，但不修改原始计分板上的受击/死亡数
        float effectiveDenominator = Math.max(minDenominator, (float) denominator);

        int result = (int) Math.floor(((float) numerator / effectiveDenominator) * ratioBase);

        getSafeScore(scoreboard, targetObj, playerName).set(result);
    }

    /**
     * 计算并更新百分比计分项 [ 分子 / (分子 + 分母) ]
     */
    public static void updatePercentageScore(Scoreboard scoreboard, String playerName, String numeratorObj, String denominatorObj, String targetObj, float ratioBase) {
        int numerator = getScore(scoreboard, numeratorObj, playerName);
        int denominator = getScore(scoreboard, denominatorObj, playerName);
        int total = numerator + denominator;

        int result = 0;
        if (total > 0) {
            result = (int) Math.floor(((float) numerator / total) * ratioBase);
        }

        getSafeScore(scoreboard, targetObj, playerName).set(result);
    }

    public static ScoreAccess getSafeScore(Scoreboard scoreboard, String objectiveName, String playerName) {
        Objective objective = scoreboard.getObjective(objectiveName);
        // 如果表不存在，需要手动创建，而不能用Scoreboard的getOrCreateObjective
        if (objective == null) {
            objective = scoreboard.addObjective(
                    objectiveName,
                    ObjectiveCriteria.DUMMY,
                    Component.literal(objectiveName),
                    ObjectiveCriteria.RenderType.INTEGER,
                    true, null
            );
        }
        return scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(playerName), objective);
    }

    public static void addScore(Scoreboard scoreboard, String objectiveName, String playerName, int amount) {
        if (amount == 0) return;
        ScoreAccess access = getSafeScore(scoreboard, objectiveName, playerName);
        access.set(access.get() + amount);
    }

    public static void removeObjectives(Scoreboard scoreboard, List<String> objectiveNames) {
        for (String name : objectiveNames) {
            Objective oldObj = scoreboard.getObjective(name);
            if (oldObj != null) {
                scoreboard.removeObjective(oldObj);
            }
        }
    }

    public static void addObjectivesIfNull(Scoreboard scoreboard, List<String> objectiveNames) {
        for (String name : objectiveNames) {
            Objective oldObj = scoreboard.getObjective(name);
            if (oldObj == null) {
                scoreboard.addObjective(name, ObjectiveCriteria.DUMMY,
                        Component.literal(name),
                        ObjectiveCriteria.RenderType.INTEGER);
            }
        }
    }

    // 玩家列表
    public static void setListObjective(Scoreboard scoreboard, String objectiveName) {
        Objective objective = scoreboard.getObjective(objectiveName);
        if (objective != null) {
            // 只有当当前 Slot 显示的不是该 Objective 时才设置，防止发包冗余
            if (scoreboard.getDisplayObjective(Scoreboard.DISPLAY_SLOT_LIST) != objective) {
                scoreboard.setDisplayObjective(Scoreboard.DISPLAY_SLOT_LIST, objective);
            }
        }
    }

    // 屏幕右边
    public static void setSidebarObjective(Scoreboard scoreboard, String objectiveName) {
        Objective objective = scoreboard.getObjective(objectiveName);
        if (objective != null) {
            // 防止重复设置导致客户端渲染闪烁
            if (scoreboard.getDisplayObjective(Scoreboard.DISPLAY_SLOT_SIDEBAR) != objective) {
                scoreboard.setDisplayObjective(Scoreboard.DISPLAY_SLOT_SIDEBAR, objective);
            }
        }
    }
}