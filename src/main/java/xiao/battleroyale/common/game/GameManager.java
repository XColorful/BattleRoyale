package xiao.battleroyale.common.game;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.gamerule.BattleroyaleEntryTag;
import xiao.battleroyale.api.game.stats.IStatsWriter;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.command.sub.GameCommand;
import xiao.battleroyale.command.sub.TeamCommand;
import xiao.battleroyale.common.effect.EffectManager;
import xiao.battleroyale.common.game.gamerule.GameruleManager;
import xiao.battleroyale.common.game.loot.GameLootManager;
import xiao.battleroyale.common.game.spawn.SpawnManager;
import xiao.battleroyale.common.game.stats.StatsManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.common.game.team.TeamManager;
import xiao.battleroyale.compat.playerrevive.BleedingHandler;
import xiao.battleroyale.compat.playerrevive.PlayerRevive;
import xiao.battleroyale.data.io.TempDataManager;
import xiao.battleroyale.common.game.zone.ZoneManager;
import xiao.battleroyale.common.message.MessageManager;
import xiao.battleroyale.common.message.game.GameMessageManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.bot.BotConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager.GameruleConfig;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.config.common.game.gamerule.type.GameEntry;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager;
import xiao.battleroyale.event.DelayedEvent;
import xiao.battleroyale.event.game.*;
import xiao.battleroyale.util.*;

import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static xiao.battleroyale.api.data.io.TempDataTag.*;
import static xiao.battleroyale.util.CommandUtils.*;
import static xiao.battleroyale.util.GameUtils.buildGamePlayerText;

public class GameManager extends AbstractGameManager implements IStatsWriter {

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
    private String gameLevelKeyString = "";
    private @Nullable ResourceKey<Level> gameLevelKey;
    private @Nullable ServerLevel serverLevel;
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
    private int winnerTeamTotal = 1;
    private int requiredGameTeam = 2;
    public int getWinnerTeamTotal() { return winnerTeamTotal; }
    public int getRequiredGameTeam() { return requiredGameTeam; }
    private GameEntry gameEntry;
    public GameEntry getGameEntry() { return gameEntry; }
    public boolean isStopping = false;

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
        if (serverLevel == null) {
            BattleRoyale.LOGGER.warn("Passed ServerLevel in GameManager::initGameConfig is null");
            return;
        }
        // 初始化时绑定ServerLevel及其LevelKey
        setServerLevel(serverLevel);
        setGameLevelKey(serverLevel.dimension());

        if (!initGameConfigSetup()) {
            return;
        }
        initGameConfigSubManager();

        if (gameConfigAllReady()) {
            this.configPrepared = true;
            LogEventHandler.register(); // 后续玩家登录可根据配置直接加入队伍
            ServerEventHandler.register();
        } else {
            this.configPrepared = false;
        }
    }

    /**
     * 准备游戏，将玩家传送至大厅等
     * @param serverLevel 当前 serverLevel
     */
    @Override
    public void initGame(ServerLevel serverLevel) {
        if (isInGame()) {
            return;
        }

        if (!configPrepared || this.serverLevel != serverLevel) {
            BattleRoyale.LOGGER.info("GameManager isn't configPrepared, attempt to initGameConifg");
            initGameConfig(serverLevel);
            if (!configPrepared) {
                BattleRoyale.LOGGER.info("GameManager failed to auto initGameConifg, cancel initGame");
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
            BattleRoyale.LOGGER.info("GameManager isn't startReady, attempt to initGame");
            initGame(serverLevel);
            if (!isStartReady()) {
                BattleRoyale.LOGGER.info("GameManager failed to auto initGame, cancel startGame");
                return false;
            }
        }

        checkAndUpdateInvalidGamePlayer(this.serverLevel); // 供gameTime = 1时使用
        if (startGameSubManager()) {
            startGameSetup();
            this.inGame = true;
            GameMessageManager.get().startGame(serverLevel);
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
            ChatUtils.sendComponentMessageToAllPlayers(this.serverLevel, Component.translatable("battleroyale.message.reach_max_game_time").withStyle(ChatFormatting.GRAY));
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
        ZoneManager.get().onGameTick(gameTime); // Zone会提前触发stopGame，并且Zone需要延迟stopGame到tick结束

        TeamManager.get().onGameTick(gameTime); // 将解散队伍延迟到Zone之后，并且在tick最后清理队伍
        GameruleManager.get().onGameTick(gameTime);
        SpawnManager.get().onGameTick(gameTime);
        // StatsManager.get().onGameTick(gameTime); // 基于事件主动记录，不用tick
        if (gameTime % 200 == 0) {
            finishGameIfShouldEnd(); // 每10秒保底检查游戏结束
        }
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
                    ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.eliminated_invalid_player", invalidPlayer.getPlayerName()).withStyle(ChatFormatting.GRAY));
                    BattleRoyale.LOGGER.info("Force eliminated GamePlayer {} (UUID: {})", invalidPlayer.getPlayerName(), invalidPlayer.getPlayerUUID());
                }
            }
        }
    }
    private void updateInvalidServerPlayer(@NotNull GamePlayer gamePlayer, @NotNull ServerLevel serverLevel, List<GamePlayer> invalidPlayers) {
        ServerPlayer serverPlayer = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
        if (serverPlayer == null) { // 不在线或者不在游戏运行的 serverLevel
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
        if (this.serverLevel != null) {
            ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.player_leaved_from_level", gamePlayer.getPlayerName()).withStyle(ChatFormatting.DARK_GRAY));
        } else {
            BattleRoyale.LOGGER.warn("GameManager.serverLevel is null in notifyGamePlayerIsInactive(GamePlayer {})", gamePlayer.getPlayerName());
        }
    }
    private void notifyGamePlayerIsActive(GamePlayer gamePlayer) {
        if (this.serverLevel != null) {
            ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.player_backed_to_level", gamePlayer.getPlayerName()).withStyle(ChatFormatting.DARK_GRAY));
        } else {
            BattleRoyale.LOGGER.warn("GameManager.serverLevel is null in notifyGamePlayerIsActive(GamePlayer {})", gamePlayer.getPlayerName());
        }
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
                ChatUtils.sendComponentMessageToAllPlayers(this.serverLevel, Component.translatable("battleroyale.message.eliminated_invalid_player", teamMember.getPlayerName()).withStyle(ChatFormatting.GRAY));
                BattleRoyale.LOGGER.info("Force eliminated GamePlayer {} (UUID: {}) for inactive team", invalidPlayer.getPlayerName(), invalidPlayer.getPlayerUUID());
            }
        }
        return true;
    }

    /**
     * 获取大逃杀游戏ServerLevel
     */
    @Nullable
    public ServerLevel getServerLevel() {
        if (this.serverLevel != null) {
            return this.serverLevel;
        } else if (this.gameLevelKey != null) {
            return BattleRoyale.getMinecraftServer().getLevel(this.gameLevelKey);
        } else {
            BattleRoyale.LOGGER.debug("GameManager.serverLevel && GameManager.gameLevelKey are null");
            return null;
        }
    }

    /**
     * 获取大逃杀游戏维度Key
     */
    @Nullable
    public ResourceKey<Level> getGameLevelKey() {
        return this.gameLevelKey;
    }

    private void setServerLevel(@Nullable ServerLevel serverLevel) {
        this.serverLevel = serverLevel;
        BattleRoyale.LOGGER.debug("GameManager.serverLevel set to {}", this.serverLevel);
    }
    private void setGameLevelKey(@Nullable ResourceKey<Level> levelKey) {
        this.gameLevelKey = levelKey;
        BattleRoyale.LOGGER.debug("GameManager.gameLevelKey set to {}", this.gameLevelKey);
    }
    public void setDefaultLevel(@NotNull String levelKeyString) {
        this.gameLevelKeyString = levelKeyString;
        BattleRoyale.LOGGER.debug("GameManager.gameLevelKeyString set to {}", this.gameLevelKeyString);

        if (isInGame()) {
            BattleRoyale.LOGGER.warn("GameManager is in game, reject to set default level ({})", levelKeyString);
            return;
        }

        if (this.serverLevel == null) {
            setGameLevelKey(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(levelKeyString)));
            BattleRoyale.LOGGER.debug("Set GameManager.gameLevelKey to {}", this.gameLevelKey);
        } else {
            BattleRoyale.LOGGER.debug("GameManager.serverLevel != null ({}), skipped setDefaultLevel", this.serverLevel);
        }
    }

    /**
     * 完整检查所有队伍情况，淘汰无在线玩家的队伍
     * 调用此方法将检查是否有胜利队伍
     * 如果符合条件则直接结束游戏
     */
    public void checkIfGameShouldEnd() {
        if (!isInGame()) {
            return;
        }

        checkAndUpdateInvalidGamePlayer(this.serverLevel);
        finishGameIfShouldEnd(); // 外部调用的检查
    }

    private void finishGameIfShouldEnd() {
        if (TeamManager.get().getStandingTeamCount() <= winnerTeamTotal) { // 胜利条件，暂时硬编码
            BattleRoyale.LOGGER.debug("GameManager: standingTeam <= {}, finishGame with winner", winnerTeamTotal);
            finishGame(true);
            return;
        }

        if (!gameEntry.allowRemainingBot) { // 不允许只剩人机继续打架，即无真人玩家时提前终止游戏
            for (GameTeam gameTeam : getGameTeams()) {
                if (!gameTeam.onlyRemainBot()) {
                    return;
                }
            }
            // 没有提前返回就是没有1队真人
            finishGame(false);
            BattleRoyale.LOGGER.debug("Finished game with no winner for there's no two team has non-eliminated non-bot game player");
        }
    }

    /**
     * 结束游戏，所有未淘汰队伍均胜利
     */
    public void finishGame(boolean hasWinner) {
        if (!isInGame()) {
            BattleRoyale.LOGGER.debug("GameManager is not in game, skipped finishGame({})", hasWinner);
            return;
        }

        if (hasWinner) {
            for (GameTeam team : getGameTeams()) {
                if (!team.isTeamEliminated()) {
                    winnerGameTeams.add(team);
                }
            }
            for (GameTeam team : winnerGameTeams) {
                for (GamePlayer member : team.getTeamMembers()) {
                    winnerGamePlayers.add(member);
                    notifyWinner(member);
                }
            }
        }
        stopGame(this.serverLevel);
        if (hasWinner) {
            // 延迟2tick发送胜利队伍消息
            if (this.serverLevel != null) {
                ResourceKey<Level> cachedGameLevelKey = this.serverLevel.dimension();
                Consumer<ResourceKey<Level>> delayedTask = levelKey -> {
                    ServerLevel currentServerLevel = BattleRoyale.getMinecraftServer().getLevel(levelKey);
                    this.sendWinnerResult(currentServerLevel);
                };
                new DelayedEvent<>(delayedTask, cachedGameLevelKey, 1, "GameManager::sendWinnerResult");
            }
        }
    }
    // 发送胜利队伍消息
    public void sendWinnerResult(@Nullable ServerLevel serverLevel) {
        MutableComponent winnerComponent = Component.empty()
                .append(Component.translatable("battleroyale.message.game_time", gameTime, new GameUtils.GameTimeFormat(gameTime).toFormattedString(true)));
        for (GameTeam team : winnerGameTeams) {
            // 队伍ID
            TextColor color = TextColor.fromRgb(ColorUtils.parseColorToInt(team.getGameTeamColor()));
            MutableComponent teamComponent = Component.empty()
                    .append(buildSuggestableIntBracketWithColor(team.getGameTeamId(), TeamCommand.requestTeamCommand(team.getGameTeamId()), color));
            // 队长
            GamePlayer leader = team.getLeader();
            teamComponent.append(Component.literal(" "))
                    .append(buildSuggestableIntBracketWithFullColor(leader.getGameSingleId(), TeamCommand.requestPlayerCommand(leader.getPlayerName()), color))
                    .append(Component.literal(leader.getPlayerName()).withStyle(leader.isEliminated() ? ChatFormatting.GRAY : ChatFormatting.GOLD));
            // 队员
            for (GamePlayer member : team.getTeamMembers()) {
                if (member.getGameSingleId() == leader.getGameSingleId()) {
                    continue;
                }
                teamComponent.append(Component.literal(" "))
                        .append(buildSuggestableIntBracketWithFullColor(member.getGameSingleId(), TeamCommand.requestPlayerCommand(member.getPlayerName()), color))
                        .append(Component.literal(member.getPlayerName()).withStyle(member.isEliminated() ? ChatFormatting.GRAY : ChatFormatting.GOLD));
            }
            // 添加到消息
            winnerComponent.append(Component.literal("\n")
                    .append(teamComponent));
        }
        if (serverLevel != null) {
            ChatUtils.sendMessageToAllPlayers(serverLevel, winnerComponent);
            // 游戏正常结束后自动初始化游戏
            if (this.gameEntry.initGameAfterGame) {
                initGame(serverLevel);
            }
        } else {
            BattleRoyale.LOGGER.debug("GameManager.serverLevel is null, winner result: {}", winnerComponent);
        }
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
        int colorRGB = ColorUtils.parseColorToInt(gamePlayer.getGameTeamColor()) & 0xFFFFFF;
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

    public void sendGameSpectateMessage(@NotNull ServerPlayer player) {
        sendGameSpectateMessage(player, !gameEntry.onlyGamePlayerSpectate);
    }

    private void sendGameSpectateMessage(@NotNull ServerPlayer player, boolean allowSpectate) {
        String spectateCommand = GameCommand.spectateCommand();

        Component fullMessage = Component.translatable("battleroyale.message.has_game_in_progress")
                .append(Component.literal("\n"))
                // 游戏时长：int(time)
                .append(Component.translatable("battleroyale.message.game_time", gameTime, new GameUtils.GameTimeFormat(gameTime).toFormattedString(true)))
                .append(Component.literal(" "))
                // 生存 int/int
                .append(Component.translatable("battleroyale.label.alive"))
                .append(Component.literal(" "))
                .append(Component.literal(String.valueOf(getStandingGamePlayers().size())).withStyle(ChatFormatting.AQUA))
                .append(Component.literal("/" + getGamePlayers().size()))
                .append(Component.literal(" "))
                // [观战]
                .append(buildRunnableText(Component.translatable("battleroyale.message.spectate"), spectateCommand, allowSpectate ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY));

        ChatUtils.sendComponentMessageToPlayer(player, fullMessage);
    }

    /**
     * 用于向胜利玩家发送消息，传送回大厅
     */
    public void sendLobbyTeleportMessage(@NotNull ServerPlayer player, boolean isWinner) {
        String toLobbyCommand = GameCommand.toLobbyCommand();

        Component fullMessage = Component.translatable("battleroyale.message.back_to_lobby")
                .append(Component.literal(" "))
                .append(buildRunnableText(Component.translatable("battleroyale.message.teleport"),
                        toLobbyCommand,
                        isWinner ? ChatFormatting.GOLD :  ChatFormatting.WHITE));

        ChatUtils.sendComponentMessageToPlayer(player, fullMessage);
    }

    private void teleportAfterGame() {
        if (isInGame()) { // 防止在1tick里既stopGame又startGame
            return;
        }
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
            List<GamePlayer> gamePlayerList = GameManager.get().getGamePlayers();
            for (GamePlayer gamePlayer : gamePlayerList) {
                if (winnerGamePlayers.contains(gamePlayer)) {
                    continue;
                }

                ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
                if (player == null) {
                    continue;
                }

                if (gameEntry.teleportAfterGame) {
                    teleportToLobby(player); // 非胜利存活玩家直接回大厅
                } else {
                    sendLobbyTeleportMessage(player, false);
                }
            }
        }
    }

    /**
     * 强制终止游戏，不包含胜利玩家判断
     */
    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        GameLootManager.get().stopGame(serverLevel);
        ZoneManager.get().stopGame(serverLevel);
        SpawnManager.get().stopGame(serverLevel);
        GameruleManager.get().stopGame(serverLevel);
        // ↑以上操作均不需要inGame判断
        this.inGame = false;
        this.teleportAfterGame();

        TeamManager.get().stopGame(serverLevel); // 最后处理TeamManager
        this.configPrepared = false;
        GameMessageManager.get().stopGame(serverLevel); // 不在游戏中影响消息逻辑
        // this.ready = false; // 不使用ready标记，因为Team会变动
        // 取消事件监听
        unregisterGameEvent();

        StatsManager.get().stopGame(serverLevel);

        // 游戏中途若修改配置，在游戏结束后生效
        setGameLevelKey(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(this.gameLevelKeyString)));
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
            if (serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID()) != null) { // 不一定在大逃杀游戏的维度
                gamePlayer.setActiveEntity(true);
            }
            if (GameManager.get().isInGame() && gamePlayer.isEliminated()) {
                notifyTeamChange(gamePlayer.getGameTeamId());
                ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.you_are_eliminated").withStyle(ChatFormatting.RED));
                teleportToLobby(player); // 淘汰的传送回大厅，防止干扰游戏
            }
            return;
        }

        if (isInGame()) {
            sendGameSpectateMessage(player, !gameEntry.onlyGamePlayerSpectate); // 提供游戏信息及观战指令
        } else { // 没开游戏就加入
            if (TeamManager.get().shouldAutoJoin()) {
                TeamManager.get().joinTeam(player);
                teleportToLobby(player); // 登录自动传到大厅
            }
        }
    }

    public void onPlayerLoggedOut(ServerPlayer player) {
        if (!isInGame()) {
            if (TeamManager.get().removePlayerFromTeam(player.getUUID())) { // 没开始游戏就等于离队
                BattleRoyale.LOGGER.debug("Player {} logged out, remove GamePlayer", player.getName().getString());
            }
        }

        GamePlayer gamePlayer = TeamManager.get().getGamePlayerByUUID(player.getUUID());
        if (gamePlayer != null) {
            gamePlayer.setActiveEntity(false);
            finishGameIfShouldEnd(); // 玩家登出服务器时的防御检查
        }
    }

    /**
     * 检查GamePlayer是被不死图腾救了还是PlayerRevive倒地
     * 没有队友时不允许倒地直接让PlayerRevive击杀掉
     * PlayerRevive只允许玩家倒地，因此人机玩家无法倒地
     */
    public void onPlayerDown(@NotNull GamePlayer gamePlayer, LivingEntity livingEntity) {
        // 不允许倒地的情况：队友没有Alive的
        GameTeam gameTeam = gamePlayer.getTeam();
        boolean hasAliveMember = false;
        for (GamePlayer member : gameTeam.getAlivePlayers()) { // 直接忽略被淘汰的队友
            if (member.getGameSingleId() == gamePlayer.getGameSingleId()) {
                continue;
            }
            if (gameEntry.removeInvalidTeam && !member.isActiveEntity()) { // 队友离线算作倒地 && 队友离线
                continue;
            }
            hasAliveMember = true;
            break;
        }
        if (!hasAliveMember) { // 没有存活队友就判定为无法救援，直接判死亡
            BattleRoyale.LOGGER.debug("GamePlayer {} is down and has no alive member", gamePlayer.getPlayerName());
            onPlayerDeath(gamePlayer);
            return;
        }

        // PlayerRevive倒地机制：取消事件并设置为流血状态
        if (livingEntity instanceof Player player) {
            PlayerRevive playerRevive = PlayerRevive.get();
            if (playerRevive.isBleeding(player)) {
                gamePlayer.setAlive(false);
                playerRevive.addBleedingPlayer(player);
                sendDownMessage(gamePlayer);
                return;
            }
        }

        if (!gamePlayer.isAlive()) { // 倒地，但是不为存活状态
            BattleRoyale.LOGGER.debug("GamePlayer {} is down but not alive, switch to onPlayerDeath", gamePlayer.getPlayerName());
            onPlayerDeath(gamePlayer);
            notifyTeamChange(gamePlayer.getGameTeamId());
        }

        // 没检测到PlayerRevive就认为是不死图腾救了
        // 实际貌似不会触发log，不清楚不死图腾原理
        // 只能认为不死图腾的功能不是自救，而是阻止倒地
        gamePlayer.setAlive(true); // 其实应该不需要设置
        BattleRoyale.LOGGER.debug("Not detected PlayerRevive, should be revived by Totem of Undying");
    }

    /**
     * 调用即视为gamePlayer被救起
     */
    public void onPlayerRevived(@NotNull GamePlayer gamePlayer) {
        if (!hasStandingGamePlayer(gamePlayer.getPlayerUUID()) || gamePlayer.isEliminated()) { // 该GamePlayer已经不是未被淘汰玩家
            BattleRoyale.LOGGER.debug("GamePlayer {} is not a standing game player, skipped revive", gamePlayer.getPlayerName());
            return;
        }
        gamePlayer.setAlive(true);
        sendReviveMessage(gamePlayer);
        BattleRoyale.LOGGER.info("GamePlayer {} has revived, singleId:{}", gamePlayer.getPlayerName(), gamePlayer.getGameSingleId());
    }

    /**
     * 调用即视为gamePlayer死亡
     */
    public void onPlayerDeath(@NotNull GamePlayer gamePlayer) {
        boolean teamEliminatedBefore = gamePlayer.getTeam().isTeamEliminated();
        boolean playerEliminatedBefore = gamePlayer.isEliminated();
        gamePlayer.setEliminated(true); // GamePlayer内部会自动让GameTeam更新eliminated
        TeamManager.get().forceEliminatePlayerSilence(gamePlayer); // 提醒 TeamManager 内部更新 standingPlayer信息
        // 死亡事件会跳过非standingPlayer，放心kill
        if (!playerEliminatedBefore) { // 第一次淘汰才尝试kill，淘汰后被打倒的不管
            sendEliminateMessage(gamePlayer);
            PlayerRevive playerRevive = PlayerRevive.get();
            ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
            if (player != null && playerRevive.isBleeding(player)) {
                BattleRoyale.LOGGER.debug("Detected GamePlayer {} PlayerRevive.isBleeding, force kill", gamePlayer.getPlayerName());
                playerRevive.kill(player);
            }
        }

        GameTeam gameTeam = gamePlayer.getTeam();
        if (gameTeam.isTeamEliminated()) {
            // 队伍淘汰则倒地队友全部kill
            BattleRoyale.LOGGER.info("Team {} has been eliminated, updating member to eliminated", gameTeam.getGameTeamId());
            for (GamePlayer member : gameTeam.getTeamMembers()) {
                if (!member.isEliminated()) {
                    onPlayerDeath(member);
                }
            }
            if (this.serverLevel != null) {
                if (!teamEliminatedBefore) {
                    ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.team_eliminated", gameTeam.getGameTeamId()).withStyle(ChatFormatting.RED));
                } else {
                    BattleRoyale.LOGGER.debug("Team {} has already been eliminated, GameManager skipped sending chat message", gameTeam.getGameTeamId());
                }
            }
            finishGameIfShouldEnd(); // 游戏队伍被淘汰时的检查
        }
        notifyTeamChange(gamePlayer.getGameTeamId());
        notifyAliveChange();
    }

    public void sendDownMessage(@NotNull GamePlayer gamePlayer) {
        if (serverLevel != null) {
            MutableComponent component = buildGamePlayerText(gamePlayer, ChatFormatting.GRAY)
                    .append(Component.literal(" "))
                    .append(Component.translatable("battleroyale.message.is_downed"));
            ChatUtils.sendComponentMessageToAllPlayers(serverLevel, component);
        } else {
            BattleRoyale.LOGGER.warn("GameManager.serverLevel is null, failed to send GamePlayer {} down", gamePlayer.getPlayerName());
        }
    }
    public void sendReviveMessage(@NotNull GamePlayer gamePlayer) {
        if (serverLevel != null) {
            MutableComponent component = buildGamePlayerText(gamePlayer, ChatFormatting.GREEN)
                    .append(Component.literal(" "))
                    .append(Component.translatable("battleroyale.message.is_revived"));
            ChatUtils.sendComponentMessageToAllPlayers(serverLevel, component);
        } else {
            BattleRoyale.LOGGER.warn("GameManager.serverLevel is null, failed to send GamePlayer {} revive", gamePlayer.getPlayerName());
        }
    }
    public void sendEliminateMessage(@NotNull GamePlayer gamePlayer) {
        if (serverLevel != null) {
            MutableComponent component = buildGamePlayerText(gamePlayer, ChatFormatting.RED)
                    .append(Component.literal(" "))
                    .append(Component.translatable("battleroyale.message.is_eliminated"));
            ChatUtils.sendComponentMessageToAllPlayers(serverLevel, component);
        } else {
            BattleRoyale.LOGGER.warn("GameManager.serverLevel is null, failed to send GamePlayer {} eliminate", gamePlayer.getPlayerName());
        }
    }

    public void onServerStopping() {
        isStopping = true;
        stopGame(serverLevel);
        setServerLevel(null); // 手动设置为null，单人游戏重启之后也就失效了
        BattleRoyale.LOGGER.debug("Server stopped, GameManager.serverLevel set to null");
        ServerEventHandler.unregister();
        isStopping = false;
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
        if (isStopping) {
            return;
        }
        player.fallDistance = 0;
        player.teleportTo(x, y, z);
    }

    /**
     * 跨纬度版本
     */
    public void safeTeleport(@NotNull ServerPlayer player, @NotNull ServerLevel serverLevel, @NotNull Vec3 teleportPos, float yaw, float pitch) {
        safeTeleport(player, serverLevel, teleportPos.x, teleportPos.y, teleportPos.z, yaw, pitch);
    }
    public void safeTeleport(@NotNull ServerPlayer player, @NotNull ServerLevel serverLevel, double x, double y, double z, float yaw, float pitch) {
        if (isStopping) {
            return;
        }
        player.fallDistance = 0;
        player.teleportTo(serverLevel, x, y, z, yaw, pitch);
    }

    // TeamManager
    public int getPlayerLimit() { return TeamManager.get().getPlayerLimit(); }
    public @Nullable GamePlayer getGamePlayerByUUID(UUID uuid) { return TeamManager.get().getGamePlayerByUUID(uuid); }
    public @Nullable GamePlayer getGamePlayerBySingleId(int playerId) { return TeamManager.get().getGamePlayerBySingleId(playerId); }
    public boolean hasStandingGamePlayer(UUID uuid) { return TeamManager.get().hasStandingGamePlayer(uuid);}
    public List<GameTeam> getGameTeams() { return TeamManager.get().getGameTeamsList(); }
    public @Nullable GameTeam getGameTeamById(int teamId) { return TeamManager.get().getGameTeamById(teamId); }
    public List<GamePlayer> getGamePlayers() { return TeamManager.get().getGamePlayersList(); }
    public List<GamePlayer> getStandingGamePlayers() { return TeamManager.get().getStandingGamePlayersList(); }
    public @Nullable GamePlayer getRandomStandingGamePlayer() { return TeamManager.get().getRandomStandingGamePlayer(); }
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
    // ZoneManager
    public List<IGameZone> getGameZones() { return ZoneManager.get().getGameZones(); }
    public List<IGameZone> getCurrentGameZones() { return ZoneManager.get().getCurrentTickGameZones(this.gameTime); }
    public List<IGameZone> getCurrentGameZones(int gameTime) { return ZoneManager.get().getCurrentTickGameZones(gameTime); }
    public IGameZone getGameZone(int zoneId) { return ZoneManager.get().getZoneById(zoneId); }

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
        this.maxGameTime = brEntry.maxGameTime;
        this.winnerTeamTotal = brEntry.winnerTeamTotal;
        this.requiredGameTeam = brEntry.requiredTeamToStart;
        this.gameEntry = gameEntry;
        BleedingHandler.setBleedDamage(this.gameEntry.downDamageList);
        BleedingHandler.setBleedCooldown(this.gameEntry.downDamageFrequency);
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
        this.configPrepared = false;
    }
    private void initGameSubManager() {
        StatsManager.get().initGame(serverLevel); // 先清空stats
        GameLootManager.get().initGame(serverLevel);
        TeamManager.get().initGame(serverLevel); // TeamManager先处理组队
        GameruleManager.get().initGame(serverLevel); // Gamerule记录游戏模式
        SpawnManager.get().initGame(serverLevel); // SpawnManager会传送至大厅并更改游戏模式
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
        // this.ready = false; // 不使用ready标记，因为Team会变动
        this.gameTime = 0; // 游戏结束后不手动重置
        this.winnerGameTeams.clear(); // 游戏结束后不手动重置
        this.winnerGamePlayers.clear(); // 游戏结束后不手动重置
        registerGameEvent();
        TempDataManager.get().writeString(GAME_MANAGER, GLOBAL_OFFSET, StringUtils.vectorToString(globalCenterOffset));
        TempDataManager.get().startGame(serverLevel); // 立即写入备份
        if (this.gameEntry.healAllAtStart) {
            healGamePlayers();
        }
        recordGamerule(this);
    }
    private void healGamePlayers() {
        if (serverLevel == null) {
            BattleRoyale.LOGGER.debug("GameManager.serverLevel is null, failed to heal GamePlayers");
            return;
        }
        SpawnManager spawnManager = SpawnManager.get();
        List<GamePlayer> gamePlayers = new ArrayList<>(getGamePlayers());
        for (GamePlayer gamePlayer : gamePlayers) {
            ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
            if (player != null) {
                spawnManager.healPlayer(player);
                notifyTeamChange(gamePlayer.getGameTeamId());
            }
        }
    }
    private void registerGameEvent() {
        DamageEventHandler.register();
        LoopEventHandler.register();
        PlayerDeathEventHandler.register();
        BleedingHandler.get().clear();
    }
    private void unregisterGameEvent() {
        DamageEventHandler.unregister();
        LoopEventHandler.unregister();
        PlayerDeathEventHandler.unregister();
        LogEventHandler.unregister();
        // ServerEventHandler.unregister(); // 不需要解除注册
        BleedingHandler.unregister();
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
            BattleRoyale.LOGGER.info("setGameruleConfigId {} failed", gameId);
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

    public String getZoneConfigFileName() {
        return GameConfigManager.get().getZoneConfigEntryFileName();
    }

    public void sendSelectedConfigsInfo(ServerLevel serverLevel) {
        if (serverLevel == null) {
            return;
        }

        ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.selected_bot_config", getBotConfigId(), getBotConfigName(getBotConfigId())));
        ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.selected_gamerule_config", getGameruleConfigId(), getGameruleConfigName(getGameruleConfigId())));
        ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.selected_spawn_config", getSpawnConfigId(), getSpawnConfigName(getSpawnConfigId())));
        ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.selected_zone_config", getZoneConfigFileName(), GameConfigManager.get().getZoneConfigList().size()));
    }

    public void sendSelectedConfigsInfo(ServerPlayer player) {
        if (player == null) {
            return;
        }

        ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.selected_bot_config", getBotConfigId(), getBotConfigName(getBotConfigId())));
        ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.selected_gamerule_config", getGameruleConfigId(), getGameruleConfigName(getGameruleConfigId())));
        ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.selected_spawn_config", getSpawnConfigId(), getSpawnConfigName(getSpawnConfigId())));
        ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.selected_zone_config", getZoneConfigFileName(), GameConfigManager.get().getZoneConfigList().size()));
    }

    /**
     * 切换旁观模式
     */
    public boolean spectateGame(ServerPlayer player, ServerLevel serverLevel) {
        if (player == null) {
            return false;
        }

        // 从观战模式改回去
        if (player.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
            if (!isInGame()) { // 不在游戏进行时，即使不为GamePlayer也可以改回来（可能被清理掉）
                player.setGameMode(GameruleManager.get().getGameMode()); // 默认为冒险模式
                ChatUtils.sendTranslatableMessageToPlayer(player, "battleroyale.message.switch_gamemode_success");
                teleportToLobby(player);
            } else { // 不允许在游戏中从观战模式改回去，但是仍然执行一次传送
                teleportToRandomStandingGamePlayer(player, serverLevel);
            }
            return true;
        }

        GamePlayer gamePlayer = getGamePlayerByUUID(player.getUUID());
        if (gamePlayer == null) { // 非游戏玩家
            if (this.gameEntry.onlyGamePlayerSpectate) {
                return false;
            }
            player.setGameMode(GameType.SPECTATOR);
            teleportToRandomStandingGamePlayer(player, serverLevel);
            return true;
        }

        if (!gamePlayer.isEliminated()) { // 未被淘汰不能观战
            return false;
        }
        if (!gamePlayer.getTeam().isTeamEliminated()) { // 队伍未被淘汰不能观战
            if (this.gameEntry.spectateAfterTeam) {
                return false;
            }
        }
        player.setGameMode(GameType.SPECTATOR);
        teleportToRandomStandingGamePlayer(player, serverLevel);

        return true;
    }

    public void teleportToRandomStandingGamePlayer(ServerPlayer player, ServerLevel serverLevel) {
        if (serverLevel != this.serverLevel) {
            return;
        }
        GamePlayer standingGamePlayer = getRandomStandingGamePlayer();
        if (standingGamePlayer != null) {
            float yaw = 0, pitch = 0;
            ServerPlayer targetPlayer = (ServerPlayer) this.serverLevel.getPlayerByUUID(standingGamePlayer.getPlayerUUID());
            if (targetPlayer != null) {
                yaw = targetPlayer.getYRot();
                pitch = targetPlayer.getXRot();
            }
            safeTeleport(player, serverLevel, standingGamePlayer.getLastPos(), yaw, pitch);
            ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.player_is_spectating", player.getName().getString(), standingGamePlayer.getPlayerName()).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public Map<String, Integer> getIntWriter() {
        Map<String, Integer> intGamerule = new HashMap<>();
        intGamerule.put(BattleroyaleEntryTag.REQUIRED_TEAM_TO_START, this.requiredGameTeam);
        intGamerule.put(BattleroyaleEntryTag.WINNER_TEAM_TOTAL, this.winnerTeamTotal);
        return intGamerule;
    }
}
