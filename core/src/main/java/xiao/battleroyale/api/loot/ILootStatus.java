package xiao.battleroyale.api.loot;

import net.minecraft.commands.CommandSourceStack;
import xiao.battleroyale.common.loot.LootStatus;

public interface ILootStatus {

    // 非极端情况下可以不考虑先检查后执行，至少等MC服务端做成多线程
    LootStatus lootStatusCheck();
    LootStatus lootStatusCheck(CommandSourceStack source);
}
