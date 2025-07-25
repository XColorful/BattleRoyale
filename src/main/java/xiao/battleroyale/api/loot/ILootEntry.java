package xiao.battleroyale.api.loot;

import java.util.List;

import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.IConfigEntry;
import xiao.battleroyale.common.loot.LootGenerator.LootContext;

public interface ILootEntry extends IConfigEntry {
    /**
     * 计算物资刷新生成内容
     * @param lootContext 物资刷新环境
     * @param target 方块实体，不应在函数内修改
     */
    @NotNull
    <T extends BlockEntity> List<ILootData> generateLootData(LootContext lootContext, T target);
}