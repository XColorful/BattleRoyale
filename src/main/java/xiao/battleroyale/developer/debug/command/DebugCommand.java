package xiao.battleroyale.developer.debug.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import xiao.battleroyale.developer.debug.command.sub.GetCommand;
import xiao.battleroyale.util.ChatUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static xiao.battleroyale.developer.debug.command.CommandArg.*;

public class DebugCommand {

    private static int DEBUG_PERMISSION_LEVEL = 4;
    public static void setDebugPermissionLevel(int permissionLevel) { DEBUG_PERMISSION_LEVEL = permissionLevel; }

    private static final Map<UUID, String> debugPlayers = new HashMap<>();
    private static void reloadDebugPlayer() {
        ;
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

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(get(DEBUG_MOD, true));
        dispatcher.register(get(DEBUG_MOD_SHORT, false));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> get(String rootName, boolean useFullName) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(rootName);
        root.requires(source -> {
            if (source.hasPermission(DEBUG_PERMISSION_LEVEL)) {
                return true;
            }
            if (source.source instanceof ServerPlayer player) { // 用source.getEntity()不能防止命令方块篡改来源
                return debugPlayers.containsKey(player.getUUID());
            }
            return false;
        });
        LiteralArgumentBuilder<CommandSourceStack> debugCommand = Commands.literal(useFullName ? DEBUG : DEBUG_SHORT);
        debugCommand.then(GetCommand.get(useFullName));
        root.then(debugCommand);
        return root;
    }
}
