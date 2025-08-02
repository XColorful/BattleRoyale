package xiao.battleroyale.config.common.game.spawn.type;

import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.spawn.IGameSpawner;
import xiao.battleroyale.api.game.spawn.type.detail.SpawnDetailTag;
import xiao.battleroyale.api.game.spawn.type.shape.SpawnShapeTag;
import xiao.battleroyale.api.game.spawn.type.SpawnTypeTag;
import xiao.battleroyale.common.game.spawn.vanilla.TeleportSpawner;
import xiao.battleroyale.config.common.game.spawn.type.detail.CommonDetailType;
import xiao.battleroyale.config.common.game.spawn.type.shape.SpawnShapeType;
import xiao.battleroyale.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class TeleportEntry extends AbstractCommonSpawnEntry {

    // detail
    private final CommonDetailType detailType;
    private final DetailInfo detailInfo;

    public record DetailInfo(List<Vec3> fixedPos,
                              boolean teamTogether,
                              boolean findGround,
                              double randomRange,
                             int hangTime) {}

    public TeleportEntry(SpawnShapeType shapeType, Vec3 center, Vec3 dimension,
                         CommonDetailType detailType,
                         DetailInfo detailInfo) {
        super(shapeType, center, dimension);

        this.detailType = detailType;
        this.detailInfo = detailInfo;
    }

    @Override
    public String getType() {
        return SpawnTypeTag.SPAWN_TYPE_TELEPORT;
    }

    @Override
    public IGameSpawner createGameSpawner() {
        return new TeleportSpawner(shapeType, centerPos, dimension, preZoneId, detailType, detailInfo);
    }

    @Override
    public JsonObject toJson() {
        // common
        JsonObject jsonObject = super.toJson();
        // detail
        jsonObject.addProperty(SpawnDetailTag.TYPE_NAME, detailType.getName());
        switch (this.detailType) {
            case FIXED -> jsonObject.add(SpawnDetailTag.GROUND_FIXED_POS, JsonUtils.writeVec3ListToJson(this.detailInfo.fixedPos));
            case RANDOM -> {}
        }
        jsonObject.addProperty(SpawnDetailTag.GROUND_TEAM_TOGETHER, this.detailInfo.teamTogether);
        jsonObject.addProperty(SpawnDetailTag.GROUND_FIND_GROUND, this.detailInfo.findGround);
        jsonObject.addProperty(SpawnDetailTag.GROUND_RANDOM_RANGE, this.detailInfo.randomRange);
        jsonObject.addProperty(SpawnDetailTag.GROUND_HANG_TIME, this.detailInfo.hangTime);

        return jsonObject;
    }

    @Nullable
    public static TeleportEntry fromJson(JsonObject jsonObject) {
        // common
        SpawnShapeType shapeType = SpawnShapeType.fromName(JsonUtils.getJsonString(jsonObject, SpawnShapeTag.TYPE_NAME, ""));
        if (shapeType == null) {
            BattleRoyale.LOGGER.info("Unknown shapeType in GroundEntry, skipped");
            return null;
        }
        Vec3 center = JsonUtils.getJsonVec(jsonObject, SpawnShapeTag.CENTER, null);
        Vec3 dimension = JsonUtils.getJsonVec(jsonObject, SpawnShapeTag.DIMENSION, null);
        if (center == null || dimension == null) {
            BattleRoyale.LOGGER.info("Invalid center or dimension in GroundEntry, skipped");
            return null;
        }

        // detail
        CommonDetailType detailType = CommonDetailType.fromName(JsonUtils.getJsonString(jsonObject, SpawnDetailTag.TYPE_NAME, ""));
        if (detailType == null) {
            BattleRoyale.LOGGER.info("Unknown detailType for GroundEntry, skipped");
            return null;
        }
        List<Vec3> fixedPos = new ArrayList<>();
        switch (detailType) {
            case FIXED -> fixedPos = JsonUtils.getJsonVecList(jsonObject, SpawnDetailTag.GROUND_FIXED_POS);
            case RANDOM -> {}
        }
        boolean teamTogether = JsonUtils.getJsonBool(jsonObject, SpawnDetailTag.GROUND_TEAM_TOGETHER, false);
        boolean findGround = JsonUtils.getJsonBool(jsonObject, SpawnDetailTag.GROUND_FIND_GROUND, false);
        double range = JsonUtils.getJsonDouble(jsonObject, SpawnDetailTag.GROUND_FIND_GROUND, 0);
        int hangTime = JsonUtils.getJsonInt(jsonObject, SpawnDetailTag.GROUND_HANG_TIME, 20 * 15);

        return new TeleportEntry(shapeType, center, dimension,
                detailType,
                new DetailInfo(fixedPos, teamTogether, findGround, range, hangTime)
        );
    }
}
