package xiao.battleroyale.config.common.loot.type;

import com.google.gson.JsonObject;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.game.zone.shape.IZoneShapeEntry;
import xiao.battleroyale.api.loot.ILootData;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.api.loot.LootEntryTag;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.zone.ZoneManager;
import xiao.battleroyale.common.loot.LootGenerator;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class ShapeEntry extends AbstractLootEntry {

    public boolean invert;
    public IZoneShapeEntry shapeEntry;
    public ILootEntry entry;

    public ShapeEntry(boolean invert, IZoneShapeEntry shapeEntry, ILootEntry entry) {
        this.invert = invert;
        this.shapeEntry = shapeEntry;
        this.entry = entry;
    }

    @Override
    public @NotNull ShapeEntry copy() {
        return new ShapeEntry(invert, shapeEntry.copy(), entry.copy());
    }

    @Override
    public @NotNull <T extends BlockEntity> List<ILootData> generateLootData(LootGenerator.LootContext lootContext, @Nullable T target) {
        BattleRoyale.LOGGER.debug("ShapeEntry generateLootData");
        List<ILootData> lootData = new ArrayList<>();
        if (entry != null) {
            try {
                boolean isInRange = false;
                if (shapeEntry != null) {
                    ZoneManager.ZoneContext zoneContext = ZoneManager.get().getCommonZoneContext();
                    if (zoneContext != null && target != null) {
                        BattleRoyale.LOGGER.debug("zoneContext != null && target != null");
                        ISpatialZone spatialZone = shapeEntry.createSpatialZone();
                        spatialZone.calculateShape(zoneContext);
                        GameManager gameManager = GameManager.get();
                        float gameTime = gameManager.getGameTime();
                        float maxGameTime = gameManager.getMaxGameTime();
                        if (spatialZone.isDetermined()) {
                            BattleRoyale.LOGGER.debug("spatialZone is determined");
                            BattleRoyale.LOGGER.debug("start: {}, {}", spatialZone.getStartCenterPos(), spatialZone.getStartDimension());
                            BattleRoyale.LOGGER.debug("end: {}, {}", spatialZone.getEndCenterPos(), spatialZone.getEndDimension());
                        }
                        BattleRoyale.LOGGER.debug("target: {}", target.getBlockPos().getCenter());
                        if (spatialZone.isWithinZone(target.getBlockPos().getCenter(), gameTime / maxGameTime)) {
                            isInRange = true;
                            BattleRoyale.LOGGER.debug("ShapeEntry: isInRange set to true");
                        }
                    }
                }
                if (isInRange != invert) {
                    lootData.addAll(entry.generateLootData(lootContext, target));
                    BattleRoyale.LOGGER.debug("lootData.addAll");
                }
            } catch (Exception e) {
                parseErrorLog(e, target);
            }
        } else {
            entryErrorLog(target);
        }
        return lootData;
    }

    @Override
    public String getType() {
        return LootEntryTag.TYPE_SHAPE;
    }

    @NotNull
    public static ShapeEntry fromJson(JsonObject jsonObject) {
        boolean invert = JsonUtils.getJsonBool(jsonObject, LootEntryTag.INVERT, false);
        JsonObject shapeEntryObject = JsonUtils.getJsonObject(jsonObject, LootEntryTag.SHAPE_ENTRY, null);
        IZoneShapeEntry shapeEntry = ZoneConfigManager.ZoneConfig.deserializeZoneShapeEntry(shapeEntryObject);
        JsonObject entryObject = JsonUtils.getJsonObject(jsonObject, LootEntryTag.ENTRY, null);
        ILootEntry entry = LootConfigManager.LootConfig.deserializeLootEntry(entryObject);
        return new ShapeEntry(invert, shapeEntry, entry);
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty(LootEntryTag.INVERT, invert);
        if (shapeEntry != null) {
            jsonObject.add(LootEntryTag.SHAPE_ENTRY, shapeEntry.toJson());
        }
        if (entry != null) {
            jsonObject.add(LootEntryTag.ENTRY, entry.toJson());
        }
        return jsonObject;
    }
}
