package xiao.battleroyale.common.game;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.stats.IStatsWriter;
import xiao.battleroyale.command.sub.GameCommand;
import xiao.battleroyale.common.effect.EffectManager;
import xiao.battleroyale.common.game.gamerule.GameruleManager;
import xiao.battleroyale.common.game.loot.GameLootManager;
import xiao.battleroyale.common.game.spawn.SpawnManager;
import xiao.battleroyale.common.game.stats.StatsManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.common.game.team.TeamManager;
import xiao.battleroyale.common.game.tempdata.TempDataManager;
import xiao.battleroyale.common.game.zone.ZoneManager;
import xiao.battleroyale.common.message.MessageManager;
import xiao.battleroyale.common.message.game.GameMessageManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.bot.BotConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager.GameruleConfig;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.config.common.game.gamerule.type.GameEntry;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager;
import xiao.battleroyale.event.game.*;
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.ColorUtils;
import xiao.battleroyale.util.StringUtils;
import xiao.battleroyale.util.Vec3Utils;

import java.util.*;
import java.util.List;
import java.util.function.Supplier;

import static xiao.battleroyale.api.game.tempdata.TempDataTag.*;

public class GameManager extends AbstractGameManager {

    private static class GameManagerHolder {
        private static final GameManager INSTANCE = new GameManager();
    }

    public static GameManager get() {
        return GameManagerHolder.INSTANCE;
    }

    private GameManager() {
        // 恢复全局偏移
        String offsetString = TempDataManager.get().getString(GAME_MANAGER, GLOBAL_OFFSET);
        if (offsetString != null) {
            Vec3 offset = StringUtils.parseVectorString(offsetString);
            if (offset != null) {
                setGlobalCenterOffset(offset);
            }
        }
    }

    public static void init() {
        GameruleManager.init();
        GameLootManager.init();
        SpawnManager.init();
        StatsManager.init();
        TeamManager.init();
        ZoneManager.init();
    }

    private int gameTime = 0; // 游戏运行时维护当前游戏时间
    public int getGameTime() { return this.gameTime; }
    private UUID gameId;
    private boolean inGame;
    private ResourceKey<Level> gameDimensionKey;
    private ServerLevel serverLevel;
    private final Set<GameTeam> winnerGameTeams = new HashSet<>();
    private final Set<GamePlayer> winnerGamePlayers = new HashSet<>();

    // config
    private int gameruleConfigId = 0;
    private int spawnConfigId = 0;
    private int botConfigId = 0;
    private Vec3 globalCenterOffset = Vec3.ZERO;
    public Vec3 getGlobalCenterOffset() { return globalCenterOffset; }
    public boolean setGlobalCenterOffset(Vec3 offset) {
        if (isInGame()) {
            return false;
        }
        globalCenterOffset = offset;
        TempDataManager.get().writeString(GAME_MANAGER, GLOBAL_OFFSET, StringUtils.vectorToString(globalCenterOffset));
        return true;
    }

    private int maxGameTime;
    private GameEntry gameEntry;
    public GameEntry getGameEntry() { return gameEntry; }

    @NotNull
    public UUID getGameId() {
        if (this.gameId == null) {
            generateGameId();
        }
        return this.gameId;
    }

    private void generateGameId() {
        setGameId(UUID.randomUUID());
    }

    public void setGameId(UUID gameId) {
        if (isInGame()) {
            return;
        }
        this.gameId = gameId;
    }

    public boolean isInGame() {
        return inGame;
    }

    /**
     * 检测并加载游戏配置，不应该执行任何实际内容
     */
    public void initGameConfig(ServerLevel serverLevel) {
        if (isInGame()) {
            return;
        }
        this.serverLevel = serverLevel;

        if (!initGameConfigSetup()) {
            return;
        }
        initGameConfigSubManager();

        if (gameConfigAllReady()) {
            this.prepared = true;
            LogEventHandler.register(); // 后续玩家登录可根据配置直接加入队伍
        } else {
            this.prepared = false;
        }
    }

    /**
     * 准备游戏，将玩家传送至大厅等
     * @param serverLevel 当前 level
     */
    @Override
    public void initGame(ServerLevel serverLevel) {
        if (isInGame()) {
            return;
        }

        if (!prepared || this.serverLevel != serverLevel) {
            initGameConfig(serverLevel);
            if (!prepared) {
                return;
            }
        }

        initGameSetup();
        initGameSubManager();
        if (isReady()) {
            generateGameId(); // 手动刷新 gameId
        }
    }

    /**
     * 开始游戏，需要在开始瞬间进行额外判定
     */
    @Override
    public boolean startGame(ServerLevel serverLevel) {
        if (isInGame()) {
            return false;
        }
        if (!isStartReady() || this.serverLevel != serverLevel) {  // Team会变动，用isStartReady
            initGame(serverLevel);
            if (!isStartReady()) {
                return false;
            }
        }

        checkAndUpdateInvalidGamePlayer(this.serverLevel); // 供gameTime = 1时使用
        if (startGameSubManager()) {
            startGameSetup();
            return true;
        } else {
            stopGame(this.serverLevel);
            return false;
        }
    }

    /**
     * 外部调用接口
     */
    public void onGameTick() {
        if (this.serverLevel == null) { // 当前level未加载或者超过最大时长
            BattleRoyale.LOGGER.warn("GameManager cached serverLevel is null, stopped game");
            stopGame(null);
        }

        this.gameTime++;
        if (this.gameTime > this.maxGameTime) { // 超过最大游戏时长
            stopGame(this.serverLevel);
            ChatUtils.sendTranslatableMessageToAllPlayers(this.serverLevel, Component.translatable("battleroyale.message.reach_max_game_time").withStyle(ChatFormatting.GRAY));
            BattleRoyale.LOGGER.info("Reached max game time ({}) and force stopped", this.maxGameTime);
        }
        onGameTick(this.gameTime);
    }

    /**
     * 游戏主逻辑，调度各 Manager，向客户端通信
     */
    public void onGameTick(int gameTime) {
        checkAndUpdateInvalidGamePlayer(this.serverLevel); // 为其他Manager预处理当前tick

        GameLootManager.get().onGameTick(gameTime);
        ZoneManager.get().onGameTick(gameTime);

        TeamManager.get().onGameTick(gameTime);
        GameruleManager.get().onGameTick(gameTime);
        SpawnManager.get().onGameTick(gameTime);
        // StatsManager.get().onGameTick(gameTime); // 基于事件主动记录，不用tick
    }

    /**
     * 检查所有未淘汰玩家是否在线，更新不在线时长或更新最后有效位置
     * 检查队伍成员是否均为倒地或者不在线，淘汰队伍（所有成员）
     */
    private void checkAndUpdateInvalidGamePlayer(ServerLevel serverLevel) {
        if (serverLevel == null) {
            return;
        }

        List<GamePlayer> invalidPlayers = new ArrayList<>();
        // 筛选并增加无效时间计数
        for (GamePlayer gamePlayer : getStandingGamePlayers()) {
            if (!gamePlayer.isBot()) { // 真人玩家
                updateInvalidServerPlayer(gamePlayer, serverLevel, invalidPlayers);
            } else { // 人机
                updateInvalidBotPlayer(gamePlayer, serverLevel, invalidPlayers);
            }
        }

        // 清理无效玩家
        if (!invalidPlayers.isEmpty()) {
            for (GamePlayer invalidPlayer : invalidPlayers) {
                if (TeamManager.get().forceEliminatePlayerSilence(invalidPlayer)) { // 强制淘汰了玩家，不一定都在此处淘汰
                    ChatUtils.sendTranslatableMessageToAllPlayers(this.serverLevel, Component.translatable("battleroyale.message.eliminated_invalid_player", invalidPlayer.getPlayerName()).withStyle(ChatFormatting.GRAY));
                    BattleRoyale.LOGGER.info("Force eliminated GamePlayer {} (UUID: {})", invalidPlayer.getPlayerName(), invalidPlayer.getPlayerUUID());
                }
            }
        }
    }
    private void updateInvalidServerPlayer(@NotNull GamePlayer gamePlayer, @NotNull ServerLevel serverLevel, List<GamePlayer> invalidPlayers) {
        ServerPlayer serverPlayer = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
        if (serverPlayer == null) { // 不在线或者不在游戏运行的 level
            if (gamePlayer.isActiveEntity()) {
                notifyGamePlayerIsInactive(gamePlayer);
            }
            gamePlayer.setActiveEntity(false);
            gamePlayer.addInvalidTime();
            if (eliminateInactiveTeam(gamePlayer)) { // 队伍全员离线
                return;
            } else if (gamePlayer.getInvalidTime() >= gameEntry.maxPlayerInvalidTime) { // 达到允许的最大离线时间
                invalidPlayers.add(gamePlayer); // 淘汰单个离线玩家
            }
        } else { // 更新最后有效位置
            if (!gamePlayer.isActiveEntity()) { // 刚上线
                notifyGamePlayerIsActive(gamePlayer);
                float lastHealth = gamePlayer.getLastHealth();
                if (lastHealth <= 0) {
                    invalidPlayers.add(gamePlayer);
                    return;
                }
                // TODO GamePlayer health 和 absorptionAmount 处理
                serverPlayer.setHealth(lastHealth); // 不用maxHealth检查，可能包含吸收血量
            }
            gamePlayer.setActiveEntity(true);
            gamePlayer.setLastHealth(serverPlayer.getHealth());
            gamePlayer.setLastPos(serverPlayer.position());
        }
    }
    private void updateInvalidBotPlayer(@NotNull GamePlayer gamePlayer, @NotNull ServerLevel serverLevel, List<GamePlayer> invalidPlayers) {
        Entity entity = serverLevel.getEntity(gamePlayer.getPlayerUUID());
        if (!(entity instanceof LivingEntity livingEntity)) {
            if (gamePlayer.isActiveEntity()) {
                notifyGamePlayerIsInactive(gamePlayer);
            }
            gamePlayer.setActiveEntity(false);
            gamePlayer.addInvalidTime();
            if (eliminateInactiveTeam(gamePlayer)) { // 队伍全员离线啊
                return;
            } else if (gamePlayer.getInvalidTime() >= gameEntry.maxBotInvalidTime) {
                invalidPlayers.add(gamePlayer); // 淘汰单个人机
            }
        } else {
            if (!gamePlayer.isActiveEntity()) { // 刚上线
                notifyGamePlayerIsActive(gamePlayer);
                float lastHealth = gamePlayer.getLastHealth();
                if (lastHealth <= 0) {
                    invalidPlayers.add(gamePlayer);
                }
                livingEntity.setHealth(lastHealth);
            }
            gamePlayer.setActiveEntity(true);
            gamePlayer.setLastHealth(livingEntity.getHealth());
            gamePlayer.setLastPos(livingEntity.position());
        }
    }
    private void notifyGamePlayerIsInactive(GamePlayer gamePlayer) {
        ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.player_leaved_from_level", gamePlayer.getPlayerName()).withStyle(ChatFormatting.DARK_GRAY));
    }
    private void notifyGamePlayerIsActive(GamePlayer gamePlayer) {
        ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.player_backed_to_level", gamePlayer.getPlayerName()).withStyle(ChatFormatting.DARK_GRAY));
    }

    /**
     * 检查是否只有倒地或不在线玩家，逐个淘汰
     * 默认不开启，以防玩家倒地的时候队友离线导致 kibo 破灭
     */
    private boolean eliminateInactiveTeam(GamePlayer invalidPlayer) {
        if (!gameEntry.removeInvalidTeam) {
            return false;
        }
        GameTeam gameTeam = invalidPlayer.getTeam();
        for (GamePlayer teamMember : gameTeam.getTeamMembers()) {
            if (teamMember.isActiveEntity() || teamMember.isAlive()) { // 有在线的未倒地玩家
                return false;
            }
        }
        for (GamePlayer teamMember : gameTeam.getTeamMembers()) {
            if (TeamManager.get().forceEliminatePlayerSilence(teamMember)) {
                ChatUtils.sendTranslatableMessageToAllPlayers(this.serverLevel, Component.translatable("battleroyale.message.eliminated_invalid_player", teamMember.getPlayerName()).withStyle(ChatFormatting.GRAY));
                BattleRoyale.LOGGER.info("Force eliminated GamePlayer {} (UUID: {}) for inactive team", invalidPlayer.getPlayerName(), invalidPlayer.getPlayerUUID());
            }
        }
        return true;
    }

    @Nullable
    public ServerLevel getServerLevel() {
        return this.serverLevel;
    }

    /**
     * 完整检查所有队伍情况，淘汰无在线玩家的队伍
     * 调用此方法将检查是否有胜利队伍
     */
    public void checkIfGameShouldEnd() {
        if (!isInGame()) {
            return;
        }

        checkAndUpdateInvalidGamePlayer(this.serverLevel);
        if (!gameEntry.allowRemainingBot) { // 不允许只剩人机继续打架，即提前终止游戏
            int playerTeamCount = TeamManager.get().getStandingPlayerTeamCount();
            if (playerTeamCount > 0) {
                return;
            }
            boolean hasWinnerBotTeam = TeamManager.get().getStandingTeamCount() <= 1;
            finishGame(hasWinnerBotTeam);
        } else if (TeamManager.get().getStandingTeamCount() <= 1) {
            finishGame(true);
        }
    }

    /**
     * 结束游戏，使所有未淘汰玩家均胜利
     */
    public void finishGame(boolean hasWinner) {
        if (hasWinner) {
            for (GamePlayer gamePlayer : getStandingGamePlayers()) {
                winnerGamePlayers.add(gamePlayer);
                winnerGameTeams.add(gamePlayer.getTeam());
                notifyWinner(gamePlayer);
            }
        }
        stopGame(this.serverLevel);
    }

    /**
     * 大吉大利！今晚吃鸡！
     * 附加烟花，粒子效果（人机不触发）
     */
    private void notifyWinner(@NotNull GamePlayer gamePlayer) {
        ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
        if (player == null) {
            BattleRoyale.LOGGER.info("Skipped to notify winner game player {}", gamePlayer.getPlayerName());
            return;
        }
        int teamId = gamePlayer.getGameTeamId();
        String colorString = gamePlayer.getGameTeamColor();
        int colorRGB = ColorUtils.parseColorFromString(colorString).getRGB();
        TextColor textColor = TextColor.fromRgb(colorRGB);

        Component winnerTitle = Component.translatable("battleroyale.message.winner_message")
                .withStyle(ChatFormatting.GOLD);

        Component teamWinMessage = Component.translatable("battleroyale.message.team", teamId)
                .withStyle(Style.EMPTY.withColor(textColor))
                .append(Component.literal(" "))
                .append(Component.translatable("battleroyale.message.has_won_the_game")
                        .withStyle(ChatFormatting.WHITE));

        ChatUtils.sendTitleToPlayer(player, winnerTitle, teamWinMessage, 10, 80, 20);

        // 暂时硬编码
        EffectManager.get().spawnPlayerFirework(player, 16, 4, 1.0F, 16.0F);
        EffectManager.get().addGameParticle(serverLevel, player.position(), gameEntry.winnerParticleId, 0);
    }

    /**
     * 用于向胜利玩家发送消息，传送回大厅
     */
    public void sendLobbyTeleportMessage(@NotNull ServerPlayer player, boolean isWinner) {
        String toLobbyCommandString = GameCommand.toLobbyCommandString();

        Component fullMessage = Component.translatable("battleroyale.message.back_to_lobby")
                .append(Component.literal(" "))
                .append(Component.translatable("battleroyale.message.teleport")
                        .withStyle(isWinner ? ChatFormatting.GOLD :  ChatFormatting.WHITE)
                        .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, toLobbyCommandString))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(toLobbyCommandString)))
                        )
                );

        ChatUtils.sendTranslatableMessageToPlayer(player, fullMessage);
    }

    private void teleportAfterGame() {
        if (serverLevel != null) {
            // 胜利玩家
            for (GamePlayer winnerGamePlayer : winnerGamePlayers) {
                ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(winnerGamePlayer.getPlayerUUID());
                if (player == null) {
                    continue;
                }

                if (gameEntry.teleportWinnerAfterGame) { // 传送
                    teleportToLobby(player); // 传送胜利玩家回大厅
                } else { // 不传送，改为发送传送消息
                    sendLobbyTeleportMessage(player, true);
                }
            }

            // 非胜利玩家
            if (gameEntry.teleportAfterGame) {
                List<GamePlayer> gamePlayerList = GameManager.get().getGamePlayers();
                for (GamePlayer gamePlayer : gamePlayerList) {
                    if (winnerGamePlayers.contains(gamePlayer) || gamePlayer.isEliminated()) {
                        continue;
                    }

                    ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
                    if (player == null) {
                        continue;
                    }
                    teleportToLobby(player); // 非胜利存活玩家直接回大厅
                }
            }
        }
    }

    /**
     * 强制终止游戏，不包含胜利玩家判断
     */
    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        this.teleportAfterGame();

        GameLootManager.get().stopGame(serverLevel);
        ZoneManager.get().stopGame(serverLevel);
        SpawnManager.get().stopGame(serverLevel);
        GameruleManager.get().stopGame(serverLevel);
        TeamManager.get().stopGame(serverLevel); // 最后处理TeamManager
        this.prepared = false;
        this.inGame = false;
        // this.ready = false; // 不使用ready标记，因为Team会变动
        // 取消事件监听
        unregisterGameEvent();

        if (!gameEntry.keepTeamAfterGame) {
            for (GamePlayer gamePlayer : getGamePlayers()) {
                notifyLeavedMember(gamePlayer.getPlayerUUID(), gamePlayer.getGameTeamId());
            }
            TeamManager.get().clear();
        }

        StatsManager.get().stopGame(serverLevel);
    }

    public boolean teleportToLobby(@NotNull ServerPlayer player) {
        if (SpawnManager.get().isLobbyCreated()) {
            SpawnManager.get().teleportToLobby(player);
            return true;
        } else {
            return false;
        }
    }

    public void onPlayerLoggedIn(ServerPlayer player) {
        GamePlayer gamePlayer = TeamManager.get().getGamePlayerByUUID(player.getUUID());
        if (gamePlayer != null) {
            if (GameManager.get().isInGame() && gamePlayer.isEliminated()) {
                notifyTeamChange(gamePlayer.getGameTeamId());
                ChatUtils.sendTranslatableMessageToPlayer(player, Component.translatable("battleroyale.message.you_are_eliminated").withStyle(ChatFormatting.RED));
            }
            return;
        }

        if (TeamManager.get().shouldAutoJoin() && !isInGame()) { // 没开游戏就加入
            TeamManager.get().joinTeam(player);
            teleportToLobby(player); // 登录自动传到大厅
        }
    }

    public void onPlayerLoggedOut(ServerPlayer player) {
        if (!isInGame()) {
            TeamManager.get().removePlayerFromTeam(player.getUUID()); // 没开始游戏就直接踢了
        }

        GamePlayer gamePlayer = TeamManager.get().getGamePlayerByUUID(player.getUUID());
        if (gamePlayer != null) {
            gamePlayer.setActiveEntity(false);
            checkIfGameShouldEnd();
        }
    }

    /**
     * 调用即视为gamePlayer死亡
     */
    public void onPlayerDeath(@NotNull GamePlayer gamePlayer) {
        gamePlayer.setAlive(false); // GamePlayer内部会自动更新eliminated

        if (gamePlayer.isEliminated()) {
            TeamManager.get().forceEliminatePlayerSilence(gamePlayer); // 提醒 TeamManager 内部更新 standingPlayer信息
        }

        GameTeam gameTeam = gamePlayer.getTeam();
        if (gameTeam.isTeamEliminated()) {
            BattleRoyale.LOGGER.info("Team {} has been eliminated", gameTeam.getGameTeamId());
            if (this.serverLevel != null) {
                ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.team_eliminated", gameTeam.getGameTeamId()).withStyle(ChatFormatting.RED));
            }
            checkIfGameShouldEnd();
        }
        notifyTeamChange(gamePlayer.getGameTeamId());
        notifyAliveChange();
    }

    /**
     * 安全传送，文明掉落
     * 传送不规范，玩家两行泪
     */
    public void safeTeleport(@NotNull ServerPlayer player, @NotNull Vec3 teleportPos) {
        safeTeleport(player, teleportPos.x, teleportPos.y, teleportPos.z);
    }
    /**
     * 安全传送，文明掉落
     * 传送不规范，玩家两行泪
     */
    public void safeTeleport(@NotNull ServerPlayer player, double x, double y, double z) {
        player.fallDistance = 0;
        player.teleportTo(x, y, z);
    }

    // TeamManager
    public int getPlayerLimit() { return TeamManager.get().getPlayerLimit(); }
    public @Nullable GamePlayer getGamePlayerByUUID(UUID uuid) { return TeamManager.get().getGamePlayerByUUID(uuid); }
    public @Nullable GamePlayer getGamePlayerBySingleId(int playerId) { return TeamManager.get().getGamePlayerBySingleId(playerId); }
    public List<GameTeam> getGameTeams() { return TeamManager.get().getGameTeamsList(); }
    public @Nullable GameTeam getGameTeamById(int teamId) { return TeamManager.get().getGameTeamById(teamId); }
    public List<GamePlayer> getGamePlayers() { return TeamManager.get().getGamePlayersList(); }
    public List<GamePlayer> getStandingGamePlayers() { return TeamManager.get().getStandingGamePlayersList(); }
    // StatsManager
    public boolean shouldRecordStats() { return StatsManager.get().shouldRecordStats(); }
    public void recordIntGamerule(Map<String, Integer> intGameruleWriter) { StatsManager.get().onRecordIntGamerule(intGameruleWriter); }
    public void recordBoolGamerule(Map<String, Boolean> boolGameruleWriter) { StatsManager.get().onRecordBoolGamerule(boolGameruleWriter); }
    public void recordDoubleGamerule(Map<String, Double> doubleGameruleWriter) { StatsManager.get().onRecordDoubleGamerule(doubleGameruleWriter); }
    public void recordStringGamerule(Map<String, String> stringGameruleWriter) { StatsManager.get().onRecordStringGamerule(stringGameruleWriter); }
    public void recordGamerule(IStatsWriter gameruleWriter) {
        recordIntGamerule(gameruleWriter.getIntWriter());
        recordBoolGamerule(gameruleWriter.getBoolWriter());
        recordDoubleGamerule(gameruleWriter.getDoubleWriter());
        recordStringGamerule(gameruleWriter.getStringWriter());
    }
    public void recordSpawnInt(String key, Map<String, Integer> spawnIntWriter) { StatsManager.get().onRecordSpawnInt(key, spawnIntWriter); }
    public void recordSpawnBool(String key, Map<String, Boolean> spawnBoolWriter) { StatsManager.get().onRecordSpawnBool(key, spawnBoolWriter); }
    public void recordSpawnDouble(String key, Map<String, Double> spawnDoubleWriter) { StatsManager.get().onRecordSpawnDouble(key, spawnDoubleWriter); }
    public void recordSpawnString(String key, Map<String, String> spawnStringWriter) { StatsManager.get().onRecordSpawnString(key, spawnStringWriter); }
    public void recordSpawn(String key, IStatsWriter spawnWriter) {
        recordSpawnInt(key, spawnWriter.getIntWriter());
        recordSpawnBool(key, spawnWriter.getBoolWriter());
        recordSpawnDouble(key, spawnWriter.getDoubleWriter());
        recordSpawnString(key, spawnWriter.getStringWriter());
    }
    public void recordZoneInt(int zoneId, Map<String, Integer> zoneIntWriter) { StatsManager.get().onRecordZoneInt(zoneId, zoneIntWriter); }
    public void recordZoneBool(int zoneId, Map<String, Boolean> zoneBoolWriter) { StatsManager.get().onRecordZoneBool(zoneId, zoneBoolWriter); }
    public void recordZoneDouble(int zoneId, Map<String, Double> zoneDoubleWriter) { StatsManager.get().onRecordZoneDouble(zoneId, zoneDoubleWriter); }
    public void recordZoneString(int zoneId, Map<String, String> zoneStringWriter) { StatsManager.get().onRecordZoneString(zoneId, zoneStringWriter); }
    public void recordZone(int zoneId, IStatsWriter zoneWriter) {
        recordZoneInt(zoneId, zoneWriter.getIntWriter());
        recordZoneBool(zoneId, zoneWriter.getBoolWriter());
        recordZoneDouble(zoneId, zoneWriter.getDoubleWriter());
        recordZoneString(zoneId, zoneWriter.getStringWriter());
    }

    public Supplier<Float> getRandom() {
        return () -> this.serverLevel.getRandom().nextFloat();
    }

    public void addZoneNbtMessage(int zoneId, @Nullable CompoundTag nbtMessage) { MessageManager.get().addZoneNbtMessage(zoneId, nbtMessage); }
    public void notifyZoneEnd(List<Integer> zoneIdList) { MessageManager.get().notifyZoneEnd(zoneIdList); }
    public void notifyTeamChange(int teamId) {
        MessageManager.get().notifyTeamChange(teamId);
    }
    public void notifyLeavedMember(UUID playerUUID, int teamId) {
        MessageManager.get().notifyLeavedMember(playerUUID, teamId);
    }
    public void notifyAliveChange() {
        MessageManager.get().notifyGameChange(GameMessageManager.ALIVE_CHANNEL);
    }

    public int getGameruleConfigId() { return gameruleConfigId; }
    public int getSpawnConfigId() { return spawnConfigId; }
    public int getBotConfigId() { return botConfigId; }

    private boolean initGameConfigSetup() {
        GameruleConfig gameruleConfig = GameConfigManager.get().getGameruleConfig(gameruleConfigId);
        if (gameruleConfig == null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            return false;
        }
        BattleroyaleEntry brEntry = gameruleConfig.getBattleRoyaleEntry();
        GameEntry gameEntry = gameruleConfig.getGameEntry();
        if (brEntry == null || gameEntry == null) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            return false;
        }
        maxGameTime = brEntry.maxGameTime;
        this.gameEntry = gameEntry;
        return true;
    }
    private void initGameConfigSubManager() {
        GameLootManager.get().initGameConfig(serverLevel);
        GameruleManager.get().initGameConfig(serverLevel);
        SpawnManager.get().initGameConfig(serverLevel);
        TeamManager.get().initGameConfig(serverLevel);
        ZoneManager.get().initGameConfig(serverLevel);
        StatsManager.get().initGameConfig(serverLevel);
    }
    private boolean gameConfigAllReady() {
        return (GameLootManager.get().isPreparedForGame() // 判定的优先级最低
                && GameruleManager.get().isPreparedForGame()
                && SpawnManager.get().isPreparedForGame()
                && TeamManager.get().isPreparedForGame()
                && ZoneManager.get().isPreparedForGame()
                && StatsManager.get().isPreparedForGame());
    }
    private void initGameSetup() {
        // 清除游戏效果
        EffectManager.get().forceEnd();
    }
    private void initGameSubManager() {
        StatsManager.get().initGame(serverLevel); // 先清空stats
        GameLootManager.get().initGame(serverLevel);
        TeamManager.get().initGame(serverLevel);
        GameruleManager.get().initGame(serverLevel); // Gamerule会进行一次默认游戏模式切换
        SpawnManager.get().initGame(serverLevel); // SpawnManager会进行一次传送，放在TeamManager之后
        ZoneManager.get().initGame(serverLevel);

        Map<String, Integer> intGamerule = new HashMap<>();
        intGamerule.put("maxGameTime", maxGameTime);
        recordIntGamerule(intGamerule);
    }
    private boolean startGameSubManager() {
        if (!GameLootManager.get().startGame(serverLevel)) { // 判定的优先级最高
            BattleRoyale.LOGGER.warn("GameLootManager failed to start game");
            return false;
        } else if (!TeamManager.get().startGame(serverLevel)) { // 先执行 TeamManager 得到 StandingGamePlayers，并确保无队伍玩家均被清理
            BattleRoyale.LOGGER.warn("TeamManager failed to start game");
            return false;
        } else if (!GameruleManager.get().startGame(serverLevel)) { // 依赖 TeamManager 的 StandingGamePlayers
            BattleRoyale.LOGGER.warn("GameruleManager failed to start game");
            return false;
        } else if (!ZoneManager.get().startGame(serverLevel)) { // 有圈则行
            BattleRoyale.LOGGER.warn("ZoneManager failed to start game");
            return false;
        } else if (!SpawnManager.get().startGame(serverLevel)) { // SpawnManager在onGameTick处理出生，提前处理过就行
            BattleRoyale.LOGGER.warn("SpawnManager failed to start game");
            return false;
        } else if (!StatsManager.get().startGame(serverLevel)) {
            BattleRoyale.LOGGER.warn("StatsManager failed to start game");
            return false;
        }
        return true;
    }
    private void startGameSetup() {
        this.gameDimensionKey = serverLevel.dimension();
        this.inGame = true;
        // this.ready = false; // 不使用ready标记，因为Team会变动
        this.gameTime = 0; // 游戏结束后不手动重置
        this.winnerGameTeams.clear(); // 游戏结束后不手动重置
        this.winnerGamePlayers.clear(); // 游戏结束后不手动重置
        registerGameEvent();
        notifyAliveChange();
        TempDataManager.get().writeString(GAME_MANAGER, GLOBAL_OFFSET, StringUtils.vectorToString(globalCenterOffset));
        TempDataManager.get().startGame(serverLevel); // 立即写入备份
    }
    private void registerGameEvent() {
        DamageEventHandler.register();
        LoopEventHandler.register();
        PlayerDeathEventHandler.register();
    }
    private void unregisterGameEvent() {
        DamageEventHandler.unregister();
        LoopEventHandler.unregister();
        PlayerDeathEventHandler.unregister();
        LogEventHandler.unregister();
    }

    /**
     * 由于Team会变动，开始游戏使用isCompleteReady检查
     */
    @Override
    public boolean isReady() {
        // return this.ready; // 不用ready标记，因为Team会变动
        return GameLootManager.get().isReady()
                && GameruleManager.get().isReady()
                && SpawnManager.get().isReady()
                // && TeamManager.get().isReady() // Team会变动
                && ZoneManager.get().isReady()
                && StatsManager.get().isReady();
    }
    /**
     * 开始游戏的检查
     */
    private boolean isStartReady() {
        return isReady() && TeamManager.get().isReady();
    }

    // 用指令设置默认配置
    public boolean setGameruleConfigId(int gameId) {
        if (gameId < 0 || GameConfigManager.get().getGameruleConfig(gameId) == null) {
            return false;
        }
        this.gameruleConfigId = gameId;
        return true;
    }
    public String getGameruleConfigName(int gameId) {
        GameruleConfig config = GameConfigManager.get().getGameruleConfig(gameId);
        return config != null ? config.getGameName() : "";
    }
    public boolean setSpawnConfigId(int id) {
        if (id < 0 || GameConfigManager.get().getSpawnConfig(id) == null) {
            return false;
        }
        this.spawnConfigId = id;
        return true;
    }
    public String getSpawnConfigName(int id) {
        SpawnConfigManager.SpawnConfig config = SpawnConfigManager.get().getSpawnConfig(id);
        return config != null ? config.name : "";
    }
    public boolean setBotConfigId(int id) {
        if (id < 0 || BotConfigManager.get().getBotConfig(id) == null) {
            return false;
        }
        this.botConfigId = id;
        return true;
    }
    public String getBotConfigName(int id) {
        BotConfigManager.BotConfig config = BotConfigManager.get().getBotConfig(id);
        return config != null ? config.name : "";
    }
}
