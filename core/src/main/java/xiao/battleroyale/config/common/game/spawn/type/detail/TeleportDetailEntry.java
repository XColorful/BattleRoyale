package xiao.battleroyale.config.common.game.spawn.type.detail;

import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.spawn.type.detail.SpawnDetailTag;
import xiao.battleroyale.config.common.game.spawn.type.shape.SpawnShapeType;
import xiao.battleroyale.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class TeleportDetailEntry extends AbstractDetailEntry<TeleportDetailEntry> {

    // random & common
    public boolean teamTogether;
    public boolean findGround;
    public double randomRange;
    public int hangTime;
    // fixed
    public final List<Vec3> fixedPos = new ArrayList<>();
    // distributed
    public int fixedSimulationCount = 0;
    public double playerFactorContribution = 0;
    public boolean useGoldenSpiral = false;
    public boolean allowOnBorder = false;
    public double globalShrinkRatio = 1;
    // fixed & distributed
    public boolean needShuffle = false;


    // fixed
    public TeleportDetailEntry(boolean teamTogether, boolean findGround, double randomRange, int hangTime,
                               List<Vec3> fixedPos,
                               boolean needShuffle) {
        this(teamTogether, findGround, randomRange, hangTime);
        this.fixedPos.addAll(fixedPos);
        this.needShuffle = needShuffle;
    }
    // random & common
    public TeleportDetailEntry(boolean teamTogether, boolean findGround, double randomRange, int hangTime) {
        this.teamTogether = teamTogether;
        this.findGround = findGround;
        this.randomRange = randomRange;
        this.hangTime = hangTime;
    }
    // distributed
    public TeleportDetailEntry(boolean teamTogether, boolean findGround, double randomRange, int hangTime,
                               int fixedSimulationCount, double playerFactorContribution, boolean useGoldenSpiral, boolean allowOnBorder, double globalShrinkRatio,
                               boolean needShuffle) {
        this(teamTogether, findGround, randomRange, hangTime);
        this.fixedSimulationCount = fixedSimulationCount;
        this.playerFactorContribution = playerFactorContribution;
        this.useGoldenSpiral = useGoldenSpiral;
        this.allowOnBorder = allowOnBorder;
        this.globalShrinkRatio = globalShrinkRatio;
        this.needShuffle = needShuffle;
    }
    // full
    public TeleportDetailEntry(boolean teamTogether, boolean findGround, double randomRange, int hangTime,
                               List<Vec3> fixedPos,
                               int fixedSimulationCount, double playerFactorContribution, boolean useGoldenSpiral, boolean allowOnBorder, double globalShrinkRatio,
                               boolean needShuffle) {
        this(teamTogether, findGround, randomRange, hangTime);
        this.fixedPos.addAll(fixedPos);
        this.fixedSimulationCount = fixedSimulationCount;
        this.playerFactorContribution = playerFactorContribution;
        this.useGoldenSpiral = useGoldenSpiral;
        this.allowOnBorder = allowOnBorder;
        this.globalShrinkRatio = globalShrinkRatio;
        this.needShuffle = needShuffle;
    }
    @Override public TeleportDetailEntry copy() {
        return new TeleportDetailEntry(teamTogether, findGround, randomRange, hangTime,
                new ArrayList<>(fixedPos),
                fixedSimulationCount, playerFactorContribution, useGoldenSpiral, allowOnBorder, globalShrinkRatio,
                needShuffle);
    }

    @Override
    public void toJson(JsonObject jsonObject, SpawnShapeType shapeType, CommonDetailType detailType) {
        jsonObject.addProperty(SpawnDetailTag.GROUND_TEAM_TOGETHER, teamTogether);
        jsonObject.addProperty(SpawnDetailTag.GROUND_FIND_GROUND, findGround);
        jsonObject.addProperty(SpawnDetailTag.GROUND_RANDOM_RANGE, randomRange);
        jsonObject.addProperty(SpawnDetailTag.GROUND_HANG_TIME, hangTime);
        switch (detailType) {
            case FIXED -> {
                jsonObject.add(SpawnDetailTag.GROUND_FIXED_POS, JsonUtils.writeVec3ListToJson(fixedPos));
            }
            case RANDOM -> {}
            case DISTRIBUTED -> {
                if (shapeType == SpawnShapeType.CIRCLE) {
                    jsonObject.addProperty(SpawnDetailTag.USE_GOLDEN_SPIRAL, useGoldenSpiral);
                }
                jsonObject.addProperty(SpawnDetailTag.FIXED_SIMULATION_COUNT, fixedSimulationCount);
                jsonObject.addProperty(SpawnDetailTag.PLAYER_FACTOR_CONTRIBUTION, playerFactorContribution);
                jsonObject.addProperty(SpawnDetailTag.ALLOW_ON_BORDER, allowOnBorder);
                jsonObject.addProperty(SpawnDetailTag.GLOBAL_SHRINK_RATIO, globalShrinkRatio);
            }
        }
        switch (detailType) {
            case FIXED, DISTRIBUTED -> jsonObject.addProperty(SpawnDetailTag.NEED_SHUFFLE, needShuffle);
        }
    }

    public static @NotNull TeleportDetailEntry fromJson(JsonObject jsonObject, CommonDetailType detailType) {
        boolean teamTogether = JsonUtils.getJsonBool(jsonObject, SpawnDetailTag.GROUND_TEAM_TOGETHER, false);
        boolean findGround = JsonUtils.getJsonBool(jsonObject, SpawnDetailTag.GROUND_FIND_GROUND, false);
        double range = JsonUtils.getJsonDouble(jsonObject, SpawnDetailTag.GROUND_RANDOM_RANGE, 0);
        int hangTime = JsonUtils.getJsonInt(jsonObject, SpawnDetailTag.GROUND_HANG_TIME, 20 * 15);
        switch (detailType) {
            case FIXED -> {
                List<Vec3> fixedPos  = JsonUtils.getJsonVecList(jsonObject, SpawnDetailTag.GROUND_FIXED_POS);
                boolean needShuffle = JsonUtils.getJsonBool(jsonObject, SpawnDetailTag.NEED_SHUFFLE, false);
                return new TeleportDetailEntry(teamTogether, findGround, range, hangTime,
                        fixedPos,
                        needShuffle);
            }
            case RANDOM -> {
                return new TeleportDetailEntry(teamTogether, findGround, range, hangTime);
            }
            case DISTRIBUTED -> {
                int fixedSimulationCount = JsonUtils.getJsonInt(jsonObject, SpawnDetailTag.FIXED_SIMULATION_COUNT, 0);
                double playerFactorContribution = JsonUtils.getJsonDouble(jsonObject, SpawnDetailTag.PLAYER_FACTOR_CONTRIBUTION, 0);
                boolean useGoldenSpiral = JsonUtils.getJsonBool(jsonObject, SpawnDetailTag.USE_GOLDEN_SPIRAL, false);
                boolean allowOnBorder = JsonUtils.getJsonBool(jsonObject, SpawnDetailTag.ALLOW_ON_BORDER, false);
                double globalShrinkRatio = JsonUtils.getJsonDouble(jsonObject, SpawnDetailTag.GLOBAL_SHRINK_RATIO, 1);
                boolean needShuffle = JsonUtils.getJsonBool(jsonObject, SpawnDetailTag.NEED_SHUFFLE, false);
                return new TeleportDetailEntry(teamTogether, findGround, range, hangTime,
                        fixedSimulationCount, playerFactorContribution, useGoldenSpiral, allowOnBorder, globalShrinkRatio,
                        needShuffle);
            }
            default -> {
                return new TeleportDetailEntry(teamTogether, findGround, range, hangTime, new ArrayList<>(), 0, 0, false, false, 1, false);
            }
        }
    }
}
