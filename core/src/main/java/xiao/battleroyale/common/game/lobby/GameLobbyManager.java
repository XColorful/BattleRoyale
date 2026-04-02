package xiao.battleroyale.common.game.lobby;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.api.config.IModConfigManager;
import xiao.battleroyale.api.event.ICustomEventPoster;
import xiao.battleroyale.api.event.game.spawn.GameLobbyTeleportEvent;
import xiao.battleroyale.api.event.game.spawn.GameLobbyTeleportFinishEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.game.lobby.IGameLobbyManager;
import xiao.battleroyale.command.sub.GameCommand;
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.common.game._GameTeamManager;
import xiao.battleroyale.common.game.GameUtilsFunction;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.compat.playerrevive.PlayerRevive;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;
import xiao.battleroyale.config.common.game.gamerule.type.GameEntry;
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.GameUtils;
import xiao.battleroyale.util.Vec3Utils;

import java.util.List;

import static xiao.battleroyale.util.CommandUtils.buildRunnableText;

public class GameLobbyManager extends AbstractGameManager implements IGameLobbyManager {

    private static class GameLobbyManagerHolder {
        private static final GameLobbyManager INSTANCE = new GameLobbyManager();
    }

    public static GameLobbyManager get() {
        return GameLobbyManagerHolder.INSTANCE;
    }

    protected GameLobbyManager() {}

    public static void init(McSide mcSide) {
    }

    @Override public String getManagerName() {
        return String.format("%s:GameLobbyManager", BattleRoyale.MOD_ID);
    }

    protected boolean initGameTeleport = true;
    protected Vec3 lobbyPos;
    protected Vec3 lobbyDimension;
    protected boolean lobbyMuteki = true;
    protected boolean lobbyHeal = true;
    protected boolean changeGamemode = true;
    protected boolean teleportDropInventory = false;
    protected boolean teleportClearInventory = false;

    @Override
    public void initGameConfig(ServerLevel serverLevel) {
        IGameManager gameManager = BattleRoyale.getGameManager();
        if (gameManager.isInGame()) return;

        IModConfigManager modConfigManager = BattleRoyale.getModConfigManager();
        IConfigSubManager<?> gameruleConfigManager = modConfigManager.getConfigSubManager(GameConfigManager.get().getNameKey(), GameruleConfigManager.get().getNameKey());
        int configId = gameManager.getGameruleConfigId();
        if (gameruleConfigManager == null || !(gameruleConfigManager.getConfigEntry(configId) instanceof GameruleConfigManager.GameruleConfig gameruleConfig)) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            return;
        }
        BattleroyaleEntry brEntry = gameruleConfig.getBattleRoyaleEntry();
        if (brEntry == null) {
            BattleRoyale.LOGGER.debug("Gamerule config missing brEntry");
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            return;
        }
        setLobby(brEntry.lobbyCenterPos, brEntry.lobbyDimension, brEntry.lobbyMuteki, brEntry.lobbyHeal, brEntry.lobbyChangeGamemode, brEntry.lobbyTeleportDropInventory, brEntry.lobbyTeleportClearInventory);
        if (!isLobbyCreated()) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            return;
        }

        GameEntry gameEntry = gameruleConfig.getGameEntry();
        if (gameEntry == null) {
            BattleRoyale.LOGGER.debug("Gamerule config missing gameEntry");
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            return;
        }
        this.initGameTeleport = gameEntry.teleportWhenInitGame;


        this.configPrepared = true;
        BattleRoyale.LOGGER.debug("GameLobbyManager complete initGameConfig");
    }

    @Override
    public void initGame(ServerLevel serverLevel) {
        if (BattleRoyale.getGameManager().isInGame()) {
            return;
        }
        if (!this.configPrepared) {
            return;
        }

        // 传送至大厅
        if (this.initGameTeleport) {
            List<GamePlayer> gamePlayerList = _GameTeamManager.getGamePlayers();
            for (GamePlayer gamePlayer : gamePlayerList) {
                teleportGamePlayerToLobby(gamePlayer, serverLevel);
            }
            BattleRoyale.LOGGER.debug("GameLobbyManager::initGame teleported all game player to lobby");
        }
        this.configPrepared = false;
        this.ready = true;
        BattleRoyale.LOGGER.debug("GameLobbyManager complete initGame");
    }

    @Override
    public boolean startGame(ServerLevel serverLevel) {
        if (BattleRoyale.getGameManager().isInGame()) {
            return false;
        }

        return isReady();
    }

    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        this.configPrepared = false;
        this.ready = false;
    }

    @Override
    public void onGameTick(int gameTime) {
        ;
    }

    // --------ILobbyFuncApi--------

    @Override public boolean teleportToLobby(@NotNull LivingEntity livingEntity) {
        IGameManager gameManager = BattleRoyale.getGameManager();
        ICustomEventPoster eventPoster = BattleRoyale.getEventPoster();
        if (eventPoster.postCustomEvent(new GameLobbyTeleportEvent(gameManager, livingEntity))) {
            BattleRoyale.LOGGER.debug("GameLobbyManager: LobbyTeleportEvent canceled, skipped teleportToLobbyInGame (LivingEntity {})", livingEntity.getName().getString());
            return false;
        }

        if (!isLobbyCreated()) {
            BattleRoyale.LOGGER.debug("GameLobbyManager: Lobby is not created, failed to teleport livingEntity {} (UUID:{}) to lobby", livingEntity.getName().getString(), livingEntity.getUUID());
            return false;
        }

        if (livingEntity instanceof ServerPlayer player) {
            if (lobbyHeal) {
                healPlayer(player);
            }
            if (changeGamemode) {
                player.setGameMode(gameManager.getGameruleManager().getGameMode());
            }
            if (teleportDropInventory) {
                player.getInventory().dropAll();
            }
            if (teleportClearInventory) {
                player.getInventory().clearContent();
            }
        } else {
            ;
        }

        ServerLevel serverLevel = gameManager.getServerLevel();
        if (serverLevel != null) {
            GameUtilsFunction.safeTeleport(livingEntity, serverLevel, lobbyPos, 0, 0); // 大厅传送
        } else {
            BattleRoyale.LOGGER.debug("GameManager.serverLevel is null, teleport to literal position");
            GameUtilsFunction.safeTeleport(livingEntity, lobbyPos);
        }
        BattleRoyale.LOGGER.info("Teleport livingEntity {} (UUID: {}) to lobby ({}, {}, {})", livingEntity.getName().getString(), livingEntity.getUUID(), lobbyPos.x, lobbyPos.y, lobbyPos.z);
        eventPoster.postCustomEvent(new GameLobbyTeleportFinishEvent(gameManager, livingEntity));
        return true;
    }
    @Override public void healPlayer(@NotNull LivingEntity livingEntity) {
        @Nullable ServerPlayer player = livingEntity instanceof ServerPlayer serverPlayer ? serverPlayer : null;
        if (player != null && PlayerRevive.get().isBleeding(player)) {
            PlayerRevive.get().revive(player);
        }
        livingEntity.removeAllEffects();
        livingEntity.heal(livingEntity.getMaxHealth()); // heal会触发事件
        if (player != null) {
            player.getFoodData().setFoodLevel(20);
        }
    }
    @Override public boolean setLobby(Vec3 centerPos, Vec3 dimension, boolean shouldMuteki, boolean shouldHeal, boolean changeGamemode, boolean teleportDropInventory, boolean teleportClearInventory) {
        if (BattleRoyale.getGameManager().isInGame()) {
            BattleRoyale.LOGGER.debug("GameManager is in game, GameLobbyManager skipped set lobby");
            return false;
        }
        if (Vec3Utils.hasNegative(dimension)) {
            BattleRoyale.LOGGER.warn("GameLobbyManager: dimension:{} has negative, reject to apply", dimension);
            return false;
        }
        this.lobbyPos = centerPos;
        this.lobbyMuteki = shouldMuteki;
        // 大厅无敌（监听伤害事件）
        if (this.lobbyMuteki) {
            _LobbyEventHandler.register();
        } else {
            _LobbyEventHandler.unregister();
        }
        this.lobbyHeal = shouldHeal;
        this.lobbyDimension = dimension;
        this.changeGamemode = changeGamemode;
        this.teleportDropInventory = teleportDropInventory;
        this.teleportClearInventory = teleportClearInventory;
        BattleRoyale.LOGGER.debug("Successfully set lobby: center{}, dim{}", lobbyPos, lobbyDimension);
        return true;
    }
    @Override public boolean setLobby(Vec3 centerPos, double radius) {
        return setLobby(centerPos, new Vec3(radius, radius, radius), this.lobbyMuteki, this.lobbyHeal, this.changeGamemode, this.teleportDropInventory, this.teleportClearInventory);
    }
    /**
     * 类内部负责的传送，类内调用前进行检查
     */
    protected void teleportGamePlayerToLobby(@NotNull GamePlayer gamePlayer, @NotNull ServerLevel serverLevel) {
        LivingEntity livingEntity = GameUtils.getLivingEntity(serverLevel, gamePlayer.getPlayerUUID());
        if (livingEntity == null) {
            return;
        }
        teleportToLobby(livingEntity);
    }

    // --------GameApi--------

    @Override public boolean isLobbyCreated() {
        // return configPrepared || ready || BattleRoyale.getGameManager().isInGame(); // 任意阶段均保证大厅已创建
        return lobbyPos != null && lobbyDimension != null; // 让游戏结束后也能传送回大厅
    }
    @Override public ResourceKey<Level> lobbyLevelKey() {
        return BattleRoyale.getGameManager().getGameLevelKey();
    }
    @Override public Vec3 lobbyPos() {
        return this.lobbyPos;
    }
    @Override public Vec3 lobbyDimension() {
        return this.lobbyDimension;
    }
    @Override public boolean lobbyMuteki() {
        return this.lobbyMuteki;
    }
    @Override public boolean lobbyHeal() {
        return this.lobbyHeal;
    }
    @Override public boolean lobbyChangeGamemode() {
        return this.changeGamemode;
    }
    @Override public boolean teleportDropInventory() {
        return this.teleportDropInventory;
    }
    @Override public boolean teleportClearInventory() {
        return this.teleportClearInventory;
    }

    /**
     * 调用时保证 lobbyPos 和 lobbyDimension 非空
     * @param pos 需要判断的位置
     * @return 判定结果
     */
    @Override public boolean isInLobbyRange(Vec3 pos) {
        double minX = lobbyPos.x - lobbyDimension.x;
        double maxX = lobbyPos.x + lobbyDimension.x;
        double minY = lobbyPos.y - lobbyDimension.y;
        double maxY = lobbyPos.y + lobbyDimension.y;
        double minZ = lobbyPos.z - lobbyDimension.z;
        double maxZ = lobbyPos.z + lobbyDimension.z;

        return pos.x >= minX && pos.x <= maxX &&
                pos.y >= minY && pos.y <= maxY &&
                pos.z >= minZ && pos.z <= maxZ;
    }
    @Override public boolean isInLobbyRange(Vec3 pos, @Nullable ServerLevel serverLevel) {
        if (serverLevel == null) return false;
        return serverLevel.dimension().equals(this.lobbyLevelKey())
                && isInLobbyRange(pos);
    }
    @Override public boolean canMuteki(@NotNull LivingEntity livingEntity) {
        if (!isLobbyCreated() || _GameTeamManager.hasStandingGamePlayer(livingEntity.getUUID())) { // 游戏中的玩家不能无敌
            return false;
        }

        return livingEntity.level().dimension().equals(this.lobbyLevelKey())
                && isInLobbyRange(livingEntity.position());
    }

    @Override public void sendLobbyInfo(ServerPlayer player) {
        if (player == null) {
            return;
        }

        if (isLobbyCreated()) {
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.lobby_pos", lobbyPos.x, lobbyPos.y, lobbyPos.z).withStyle(ChatFormatting.AQUA));
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.lobby_dimension", lobbyDimension.x, lobbyDimension.y, lobbyDimension.z).withStyle(ChatFormatting.AQUA));
            if (lobbyMuteki) ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.lobby_muteki").withStyle(ChatFormatting.GOLD));
            if (lobbyHeal) ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.lobby_heal").withStyle(ChatFormatting.GREEN));
        } else { // 没有创建大厅
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.no_lobby").withStyle(ChatFormatting.RED));
        }
    }
    @Override public void sendLobbyInfo(ServerLevel serverLevel) {
        if (serverLevel == null) {
            return;
        }

        if (isLobbyCreated()) {
            ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.lobby_pos", lobbyPos.x, lobbyPos.y, lobbyPos.z).withStyle(ChatFormatting.AQUA));
            ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.lobby_dimension", lobbyDimension.x, lobbyDimension.y, lobbyDimension.z).withStyle(ChatFormatting.AQUA));
            if (lobbyMuteki) ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.lobby_muteki").withStyle(ChatFormatting.GOLD));
            if (lobbyHeal) ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.lobby_heal").withStyle(ChatFormatting.GREEN));
        } else { // 没有创建大厅
            ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.no_lobby").withStyle(ChatFormatting.RED));
        }
    }
    @Override public void sendLobbyTeleportMessage(@NotNull ServerPlayer player, boolean isWinner) {
        String toLobbyCommand = GameCommand.toLobbyCommand();

        Component fullMessage = Component.translatable("battleroyale.message.back_to_lobby").withStyle(ChatFormatting.AQUA)
                .append(Component.literal(" "))
                .append(buildRunnableText(Component.translatable("battleroyale.message.teleport"),
                        toLobbyCommand,
                        isWinner ? ChatFormatting.GOLD :  ChatFormatting.GREEN));

        ChatUtils.sendComponentMessageToPlayer(player, fullMessage);
    }
}
