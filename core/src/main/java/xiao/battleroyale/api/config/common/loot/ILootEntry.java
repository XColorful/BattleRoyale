package xiao.battleroyale.api.config.common.loot;

import com.google.gson.JsonObject;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.config.sub.IConfigEntry;
import xiao.battleroyale.api.loot.data.ILootData;
import xiao.battleroyale.common.loot.LootGenerator.LootContext;

import java.util.List;

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

    @Override
    @NotNull
    ILootEntry copy();

    /**
     * 序列化的字符串直接硬编码在具体类里
     * 返回 null 即没有对应的原版战利品词条
     */
    default @Nullable JsonObject toLootTable() {
        return null;
    }
}