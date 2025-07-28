package xiao.battleroyale.developer.debug;

import net.minecraft.commands.CommandSourceStack;
import xiao.battleroyale.common.game.loot.GameLootManager;
import xiao.battleroyale.common.loot.CommonLootManager;
import xiao.battleroyale.developer.debug.text.LootText;

public class DebugLoot {

    private static class DebugLootHolder {
        private static final DebugLoot INSTANCE = new DebugLoot();
    }

    public static DebugLoot get() {
        return DebugLootHolder.INSTANCE;
    }

    private DebugLoot() {
        ;
    }

    /**
     * [调试]getCommonLoot:
     */
    public static final String GET_COMMON_LOOT = "getCommonLoot";
    public void getCommonLoot(CommandSourceStack source) {
        DebugManager.sendDebugMessage(source, GET_COMMON_LOOT, LootText.buildCommonLootText(CommonLootManager.get()));
    }

    /**
     * [调试]getGameLoot:
     */
    public static final String GET_GAME_LOOT = "getGameLoot";
    public void getGameLoot(CommandSourceStack source) {
        DebugManager.sendDebugMessageWithGameTime(source, GET_GAME_LOOT, LootText.buildGameLootText(GameLootManager.get()));
    }
}
