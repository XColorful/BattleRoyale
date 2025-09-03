package xiao.battleroyale.common.server.utility;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.compat.playerrevive.PlayerRevive;
import xiao.battleroyale.event.server.SurvivalLobbyEventHandler;
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.GameUtils;
import xiao.battleroyale.util.Vec3Utils;

public class SurvivalLobby {

    private static class SurvivalLobbyHolder {
        private static final SurvivalLobby INSTANCE = new SurvivalLobby();
    }

    public static SurvivalLobby get() {
        return SurvivalLobbyHolder.INSTANCE;
    }

    private SurvivalLobby() {}

    public static void init() {
        ;
    }

    private String levelKeyString;
    private ResourceKey<Level> levelKey;
    private boolean allowGamePlayerTeleport = false;
    private Vec3 lobbyPos;
    private Vec3 lobbyDimension;
    private boolean lobbyMuteki = false;
    private boolean lobbyHeal = false;
    private boolean clearInventory = true;
    private boolean clearGameItemOnly = true;

    public void healPlayer(@NotNull ServerPlayer player) {
        if (PlayerRevive.get().isBleeding(player)) {
            PlayerRevive.get().revive(player);
        }
        player.removeAllEffects();
        player.heal(player.getMaxHealth()); // heal会触发事件
        player.getFoodData().setFoodLevel(20);
    }

    public void teleportToLobby(@NotNull ServerPlayer player) {
        if (!isLobbyCreated()) {
            BattleRoyale.LOGGER.debug("Survival lobby is not created, failed to teleport player {} (UUID:{}) to lobby", player.getName().getString(), player.getUUID());
            ChatUtils.sendTranslatableMessageToPlayer(player, "battleroyale.message.no_lobby");
            return;
        }
        if (!allowGamePlayerTeleport && GameManager.get().hasStandingGamePlayer(player.getUUID())) {
            ChatUtils.sendTranslatableMessageToPlayer(player, "battleroyale.message.not_allow_standing_gameplayer_teleport");
            return;
        }

        // 获取大厅所在维度ServerLevel
        MinecraftServer server = player.getServer();
        if (server == null) {
            BattleRoyale.LOGGER.warn("Failed to get MinecraftServer from ServerPlayer {} (UUID:{})", player.getName().getString(), player.getUUID());
            ChatUtils.sendMessageToPlayer(player, "Failed to get player's server");
            return;
        }
        ServerLevel serverLevel = server.getLevel(levelKey);
        if (serverLevel == null) {
            ChatUtils.sendTranslatableMessageToPlayer(player, "battleroyale.message.failed_lobby_teleport");
            BattleRoyale.LOGGER.warn("Failed to get ServerLevel by ResourceKey<Level>: {}, original string: {}", levelKey, levelKeyString);
            return;
        }

        if (clearInventory) {
            if (clearGameItemOnly) { // 仅清理带GameId的物品
                Inventory inventory = player.getInventory();
                int keepCount = 0;
                for (int i = 0; i < inventory.getContainerSize(); i++) {
                    ItemStack stack = inventory.getItem(i);
                    if (stack.isEmpty()) {
                        continue;
                    }
                    if (GameUtils.getGameId(stack) != null) {
                        inventory.setItem(i, ItemStack.EMPTY);
                    } else {
                        keepCount++;
                    }
                }
                BattleRoyale.LOGGER.info("ClearGameItemOnly: {} has {} without gameId", player.getName().getString(), keepCount);
            } else {
                player.getInventory().clearContent();
            }
            BattleRoyale.LOGGER.debug("Cleared {}'s inventory", player.getName().getString());
        }

        if (lobbyHeal) {
            healPlayer(player);
        }
        player.setGameMode(GameType.SURVIVAL);
        GameManager.get().safeTeleport(player, serverLevel, lobbyPos, 0, 0);
        BattleRoyale.LOGGER.info("Teleport player {} (UUID:{}) to lobby ({}, {}, {})", player.getName().getString(), player.getUUID(), lobbyPos.x, lobbyPos.y, lobbyPos.z);
    }

    public boolean canMuteki(ServerPlayer player) {
        if (!isLobbyCreated() || GameManager.get().hasStandingGamePlayer(player.getUUID())) {
            return false;
        }

        return player.level().dimension() == levelKey
                && isInLobbyRange(player.position());
    }

    private boolean isInLobbyRange(Vec3 pos) {
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

    public boolean isLobbyCreated() {
        return lobbyPos != null;
    }

    public void sendLobbyInfo(ServerPlayer player) {
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

    public void sendLobbyInfo(ServerLevel serverLevel) {
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

    public boolean setLobby(String levelDimension, boolean allowGamePlayerTeleport,
                            Vec3 lobbyPos, Vec3 lobbyDimension, boolean lobbyMuteki, boolean lobbyHeal,
                            boolean clearInventory, boolean clearGameItemOnly) {
        if (Vec3Utils.hasNegative(lobbyDimension)) {
            BattleRoyale.LOGGER.warn("SurvivalLobby: dimension:{} has negative, reject to apply", lobbyDimension);
            return false;
        }
        this.levelKeyString = levelDimension;
        levelKey = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(levelDimension));
        this.allowGamePlayerTeleport = allowGamePlayerTeleport;

        this.lobbyPos = lobbyPos;
        this.lobbyMuteki = lobbyMuteki;
        if (this.lobbyMuteki) {
            SurvivalLobbyEventHandler.register();
        } else {
            SurvivalLobbyEventHandler.unregister();
        }
        this.lobbyHeal = lobbyHeal;
        this.lobbyDimension = lobbyDimension;
        this.clearInventory = clearInventory;
        this.clearGameItemOnly = clearGameItemOnly;
        BattleRoyale.LOGGER.debug("Successfully set survival lobby: levelKey:{}, center{}, dim{}", levelKey, lobbyPos, lobbyDimension);
        return true;
    }
}
