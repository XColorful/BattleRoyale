package xiao.battleroyale.developer.debug.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import xiao.battleroyale.api.data.io.DevDataTag;
import xiao.battleroyale.data.io.DevDataManager;
import xiao.battleroyale.developer.debug.command.sub.GetCommand;

import static xiao.battleroyale.developer.debug.command.CommandArg.*;

public class LocalDebugCommand {

    private static boolean LOCAL_DEBUG = false;
    public static void setLocalDebug(boolean bool) { LOCAL_DEBUG = bool; }

    private static void reloadLocalDebug() {
        Boolean localDebug = DevDataManager.get().getBool(DevDataTag.DEBUG, DevDataTag.LOCAL_DEBUG);
        setLocalDebug(localDebug != null && localDebug);
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        reloadLocalDebug();
        dispatcher.register(get(DEBUG_MOD_LOCAL, true)); // 用相同前缀会跳过权限检查，可能是Forge的Bug
        dispatcher.register(get(DEBUG_MOD_LOCAL_SHORT, false));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> get(String rootName, boolean useFullName) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(rootName);
        root.requires(source -> LOCAL_DEBUG);
        LiteralArgumentBuilder<CommandSourceStack> debugCommand = Commands.literal(useFullName ? DEBUG : DEBUG_SHORT);
        debugCommand.then(GetCommand.getClient(useFullName));
        root.then(debugCommand);
        return root;
    }
}
