package xiao.battleroyale.common.game.zone.tickable.event;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.api.event.game.zone.EntityEvent;
import xiao.battleroyale.api.game.IGameIdWriteApi;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.zone.ZoneManager.ZoneContext;
import xiao.battleroyale.common.game.zone.ZoneManager.ZoneTickContext;
import xiao.battleroyale.common.loot.LootGenerator;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.config.common.loot.LootConfigTypeEnum;
import xiao.battleroyale.event.EventPoster;

import java.util.ArrayList;
import java.util.List;

public class EntityFunc extends AbstractEventFunc {

    protected final int lootId;
    protected @NotNull CompoundTag nbt;
    protected @Nullable LootConfig entityConfig;
    protected List<Entity> lastLootEntities = new ArrayList<>();
    protected final List<Entity> lootEntities = new ArrayList<>();

    public EntityFunc(int moveDelay, int moveTime, int tickFreq, int tickOffset,
                      String protocol, @NotNull CompoundTag tag,
                      int lootId, @NotNull CompoundTag nbt) {
        super(moveDelay, moveTime, tickFreq, tickOffset, protocol, tag);
        this.lootId = lootId;
        this.nbt = nbt;
    }

    @Override
    public void initFunc(ZoneContext zoneContext) {
        IConfigSubManager<?> lootConfigManager = BattleRoyale.getModConfigManager().getConfigSubManager(LootConfigManager.get().getNameKey());

        @Nullable LootConfig entityConfig = lootConfigManager == null ? null
                : lootConfigManager.getConfigEntry(LootConfigTypeEnum.ENTITY_SPAWNER, lootId) instanceof LootConfig config ? config.copy() : null;
        if (entityConfig == null) {
            return;
        }
        this.entityConfig = entityConfig;

        super.initFunc(zoneContext);
    }
    @Override
    public void funcTick(ZoneTickContext zoneTickContext) {
        assert entityConfig != null;

        Vec3 zoneCenter = zoneTickContext.spatialZone.getCenterPos(zoneTickContext.progress);
        if (zoneCenter == null) zoneCenter = Vec3.ZERO;
        LootGenerator.LootContext lootContext = new LootGenerator.LootContext(
                zoneTickContext.serverLevel,
                new ChunkPos(new BlockPos((int) zoneCenter.x, (int) zoneCenter.y, (int) zoneCenter.z)),
                GameManager.get().getGameId()
        );

        lastLootEntities = new ArrayList<>(lootEntities);
        lootEntities.clear();
        lootEntities.addAll(LootGenerator.generateLootEntities(lootContext, entityConfig.entry));

        if (EventPoster.postEvent(new EntityEvent(GameManager.get(), zoneTickContext, protocol, tag,
                lootEntities, lastLootEntities, nbt,
                lootContext, entityConfig.entry))) {
            BattleRoyale.LOGGER.debug("Entity Func canceled");
            return;
        }

        // 集中生成并写入gameId
        IGameIdWriteApi gameIdWriteApi = GameManager.get().getGameIdWriteApi();
        int generatedCount = 0;
        int skippedCount = 0;
        for (Entity entity : lootEntities) {
            if (entity == null) {
                skippedCount++;
                continue;
            }
            gameIdWriteApi.addGameId(entity, lootContext.gameId);
            if (lootContext.serverLevel.addFreshEntity(entity)) {
                generatedCount++;
            }
        }
        BattleRoyale.LOGGER.debug("Entity Func entity generation count: {}, skipped: {}", generatedCount, skippedCount);
    }

    @Override
    public ZoneFuncType getFuncType() {
        return ZoneFuncType.ENTITY;
    }
}
