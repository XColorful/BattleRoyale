package xiao.battleroyale.developer.gm.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import static xiao.battleroyale.developer.gm.command.CommandArg.*;

public class DebugManager {
    public static void addServer(LiteralArgumentBuilder<CommandSourceStack> gmCommand, boolean useFullName) {
        // 调整调试权限
        // /battleroyale gamemaster debugpermission [level]
        gmCommand.then(Commands.literal(useFullName ? DEBUG_PERMISSION : DEBUG_PERMISSION_SHORT)
                .then(Commands.argument(LEVEL, IntegerArgumentType.integer())
                        .executes(DebugManager::executeDebugPermission)));
        // 增减调试玩家
        // /battleroyale gamemaster debug [entity] [bool]
        gmCommand.then(Commands.literal(useFullName ? DEBUG : DEBUG_SHORT)
                .then(Commands.argument(ENTITY, EntityArgument.entity())
                        .then(Commands.argument(BOOL, BoolArgumentType.bool())
                                .executes(DebugManager::executeDebugPlayer))));
    }

    /**
     * 调整Debug权限
     */
    private static int executeDebugPermission(CommandContext<CommandSourceStack> context) {
        final int level = IntegerArgumentType.getInteger(context, LEVEL);
        context.getSource().sendSuccess(() -> Component.literal("Executing set debug permission level to: " + level), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 修改调试玩家
     */
    private static int executeDebugPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        boolean add = IntegerArgumentType.getInteger(context, BOOL) == 1;
        if (!(entity instanceof ServerPlayer player)) {
            context.getSource().sendFailure(Component.literal("Entity must be a player."));
            return 0;
        }
        if (add) {
            context.getSource().sendSuccess(() -> Component.literal("Executing add debug player: " + player.getName().getString()), false);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Executing remove debug player: " + player.getName().getString()), false);
        }
        return Command.SINGLE_SUCCESS;
    }
}