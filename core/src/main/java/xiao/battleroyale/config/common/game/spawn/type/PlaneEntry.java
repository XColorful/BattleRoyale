package xiao.battleroyale.config.common.game.spawn.type;

import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.spawn.IGameSpawner;
import xiao.battleroyale.api.game.spawn.type.detail.SpawnDetailTag;
import xiao.battleroyale.api.game.spawn.type.shape.SpawnShapeTag;
import xiao.battleroyale.api.game.spawn.type.SpawnTypeTag;
import xiao.battleroyale.common.game.spawn.special.PlaneSpawner;
import xiao.battleroyale.config.common.game.spawn.type.detail.CommonDetailType;
import xiao.battleroyale.config.common.game.spawn.type.detail.PlaneDetailEntry;
import xiao.battleroyale.config.common.game.spawn.type.shape.SpawnShapeType;
import xiao.battleroyale.util.JsonUtils;
import xiao.battleroyale.util.StringUtils;

public class PlaneEntry extends AbstractCommonSpawnEntry<PlaneDetailEntry> {

    public PlaneEntry(SpawnShapeType shapeType, Vec3 center, Vec3 dimension,
                      CommonDetailType detailType, PlaneDetailEntry detailEntry) {
        super(shapeType, center, dimension, detailType, detailEntry);
    }

    @Override
    public String getType() {
        return SpawnTypeTag.SPAWN_TYPE_PLANE;
    }

    @Override
    public IGameSpawner createGameSpawner() {
        return new PlaneSpawner(shapeType, centerPos, dimension, preZoneId, detailType, detailEntry);
    }

    @Override
    public JsonObject toJson() {
        // common
        JsonObject jsonObject = super.toJson();
        // detail
        jsonObject.addProperty(SpawnDetailTag.TYPE_NAME, detailType.getName());
        this.detailEntry.toJson(jsonObject, this.detailType);

        return jsonObject;
    }

    @Nullable
    public static PlaneEntry fromJson(JsonObject jsonObject) {
        // common
        SpawnShapeType shapeType = SpawnShapeType.fromName(JsonUtils.getJsonString(jsonObject, SpawnShapeTag.TYPE_NAME, ""));
        if (shapeType == null) {
            BattleRoyale.LOGGER.info("Unknown shapeType in PlaneEntry, skipped");
            return null;
        }
        Vec3 center = StringUtils.parseVectorString(JsonUtils.getJsonString(jsonObject, SpawnShapeTag.CENTER, ""));
        Vec3 dimension = StringUtils.parseVectorString(JsonUtils.getJsonString(jsonObject, SpawnShapeTag.DIMENSION, ""));
        if (center == null || dimension == null) {
            BattleRoyale.LOGGER.info("Invalid center or dimension in PlaneEntry, skipped");
            return null;
        }

        // detail
        CommonDetailType detailType = CommonDetailType.fromName(JsonUtils.getJsonString(jsonObject, SpawnDetailTag.TYPE_NAME, ""));
        if (detailType == null) {
            BattleRoyale.LOGGER.info("Unknown detailType for GroundEntry, skipped");
            return null;
        }
        PlaneDetailEntry detailEntry = PlaneDetailEntry.fromJson(jsonObject, detailType);

        return new PlaneEntry(shapeType, center, dimension,
                detailType, detailEntry);
    }
}
