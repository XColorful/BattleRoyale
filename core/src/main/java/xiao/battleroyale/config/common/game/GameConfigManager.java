package xiao.battleroyale.config.common.game;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.command.CommandArg;
import xiao.battleroyale.config.AbstractConfigManager;
import xiao.battleroyale.config.ModConfigManager;
import xiao.battleroyale.config.common.game.bot.BotConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager;

import java.nio.file.Paths;

public class GameConfigManager extends AbstractConfigManager {

    public static final String GAME_CONFIG_SUB_PATH = "game";
    public static final String GAME_CONFIG_PATH = Paths.get(ModConfigManager.MOD_CONFIG_PATH).resolve(GAME_CONFIG_SUB_PATH).toString();

    private static class GameConfigManagerHolder {
        private static final GameConfigManager INSTANCE = new GameConfigManager();
    }

    public static GameConfigManager get() {
        return GameConfigManagerHolder.INSTANCE;
    }

    private GameConfigManager() {
        super(CommandArg.GAME);
    }

    public static void init(McSide mcSide) {
        if (!get().inProperSide(mcSide)) {
            return;
        }
        BattleRoyale.getModConfigManager().registerConfigManager(get());
        BotConfigManager.init();
        GameruleConfigManager.init();
        SpawnConfigManager.init();
        ZoneConfigManager.init();
    }
}