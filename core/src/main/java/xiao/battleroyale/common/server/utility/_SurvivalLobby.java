package xiao.battleroyale.common.server.utility;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.event.ICustomEventPoster;
import xiao.battleroyale.api.event.server.utility.SurvivalLobbyTeleportEvent;
import xiao.battleroyale.api.event.server.utility.SurvivalLobbyTeleportFinishEvent;
import xiao.battleroyale.api.game.IGameIdReadApi;
import xiao.battleroyale.api.server.IServerManager;
import xiao.battleroyale.api.server.utilitity.ISurvivalLobbyManager;
import xiao.battleroyale.common.game._GameTeamManager;
import xiao.battleroyale.compat.playerrevive.PlayerRevive;
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.Vec3Utils;

public class _SurvivalLobby implements ISurvivalLobbyManager {

    private static class SurvivalLobbyHolder {
        private static final _SurvivalLobby INSTANCE = new _SurvivalLobby();
    }

    public static _SurvivalLobby get() {
        return SurvivalLobbyHolder.INSTANCE;
    }

    protected _SurvivalLobby() {}

    public static void init(McSide mcSide) {
        ;
    }

    protected String levelKeyString;
    protected ResourceKey<Level> levelKey;
    protected boolean allowGamePlayerTeleport = false;
    protected Vec3 lobbyPos;
    protected Vec3 lobbyDimension;
    protected boolean lobbyMuteki = false;
    protected boolean lobbyHeal = false;
    protected boolean changeGamemode = true;
    protected boolean teleportDropInventory = true;
    protected boolean dropGameItemOnly = true;
    protected boolean teleportClearInventory = true;
    protected boolean clearGameItemOnly = true;

    // --------ILobbyFuncApi--------

    @Override public boolean teleportToLobby(@NotNull LivingEntity livingEntity) {
        @Nullable ServerPlayer serverPlayer = livingEntity instanceof ServerPlayer player ? player : null;
        IServerManager serverManager = BattleRoyale.getServerManager();
        ICustomEventPoster eventPoster = BattleRoyale.getEventPoster();
        if (eventPoster.postCustomEvent(new SurvivalLobbyTeleportEvent(serverManager, livingEntity))) {
            BattleRoyale.LOGGER.debug("SurvivalLobbyManager: LobbyTeleportEvent canceled, skipped teleportToLobby (LivingEntity {})", livingEntity.getName().getString());
            return false;
        }

        if (!isLobbyCreated()) {
            BattleRoyale.LOGGER.debug("Survival lobby is not created, failed to teleport livingEntity {} (UUID:{}) to lobby", livingEntity.getName().getString(), livingEntity.getUUID());
            if (serverPlayer != null) ChatUtils.sendTranslatableMessageToPlayer(serverPlayer, "battleroyale.message.no_lobby");
            return false;
        }

        if (!allowGamePlayerTeleport && _GameTeamManager.hasStandingGamePlayer(livingEntity.getUUID())) {
            if (serverPlayer != null) ChatUtils.sendTranslatableMessageToPlayer(serverPlayer, "battleroyale.message.not_allow_standing_gameplayer_teleport");
            return false;
        }

        // 获取大厅所在维度ServerLevel
//        MinecraftServer server = player.getServer();
//        if (server == null) {
//            BattleRoyale.LOGGER.warn("Failed to get MinecraftServer from ServerPlayer {} (UUID:{})", player.getName().getString(), player.getUUID());
//            ChatUtils.sendMessageToPlayer(player, "Failed to get player's server");
//            return;
//        }
        // ↑1.20.1-1.21.6
        ServerLevel serverLevel = BattleRoyale.getMinecraftServer().getLevel(levelKey);
        if (serverLevel == null) {
            BattleRoyale.LOGGER.warn("Failed to get ServerLevel by ResourceKey<Level>: {}, original string: {}", levelKey, levelKeyString);
            if (serverPlayer != null) ChatUtils.sendTranslatableMessageToPlayer(serverPlayer, "battleroyale.message.failed_lobby_teleport");
            return false;
        }

        if (serverPlayer != null && teleportDropInventory) {
            if (dropGameItemOnly) {
                IGameIdReadApi gameIdReadApi = BattleRoyale.getGameManager().getGameIdReadApi();
                Inventory inventory = serverPlayer.getInventory();
                int keepCount = 0;
                for (int i = 0; i < inventory.getContainerSize(); i++) {
                    ItemStack itemStack = inventory.getItem(i);
                    if (itemStack.isEmpty()) {
                        continue;
                    }
                    if (gameIdReadApi.getGameId(itemStack) != null) {
                        serverPlayer.drop(itemStack, true, false);
                        inventory.setItem(i, ItemStack.EMPTY);
                    } else {
                        keepCount++;
                    }
                }
                BattleRoyale.LOGGER.info("DropGameItemOnly: {} has {} without gameId", serverPlayer.getName().getString(), keepCount);
            } else {
                serverPlayer.getInventory().dropAll();
            }
            BattleRoyale.LOGGER.debug("Dropped {}'s inventory", serverPlayer.getName().getString());
        }

        if (serverPlayer != null && teleportClearInventory) {
            if (clearGameItemOnly) { // 仅清理带GameId的物品
                IGameIdReadApi gameIdReadApi = BattleRoyale.getGameManager().getGameIdReadApi();
                Inventory inventory = serverPlayer.getInventory();
                int keepCount = 0;
                for (int i = 0; i < inventory.getContainerSize(); i++) {
                    ItemStack itemStack = inventory.getItem(i);
                    if (itemStack.isEmpty()) {
                        continue;
                    }
                    if (gameIdReadApi.getGameId(itemStack) != null) {
                        inventory.setItem(i, ItemStack.EMPTY);
                    } else {
                        keepCount++;
                    }
                }
                BattleRoyale.LOGGER.info("ClearGameItemOnly: {} has {} without gameId", serverPlayer.getName().getString(), keepCount);
            } else {
                serverPlayer.getInventory().clearContent();
            }
            BattleRoyale.LOGGER.debug("Cleared {}'s inventory", serverPlayer.getName().getString());
        }

        if (lobbyHeal) {
            healPlayer(livingEntity);
        }
        if (serverPlayer != null && changeGamemode) {
            serverPlayer.setGameMode(GameType.SURVIVAL);
        }
        BattleRoyale.getGameManager().safeTeleport(livingEntity, serverLevel, lobbyPos, 0, 0); // 生存大厅传送
        BattleRoyale.LOGGER.info("Teleport livingEntity {} (UUID:{}) to lobby ({}, {}, {})", livingEntity.getName().getString(), livingEntity.getUUID(), lobbyPos.x, lobbyPos.y, lobbyPos.z);
        eventPoster.postCustomEvent(new SurvivalLobbyTeleportFinishEvent(serverManager, livingEntity));
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
    @Override public boolean setLobby(Vec3 centerPos, double radius) {
        return setLobby(centerPos, new Vec3(radius, radius, radius), this.lobbyMuteki, this.lobbyHeal, this.changeGamemode, this.teleportDropInventory, this.teleportClearInventory);
    }
    @Override public boolean setLobby(Vec3 centerPos, Vec3 dimension,
                            boolean shouldMuteki, boolean shouldHeal, boolean changeGamemode,
                            boolean teleportDropInventory, boolean teleportClearInventory) {
        return setLobby(this.levelKeyString, this.allowGamePlayerTeleport,
                centerPos, dimension, shouldHeal, shouldHeal, changeGamemode,
                teleportDropInventory, this.dropGameItemOnly, teleportClearInventory, this.clearGameItemOnly);
    }
    @Override
    public boolean setLobby(String levelKeyString, boolean allowGamePlayerTeleport,
                            Vec3 lobbyPos, Vec3 lobbyDimension, boolean lobbyMuteki, boolean lobbyHeal, boolean changeGamemode,
                            boolean teleportDropInventory, boolean dropGameItemOnly, boolean teleportClearInventory, boolean clearGameItemOnly) {
        if (Vec3Utils.hasNegative(lobbyDimension)) {
            BattleRoyale.LOGGER.warn("SurvivalLobby: dimension:{} has negative, reject to apply", lobbyDimension);
            return false;
        }
        this.levelKeyString = levelKeyString;
        this.levelKey = ResourceKey.create(Registries.DIMENSION, BattleRoyale.getMcRegistry().createResourceLocation(levelKeyString));
        this.allowGamePlayerTeleport = allowGamePlayerTeleport;

        this.lobbyPos = lobbyPos;
        this.lobbyMuteki = lobbyMuteki;
        if (this.lobbyMuteki) {
            _SurvivalLobbyEventHandler.register();
        } else {
            _SurvivalLobbyEventHandler.unregister();
        }
        this.lobbyHeal = lobbyHeal;
        this.changeGamemode = changeGamemode;
        this.lobbyDimension = lobbyDimension;
        this.teleportDropInventory = teleportDropInventory;
        this.dropGameItemOnly = dropGameItemOnly;
        this.teleportClearInventory = teleportClearInventory;
        this.clearGameItemOnly = clearGameItemOnly;
        BattleRoyale.LOGGER.debug("Successfully set survival lobby: levelKey:{}, center{}, dim{}", levelKey, lobbyPos, lobbyDimension);
        return true;
    }

    // --------ILobbyReadApi--------

    @Override public boolean isLobbyCreated() {
        return lobbyPos != null && lobbyDimension != null;
    }
    @Override public ResourceKey<Level> lobbyLevelKey() {
        return levelKey;
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
        if (!isLobbyCreated() || _GameTeamManager.hasStandingGamePlayer(livingEntity.getUUID())) {
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
}
