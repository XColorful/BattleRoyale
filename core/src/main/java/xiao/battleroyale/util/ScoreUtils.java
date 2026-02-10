package xiao.battleroyale.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

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
}