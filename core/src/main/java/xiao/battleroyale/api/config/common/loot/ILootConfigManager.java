package xiao.battleroyale.api.config.common.loot;

import net.minecraft.world.level.block.entity.BlockEntity;
import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.command.CommandArg;
import xiao.battleroyale.config.common.loot.LootConfigManager;

public interface ILootConfigManager<T extends ILootSingleEntry> extends IConfigSubManager<T> {

    @Override
    default String getNameKey() {
        return CommandArg.LOOT;
    }

    /**
     * 根据刷新实体/方块自身lootId的通用获取接口
     */
    LootConfigManager.LootConfig getLootConfig(BlockEntity be);
}
