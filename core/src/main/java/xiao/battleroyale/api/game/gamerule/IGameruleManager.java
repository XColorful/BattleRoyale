package xiao.battleroyale.api.game.gamerule;

import net.minecraft.world.level.GameType;
import xiao.battleroyale.api.game.IGameSubManager;

public interface IGameruleManager extends IGameSubManager {

    GameType getGameMode();
}
