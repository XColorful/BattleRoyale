package xiao.battleroyale.api.game.gamerule;

import xiao.battleroyale.api.config.IConfigSingleEntry;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.config.common.game.gamerule.type.GameEntry;
import xiao.battleroyale.config.common.game.gamerule.type.MinecraftEntry;

public interface IGameruleSingleEntry extends IConfigSingleEntry {

    BattleroyaleEntry getBattleRoyaleEntry();

    MinecraftEntry getMinecraftEntry();

    GameEntry getGameEntry();
}
