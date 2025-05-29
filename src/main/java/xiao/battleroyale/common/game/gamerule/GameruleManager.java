package xiao.battleroyale.common.game.gamerule;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.gamerule.storage.McRuleStorage;
import xiao.battleroyale.common.game.gamerule.storage.PlayerModeStorage;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.type.MinecraftEntry;
import xiao.battleroyale.util.ChatUtils;

import java.util.List;

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
        if (GameManager.get().isInGame()) {
            return;
        }

        int gameId = GameManager.get().getGameruleConfigId();
        MinecraftEntry mcEntry = GameruleConfigManager.get().getGameruleConfig(gameId).getMinecraftEntry();;
        if (mcEntry == null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            BattleRoyale.LOGGER.warn("Failed to get MinecraftEntry from GameruleConfig by id: {}", gameId);
            return;
        }
        this.gamemodeBackup.store(mcEntry, serverLevel, GameManager.get().getGamePlayers());
        this.gameruleBackup.store(mcEntry, serverLevel, null);
        this.autoSaturation = mcEntry.autoSaturation;

        prepared = true;
    }

    @Override
    public void initGame(ServerLevel serverLevel) {
        if (GameManager.get().isInGame()) {
            return;
        }
        if (!this.prepared) {
            return;
        }

        List<GamePlayer> gamePlayerList = GameManager.get().getGamePlayers();
        this.gamemodeBackup.apply(serverLevel, gamePlayerList);

        this.ready = true;
    }

    @Override
    public boolean startGame(ServerLevel serverLevel) {
        if (GameManager.get().isInGame()) {
            return false;
        }

        return true;
    }

    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        if (serverLevel != null) {
            gamemodeBackup.revert(serverLevel);
            gameruleBackup.revert(serverLevel);
        }
        this.prepared = false;
        this.ready = false;
    }
}
