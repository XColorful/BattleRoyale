package xiao.battleroyale.developer.gm.command.sub.original;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import static xiao.battleroyale.developer.gm.command.CommandArg.*;

public class GmManager {

    public static void addServer(LiteralArgumentBuilder<CommandSourceStack> gmCommand, boolean useFullName) {
        // 授权GM
        // /battleroyale original accept gm [entity]
        gmCommand.then(Commands.literal(useFullName ? ACCEPT : ACCEPT_SHORT)
                .then(Commands.literal(useFullName ? GAME_MASTER : GAME_MASTER_SHORT)
                        .then(Commands.argument(ENTITY, EntityArgument.entity())
                                .executes(GmManager::executeAcceptGm))));
        // 移除GM
        // /battleroyale original delete gm [entity]
        gmCommand.then(Commands.literal(useFullName ? DELETE : DELETE_SHORT)
                .then(Commands.literal(useFullName ? GAME_MASTER : GAME_MASTER_SHORT)
                        .then(Commands.argument(ENTITY, EntityArgument.entity())
                                .executes(GmManager::executeRemoveGm))));
    }

    /**
     * 授权GM
     */
    private static int executeAcceptGm(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        context.getSource().sendSuccess(() -> Component.literal("Executing accept gm for entity: " + entity.getName().getString()), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 移除GM
     */
    private static int executeRemoveGm(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        context.getSource().sendSuccess(() -> Component.literal("Executing delete gm for entity: " + entity.getName().getString()), false);
        return Command.SINGLE_SUCCESS;
    }
}