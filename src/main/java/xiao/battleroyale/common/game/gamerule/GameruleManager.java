package xiao.battleroyale.common.game.gamerule;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.gamerule.storage.McRuleStorage;
import xiao.battleroyale.common.game.gamerule.storage.PlayerModeStorage;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager.GameruleConfig;
import xiao.battleroyale.config.common.game.gamerule.type.MinecraftEntry;
import xiao.battleroyale.util.ChatUtils;

import java.util.List;
import java.util.UUID;

public class GameruleManager extends AbstractGameManager {

    private static GameruleManager instance;

    private final PlayerModeStorage gamemodeBackup = new PlayerModeStorage();
    private final McRuleStorage gameruleBackup = new McRuleStorage();

    private boolean autoSaturation = true;

    private GameruleManager() {
        ;
    }

    public static void init() {
        if (instance == null) {
            instance = new GameruleManager();
        }
    }

    @NotNull
    public static GameruleManager get() {
        if (instance == null) {
            GameruleManager.init();
        }
        return instance;
    }

    @Override
    public void initGameConfig(ServerLevel serverLevel) {
        int gameId = GameManager.get().getGameruleConfigId();
        MinecraftEntry mcEntry = GameruleConfigManager.get().getGameruleConfig(gameId).getMinecraftEntry();;
        if (mcEntry == null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            BattleRoyale.LOGGER.warn("Failed to get MinecraftEntry from GameruleConfig by id: {}", gameId);
            return;
        }
        this.gamemodeBackup.store(mcEntry, serverLevel, GameManager.get().getPlayerList());
        this.gameruleBackup.store(mcEntry, serverLevel, null);
        this.autoSaturation = mcEntry.autoSaturation;

        prepared = true;
    }

    /**
     * 应用配置的游戏规则
     * @param serverLevel 当前 level
     */
    @Override
    public void initGame(ServerLevel serverLevel) {
        List<UUID> playerIdList = GameManager.get().getPlayerList();
        this.gamemodeBackup.apply(serverLevel, playerIdList);

        this.ready = true;
    }

    public void stopGame(ServerLevel serverLevel) {
        gamemodeBackup.revert(serverLevel);
        gameruleBackup.revert(serverLevel);
        this.prepared = false;
        this.ready = false;
    }
}
