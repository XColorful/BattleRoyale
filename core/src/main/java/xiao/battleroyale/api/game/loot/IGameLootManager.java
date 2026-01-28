package xiao.battleroyale.api.game.loot;

import xiao.battleroyale.api.game.IGameSubManager;
import xiao.battleroyale.api.loot.ILootManager;

public interface IGameLootManager extends ILootManager,
        IGameSubManager, IGameLootConfigGetter, IGameLootStatus,
        IGameLootTester, IGameLootOperator {
}
