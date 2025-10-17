package xiao.battleroyale.common.game.zone.tickable.event;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.api.event.game.zone.AirdropEvent;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.zone.ZoneManager.ZoneContext;
import xiao.battleroyale.common.game.zone.ZoneManager.ZoneTickContext;
import xiao.battleroyale.common.loot.LootGenerator;
import xiao.battleroyale.common.loot.LootGenerator.LootContext;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager.LootConfig;
import xiao.battleroyale.config.common.loot.LootConfigTypeEnum;
import xiao.battleroyale.event.EventPoster;

import java.util.ArrayList;
import java.util.List;

public class AirdropFunc extends AbstractEventFunc {

    protected final int lootId;
    protected @NotNull CompoundTag nbt;
    protected @Nullable LootConfig airdropConfig;
    protected List<ItemStack> lastLootItems = new ArrayList<>();
    protected final List<ItemStack> lootItems = new ArrayList<>();

    public AirdropFunc(int moveDelay, int moveTime, int tickFreq, int tickOffset,
                       String protocol, @NotNull CompoundTag tag,
                       int lootId, @NotNull CompoundTag nbt) {
        super(moveDelay, moveTime, tickFreq, tickOffset, protocol, tag);
        this.lootId = lootId;
        this.nbt = nbt;
    }

    @Override
    public void initFunc(ZoneContext zoneContext) {
        IConfigSubManager<?> lootConfigManager = BattleRoyale.getModConfigManager().getConfigSubManager(LootConfigManager.get().getNameKey());

        @Nullable LootConfig airdropConfig = lootConfigManager == null ? null
                : lootConfigManager.getConfigEntry(LootConfigTypeEnum.AIRDROP, lootId) instanceof LootConfig config ? config.copy() : null;
        if (airdropConfig == null) { // 没有物资刷新配置就视为创建失败
            return;
        }
        this.airdropConfig = airdropConfig;

        super.initFunc(zoneContext);
    }
    @Override
    public void funcTick(ZoneTickContext zoneTickContext) {
        assert airdropConfig != null; // 在initFunc里提前返回了

        Vec3 zoneCenter = zoneTickContext.spatialZone.getCenterPos(zoneTickContext.progress);
        if (zoneCenter == null) zoneCenter = Vec3.ZERO;
        LootContext lootContext = new LootContext(
                zoneTickContext.serverLevel,
                new ChunkPos(new BlockPos((int) zoneCenter.x, (int) zoneCenter.y, (int) zoneCenter.z)),
                GameManager.get().getGameId()
        );

        lastLootItems = new ArrayList<>(lootItems);
        lootItems.clear();
        lootItems.addAll(LootGenerator.generateLootItem(lootContext, airdropConfig.entry));

        EventPoster.postEvent(new AirdropEvent(GameManager.get(), zoneTickContext, protocol, tag,
                lootItems, lastLootItems, nbt,
                lootContext, airdropConfig.entry));
    }

    @Override
    public ZoneFuncType getFuncType() {
        return ZoneFuncType.AIRDROP;
    }
}
