package xiao.battleroyale.api.loot;

import java.util.List;

import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.IConfigEntry;
import xiao.battleroyale.common.loot.LootGenerator.LootContext;

public interface ILootEntry extends IConfigEntry {

    default
    @NotNull
    List<ILootData> generateLootData(LootContext lootContext) {
        return generateLootData(lootContext, null);
    }

    /**
     * 计算物资刷新生成内容
     * @param lootContext 物资刷新环境
     * @param target 方块实体，不应在函数内修改
     */
    @NotNull
    <T extends BlockEntity> List<ILootData> generateLootData(LootContext lootContext, @Nullable T target);

    default
    <T extends BlockEntity> void entryErrorLog(@Nullable T target) {
        if (target != null) {
            BattleRoyale.LOGGER.warn("{} entry missing invalid entry member, skipped at {}", this.getType(), target.getBlockPos());
        } else {
            BattleRoyale.LOGGER.warn("{} entry missing invalid entry member", this.getType());
        }
    }

    default
    <T extends BlockEntity> void parseErrorLog(Exception e, @Nullable T target) {
        if (target != null) {
            BattleRoyale.LOGGER.warn("Failed to parse {} entry, skipped at {}", this.getType(), target.getBlockPos(), e);
        } else {
            BattleRoyale.LOGGER.warn("Failed to parse {} entry", this.getType());
        }
    }
}