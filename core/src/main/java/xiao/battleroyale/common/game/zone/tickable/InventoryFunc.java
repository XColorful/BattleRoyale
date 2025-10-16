package xiao.battleroyale.common.game.zone.tickable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.api.loot.ILootEntry;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.zone.ZoneManager.ZoneTickContext;
import xiao.battleroyale.common.loot.InventoryGenerator;
import xiao.battleroyale.common.loot.LootGenerator;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.config.common.loot.LootConfigTypeEnum;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InventoryFunc extends AbstractSimpleFunc {

    public final boolean skipNonEmptySlot;
    public final boolean dropBeforeReplace;
    public final int firstSlotIndex;
    public final int lastSlotIndex;
    public final @Nullable ILootEntry lootEntry;
    public final int lootSpawnerLootId;

    public InventoryFunc(int moveDelay, int moveTime, int tickFreq, int tickOffset,
                         boolean skipNonEmptySlot, boolean dropBeforeReplace, int firstSlotIndex, int lastSlotIndex,
                         @Nullable ILootEntry lootEntry, int lootSpawnerLootId) {
        super(moveDelay, moveTime, tickFreq, tickOffset);
        this.skipNonEmptySlot = skipNonEmptySlot;
        this.dropBeforeReplace = dropBeforeReplace;
        this.firstSlotIndex = firstSlotIndex;
        this.lastSlotIndex = lastSlotIndex;
        this.lootEntry = lootEntry;
        this.lootSpawnerLootId = lootSpawnerLootId;
    }

    @Override
    public void funcTick(ZoneTickContext zoneTickContext) {
        ServerLevel serverLevel = zoneTickContext.serverLevel;
        UUID gameId = GameManager.get().getGameId();
        List<ItemStack> lootItems = new ArrayList<>();
        IConfigSubManager<?> lootConfigManager = BattleRoyale.getModConfigManager().getConfigSubManager(LootConfigManager.get().getNameKey());
        @Nullable LootConfig lootConfig = lootConfigManager == null ? null
                : lootConfigManager.getConfigEntry(LootConfigTypeEnum.LOOT_SPAWNER, this.lootSpawnerLootId) instanceof LootConfig config ? config : null;
        @Nullable ILootEntry additionalLootEntry = lootConfig != null ? lootConfig.entry : null;

        for (GamePlayer gamePlayer : zoneTickContext.gamePlayers) {
            if (zoneTickContext.spatialZone.isWithinZone(gamePlayer.getLastPos(), zoneTickContext.progress)) {

                LivingEntity livingEntity = (LivingEntity) zoneTickContext.serverLevel.getEntity(gamePlayer.getPlayerUUID());
                if (livingEntity != null) {
                    Vec3 playerLastPos = gamePlayer.getLastPos();
                    ChunkPos chunkPos = new ChunkPos(new BlockPos((int) playerLastPos.x, (int) playerLastPos.y, (int) playerLastPos.z));
                    LootGenerator.LootContext lootContext = new LootGenerator.LootContext(serverLevel, chunkPos, gameId);

                    lootItems.clear();
                    // 先刷写在区域配置里的
                    if (this.lootEntry != null) {
                        lootItems = LootGenerator.generateLootItem(lootContext, this.lootEntry);
                    }
                    BattleRoyale.LOGGER.debug("lootItems.size()={}", lootItems.size());
                    // 再刷引用物资刷新器的
                    if (additionalLootEntry != null) {
                        lootItems.addAll(LootGenerator.generateLootItem(lootContext, additionalLootEntry));
                    }
                    BattleRoyale.LOGGER.debug("lootItems.size()={}", lootItems.size());
                    for (ItemStack itemStack : lootItems) {
                        BattleRoyale.LOGGER.debug(itemStack.toString());
                    }

                    if (!gamePlayer.isBot() && livingEntity instanceof ServerPlayer player) { // 直接进背包
                        InventoryGenerator.lootItemsToPlayerInventory(player, lootItems, firstSlotIndex, lastSlotIndex, skipNonEmptySlot, dropBeforeReplace);
                    } else { // 丢地上
                        // TODO 对于没背包的或者人机, 刷新的背包物品丢在lastPos
                    }
                }
            }
        }
    }

    @Override
    public ZoneFuncType getFuncType() {
        return ZoneFuncType.INVENTORY;
    }
}
