package xiao.battleroyale.config.common.game.spawn.type;

import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.spawn.ISpawnEntry;
import xiao.battleroyale.api.game.spawn.type.detail.SpawnDetailTag;
import xiao.battleroyale.api.game.spawn.type.shape.SpawnShapeTag;
import xiao.battleroyale.api.game.spawn.type.SpawnTypeTag;
import xiao.battleroyale.config.common.game.spawn.type.detail.CommonDetailType;
import xiao.battleroyale.config.common.game.spawn.type.shape.SpawnShapeType;
import xiao.battleroyale.util.StringUtils;

public class GroundEntry implements ISpawnEntry {

    // common
    private SpawnShapeType shapeType;
    private Vec3 centerPos;
    private Vec3 dimension;
    private CommonDetailType detailType;
    // detail
    boolean teamTogether;
    boolean findGround;
    double randomRange;

    public GroundEntry(SpawnShapeType shapeType, Vec3 center, Vec3 dimension, CommonDetailType detailType,
                       boolean teamTogether, boolean findGround, double randomRange) {
        this.shapeType = shapeType;
        this.centerPos = center;
        this.dimension = dimension;
        this.detailType = detailType;

        this.teamTogether = teamTogether;
        this.findGround = findGround;
        this.randomRange = randomRange;
    }

    @Override
    public String getType() {
        return SpawnTypeTag.SPAWN_TYPE_GROUND;
    }

    @Override
    public JsonObject toJson() {
        // common
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(SpawnTypeTag.TYPE_NAME, getType());
        jsonObject.addProperty(SpawnShapeTag.TYPE_NAME, shapeType.getName());
        jsonObject.addProperty(SpawnShapeTag.CENTER, StringUtils.vectorToString(centerPos));
        jsonObject.addProperty(SpawnShapeTag.DIMENSION, StringUtils.vectorToString(dimension));

        jsonObject.addProperty(SpawnDetailTag.TYPE_NAME, detailType.getName());

        // detail
        jsonObject.addProperty(SpawnDetailTag.GROUND_TEAM_TOGETHER, teamTogether);
        jsonObject.addProperty(SpawnDetailTag.GROUND_FIND_GROUND, findGround);
        jsonObject.addProperty(SpawnDetailTag.GROUND_RANDOM_RANGE, randomRange);

        return jsonObject;
    }

    public static GroundEntry fromJson(JsonObject jsonObject) {
        // common
        String shapeTypeString = jsonObject.has(SpawnShapeTag.TYPE_NAME) ? jsonObject.getAsJsonPrimitive(SpawnShapeTag.TYPE_NAME).getAsString() : "";
        SpawnShapeType shapeType = SpawnShapeType.fromName(shapeTypeString);
        if (shapeType == null) {
            BattleRoyale.LOGGER.info("Unknown shapeType in GroundEntry, skipped");
            return null;
        }
        String centerString = jsonObject.has(SpawnShapeTag.CENTER) ? jsonObject.getAsJsonPrimitive(SpawnShapeTag.CENTER).getAsString() : "";
        String dimensionString = jsonObject.has(SpawnShapeTag.DIMENSION) ? jsonObject.getAsJsonPrimitive(SpawnShapeTag.DIMENSION).getAsString() : "";
        Vec3 center = StringUtils.parseVectorString(centerString);
        Vec3 dimension = StringUtils.parseVectorString(dimensionString);
        if (center == null || dimension == null) {
            BattleRoyale.LOGGER.info("Invalid center or dimension in GroundEntry, skipped");
            return null;
        }

        String detailTypeString = jsonObject.has(SpawnDetailTag.TYPE_NAME) ? jsonObject.getAsJsonPrimitive(SpawnDetailTag.TYPE_NAME).getAsString() : "";
        CommonDetailType detailType = CommonDetailType.fromName(detailTypeString);
        if (detailType == null) {
            BattleRoyale.LOGGER.info("Unknown detailType for GroundEntry, skipped");
            return null;
        }

        // detail
        boolean teamTogether = jsonObject.has(SpawnDetailTag.GROUND_TEAM_TOGETHER) && jsonObject.getAsJsonPrimitive(SpawnDetailTag.GROUND_TEAM_TOGETHER).getAsBoolean();
        boolean findGround = jsonObject.has(SpawnDetailTag.GROUND_FIND_GROUND) && jsonObject.getAsJsonPrimitive(SpawnDetailTag.GROUND_FIND_GROUND).getAsBoolean();
        double range = jsonObject.has(SpawnDetailTag.GROUND_RANDOM_RANGE) ? jsonObject.getAsJsonPrimitive(SpawnDetailTag.GROUND_RANDOM_RANGE).getAsDouble() : 0;

        return new GroundEntry(shapeType, center, dimension, detailType, teamTogether, findGround, range);
    }
}
