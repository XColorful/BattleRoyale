package xiao.battleroyale.common.game.gamerule;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.GameStatsManager;
import xiao.battleroyale.common.game.GameTeamManager;
import xiao.battleroyale.common.game.gamerule.storage.McRuleStorage;
import xiao.battleroyale.common.game.gamerule.storage.PlayerModeStorage;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager.GameruleConfig;
import xiao.battleroyale.config.common.game.gamerule.type.GameEntry;
import xiao.battleroyale.config.common.game.gamerule.type.MinecraftEntry;
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.GameUtils;

import java.util.List;

public class GameruleManager extends AbstractGameManager {

    private static class GameruleManagerHolder {
        private static final GameruleManager INSTANCE = new GameruleManager();
    }

    public static GameruleManager get() {
        return GameruleManagerHolder.INSTANCE;
    }

    private GameruleManager() {}

    public static void init() {
        ;
    }

    private MinecraftEntry mcEntry;
    private final PlayerModeStorage gamemodeBackup = new PlayerModeStorage();
    private final McRuleStorage gameruleBackup = new McRuleStorage();

    private boolean autoSaturation = true;

    @Override
    public void initGameConfig(ServerLevel serverLevel) {
        if (GameManager.get().isInGame()) {
            return;
        }

        int gameId = GameManager.get().getGameruleConfigId();
        GameruleConfig gameruleConfig = GameConfigManager.get().getGameruleConfig(gameId);
        if (gameruleConfig == null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            return;
        }
        MinecraftEntry mcEntry = gameruleConfig.getMinecraftEntry();;
        GameEntry gameEntry = gameruleConfig.getGameEntry();
        if (mcEntry == null || gameEntry == null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            BattleRoyale.LOGGER.warn("Failed to get MinecraftEntry or GameEntry from GameruleConfig by id: {}", gameId);
            return;
        }
        this.mcEntry = mcEntry;
        this.gameruleBackup.store(mcEntry, serverLevel, null);
        this.autoSaturation = mcEntry.autoSaturation;

        configPrepared = true;
        BattleRoyale.LOGGER.debug("GameruleManager complete initGameConfig");
    }

    @Override
    public void initGame(ServerLevel serverLevel) {
        if (GameManager.get().isInGame()) {
            return;
        }
        if (!this.configPrepared) {
            return;
        }

        List<GamePlayer> gamePlayerList = GameTeamManager.getGamePlayers();
        this.gameruleBackup.apply(serverLevel, gamePlayerList);
        GameStatsManager.recordGamerule(this.gameruleBackup);

        this.ready = true;
        this.configPrepared = false;
        BattleRoyale.LOGGER.debug("GameruleManager complete initGame");
    }

    @Override
    public boolean startGame(ServerLevel serverLevel) {
        if (GameManager.get().isInGame()) {
            return false;
        }

        List<GamePlayer> gamePlayerList = GameTeamManager.getStandingGamePlayers();
        this.gamemodeBackup.clear();
        this.gamemodeBackup.store(mcEntry, serverLevel, gamePlayerList);
        this.gamemodeBackup.apply(serverLevel, gamePlayerList);
        GameStatsManager.recordGamerule(this.gamemodeBackup);
        if (mcEntry.clearInventory) {
            GameUtils.clearGamePlayersInventory(serverLevel, gamePlayerList);
        }
        return true;
    }

    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        if (serverLevel != null) {
            gamemodeBackup.revert(serverLevel);
            gameruleBackup.revert(serverLevel);
        }
        this.configPrepared = false;
        this.ready = false;
    }

    @Override
    public void onGameTick(int gameTime) {
        if (autoSaturation && gameTime % 200 == 0) {
            ServerLevel serverLevel = GameManager.get().getServerLevel();
            if (serverLevel == null) {
                return;
            }
            for (GamePlayer gamePlayer : GameTeamManager.getStandingGamePlayers()) {
                if (!gamePlayer.isActiveEntity()) {
                    continue;
                }
                ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
                if (player == null) {
                    continue;
                }
                player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 200, 0, false, false));
            }
        }
    }

    public GameType getGameMode() { return gamemodeBackup.getGameMode(); }
}
