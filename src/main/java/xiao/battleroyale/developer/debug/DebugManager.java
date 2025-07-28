package xiao.battleroyale.developer.debug;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.data.io.DevDataTag;
import xiao.battleroyale.data.io.DevDataManager;
import xiao.battleroyale.util.ChatUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DebugManager {

    private static class DebugManagerHolder {
        private static final DebugManager INSTANCE = new DebugManager();
    }

    public static DebugManager get() {
        return DebugManagerHolder.INSTANCE;
    }

    private DebugManager() {
        ;
    }

    public static void init() {
        reloadDebugPlayer();
    }

    private static int DEBUG_PERMISSION_LEVEL = 4;
    public static void setDebugPermissionLevel(int permissionLevel) { DEBUG_PERMISSION_LEVEL = permissionLevel; }

    private static final Map<UUID, String> debugPlayers = new HashMap<>();
    private static void reloadDebugPlayer() {
        Map<UUID, String> loadedDebugPlayers = DevDataManager.get().getJsonUUIDStringMap(DevDataTag.DEBUG, DevDataTag.DEBUG_PLAYERS);
        debugPlayers.clear();
        debugPlayers.putAll(loadedDebugPlayers);
    }
    public static boolean hasDebugPermission(CommandSourceStack source) {
        if (source.hasPermission(DEBUG_PERMISSION_LEVEL)) {
            return true;
        }
        if (source.source instanceof ServerPlayer player) { // 用source.getEntity()不能防止命令方块篡改来源
            return debugPlayers.containsKey(player.getUUID());
        }
        return false;
    }

    public static boolean addDebugPlayer(ServerLevel serverLevel, ServerPlayer serverPlayer) {
        if (serverLevel == null || serverPlayer == null) {
            return false;
        }
        UUID playerUUID = serverPlayer.getUUID();
        if (debugPlayers.containsKey(playerUUID)) {
            return false;
        } else {
            String playerName = serverPlayer.getName().getString();
            debugPlayers.put(playerUUID, playerName);
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.add_debug_player", playerName);
            return true;
        }
    }
    public static boolean removeDebugPlayer(ServerLevel serverLevel, UUID playerUUID) {
        if (debugPlayers.containsKey(playerUUID)) {
            String playerName = debugPlayers.get(playerUUID);
            debugPlayers.remove(playerUUID);
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.remove_debug_player", playerName);
            return true;
        }
        return false;
    }

    public static void broadcastDebugPlayerAction(CommandSourceStack source, String operation) {
        if (source.source instanceof ServerPlayer player) {
            ChatUtils.sendMessageToAllPlayers(source.getLevel(), Component.literal("[Debug]" + player.getName().getString() + ":" + operation).withStyle(ChatFormatting.DARK_GRAY));
        }
    }
    public static void sendDebugMessage(CommandSourceStack source, String operation, MutableComponent debugMessage) {
        broadcastDebugPlayerAction(source, operation);
        MutableComponent fullMessage = Component.translatable("battleroyale.message.debug")
                .append(Component.literal(operation+":"))
                .append(debugMessage);
        source.sendSuccess(() -> fullMessage, false);
        BattleRoyale.LOGGER.debug("[Debug]{}:{}", operation, fullMessage);
    }
}
