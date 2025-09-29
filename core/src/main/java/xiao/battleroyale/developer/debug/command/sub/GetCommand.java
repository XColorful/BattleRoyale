package xiao.battleroyale.developer.debug.command.sub;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import xiao.battleroyale.developer.debug.command.sub.get.*;

import static xiao.battleroyale.developer.debug.command.CommandArg.*;
import static xiao.battleroyale.util.StringUtils.buildCommandString;

public class GetCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> getServer(boolean useFullName) {
        LiteralArgumentBuilder<CommandSourceStack> getCommand = Commands.literal(GET);

        GetGame.addServer(getCommand, useFullName);
        GetLoot.addServer(getCommand, useFullName);
        GetMessage.addServer(getCommand, useFullName);
        GetEffect.addServer(getCommand, useFullName);
        GetWorld.addServer(getCommand, useFullName);

        return getCommand;
    }

    public static LiteralArgumentBuilder<CommandSourceStack> getClient(boolean useFullName) {
        LiteralArgumentBuilder<CommandSourceStack> getCommand = Commands.literal(GET);

        GetMessage.addClient(getCommand, useFullName);

        return getCommand;
    }

    public static String buildDebugCommandString(String... parts) {
        int length = 2 + parts.length;
        String[] newParts = new String[length];
        newParts[0] = DEBUG_MOD;
        newParts[1] = DEBUG;
        System.arraycopy(parts, 0, newParts, 2, parts.length);
        return buildCommandString(newParts);
    }

    public static String buildLocalDebugCommandString(String... parts) {
        int length = 2 + parts.length;
        String[] newParts = new String[length];
        newParts[0] = DEBUG_MOD;
        newParts[1] = DEBUG_LOCAL;
        System.arraycopy(parts, 0, newParts, 2, parts.length);
        return buildCommandString(newParts);
    }
}