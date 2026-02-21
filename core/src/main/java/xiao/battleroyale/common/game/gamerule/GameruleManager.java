package xiao.battleroyale.common.game.gamerule;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.api.config.IModConfigManager;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ICustomEvent;
import xiao.battleroyale.api.event.ICustomEventHandler;
import xiao.battleroyale.api.event.ICustomEventRegister;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.game.gamerule.IGameruleManager;
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.common.game.GameStatsManager;
import xiao.battleroyale.common.game.GameTeamManager;
import xiao.battleroyale.common.game.gamerule.storage.McRuleStorage;
import xiao.battleroyale.common.game.gamerule.storage.PlayerModeStorage;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager.GameruleConfig;
import xiao.battleroyale.config.common.game.gamerule.type.GameEntry;
import xiao.battleroyale.config.common.game.gamerule.type.MinecraftEntry;
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.GameUtils;

import java.util.List;

public class GameruleManager extends AbstractGameManager implements IGameruleManager, ICustomEventHandler {

    private static class GameruleManagerHolder {
        private static final GameruleManager INSTANCE = new GameruleManager();
    }

    public static GameruleManager get() {
        return GameruleManagerHolder.INSTANCE;
    }

    protected GameruleManager() {}

    public static void init(McSide mcSide) {
    }

    @Override public String getManagerName() {
        return String.format("%s:GameruleManager", BattleRoyale.MOD_ID);
    }

    protected MinecraftEntry mcEntry;
    protected final PlayerModeStorage gamemodeBackup = new PlayerModeStorage();
    protected final McRuleStorage gameruleBackup = new McRuleStorage();

    protected boolean autoSaturation = true;

    @Override
    public boolean registerGameEventHandler() {
        ICustomEventRegister customEventRegister = BattleRoyale.getEventRegister();
        customEventRegister.register(get(), CustomEventType.GAME_LOAD_FINISH_EVENT);
        customEventRegister.register(get(), CustomEventType.GAME_START_FINISH_EVENT);
        customEventRegister.register(get(), CustomEventType.GAME_STOP_FINISH_EVENT);
        return true;
    }
    @Override
    public boolean unregisterGameEventHandler() {
        ICustomEventRegister customEventRegister = BattleRoyale.getEventRegister();
        customEventRegister.unregister(get(), CustomEventType.GAME_LOAD_FINISH_EVENT);
        customEventRegister.unregister(get(), CustomEventType.GAME_START_FINISH_EVENT);
        customEventRegister.unregister(get(), CustomEventType.GAME_STOP_FINISH_EVENT);
        LogEventHandler.unregister();
        AttackEventHandler.unregister();
        return true;
    }
    @Override
    public String getEventHandlerName() {
        return String.format("%s:GameruleManager", BattleRoyale.MOD_ID);
    }
    @Override
    public void handleEvent(CustomEventType customEventType, ICustomEvent event) {
        switch (customEventType) {
            case GAME_LOAD_FINISH_EVENT -> {
                LogEventHandler.register(); // 后续玩家登录可根据配置直接加入队伍
            }
            case GAME_START_FINISH_EVENT -> {
                AttackEventHandler.register();
            }
            case GAME_STOP_FINISH_EVENT -> {
                LogEventHandler.unregister();
                AttackEventHandler.unregister();
            }
            default -> {
                onReceiveWrongEvent(customEventType);
            }
        }
    }

    @Override
    public void initGameConfig(ServerLevel serverLevel) {
        IGameManager gameManager = BattleRoyale.getGameManager();
        if (gameManager.isInGame()) return;

        IModConfigManager modConfigManager = BattleRoyale.getModConfigManager();
        IConfigSubManager<?> gameruleConfigManager = modConfigManager.getConfigSubManager(GameConfigManager.get().getNameKey(), GameruleConfigManager.get().getNameKey());
        int configId = gameManager.getGameruleConfigId();
        if (gameruleConfigManager == null || !(gameruleConfigManager.getConfigEntry(configId) instanceof GameruleConfig gameruleConfig)) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            return;
        }
        MinecraftEntry mcEntry = gameruleConfig.getMinecraftEntry();
        GameEntry gameEntry = gameruleConfig.getGameEntry();
        if (mcEntry == null || gameEntry == null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            BattleRoyale.LOGGER.warn("Failed to get MinecraftEntry or GameEntry from GameruleConfig by id: {}", configId);
            return;
        }
        mcEntry = mcEntry.copy();
        this.mcEntry = mcEntry;
        this.gameruleBackup.store(mcEntry, serverLevel, null);
        this.autoSaturation = mcEntry.autoSaturation;

        configPrepared = true;
        BattleRoyale.LOGGER.debug("GameruleManager complete initGameConfig");
    }

    @Override
    public void initGame(ServerLevel serverLevel) {
        if (BattleRoyale.getGameManager().isInGame()) {
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
        if (BattleRoyale.getGameManager().isInGame()) {
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
            ServerLevel serverLevel = BattleRoyale.getGameManager().getServerLevel();
            if (serverLevel == null) {
                return;
            }
            for (GamePlayer gamePlayer : GameTeamManager.getStandingGamePlayers()) {
                if (!gamePlayer.isActiveEntity()) {
                    continue;
                }
                @Nullable ServerPlayer player = GameUtils.getServerPlayerOrNull(serverLevel, gamePlayer.getPlayerUUID());
                if (player == null) {
                    continue;
                }
                player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 200, 0, false, false));
            }
        }
    }

    @Override public GameType getGameMode() { return gamemodeBackup.getGameMode(); }
}
