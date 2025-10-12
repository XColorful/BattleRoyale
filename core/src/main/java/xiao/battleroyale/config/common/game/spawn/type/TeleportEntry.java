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
import xiao.battleroyale.config.common.game.spawn.type.detail.TeleportDetailEntry;
import xiao.battleroyale.config.common.game.spawn.type.shape.SpawnShapeType;
import xiao.battleroyale.util.JsonUtils;

public class TeleportEntry extends AbstractCommonSpawnEntry<TeleportDetailEntry> {

    public TeleportEntry(SpawnShapeType shapeType, Vec3 center, Vec3 dimension,
                         CommonDetailType detailType, TeleportDetailEntry detailEntry) {
        super(shapeType, center, dimension, detailType, detailEntry);
    }

    @Override
    public String getType() {
        return SpawnTypeTag.SPAWN_TYPE_TELEPORT;
    }

    @Override
    public IGameSpawner createGameSpawner() {
        return new TeleportSpawner(shapeType, centerPos, dimension, preZoneId, detailType, detailEntry);
    }

    @Override
    public JsonObject toJson() {
        // common
        JsonObject jsonObject = super.toJson();
        // detail
        jsonObject.addProperty(SpawnDetailTag.TYPE_NAME, detailType.getName());
        this.detailEntry.toJson(jsonObject, this.shapeType, this.detailType);

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
        TeleportDetailEntry detailEntry = TeleportDetailEntry.fromJson(jsonObject, detailType);

        return new TeleportEntry(shapeType, center, dimension,
                detailType,
                detailEntry);
    }
}
