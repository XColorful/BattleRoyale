package xiao.battleroyale.developer.debug.command.sub.get;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.developer.debug.DebugLoot;
import xiao.battleroyale.developer.debug.DebugManager;

import static xiao.battleroyale.developer.debug.command.CommandArg.*;

public class GetLoot {

    public static void addServer(LiteralArgumentBuilder<CommandSourceStack> getCommand, boolean useFullName) {

        // get commonloot
        getCommand.then(Commands.literal(useFullName ? COMMON_LOOT : COMMON_LOOT_SHORT)
                .executes(GetLoot::getCommonLootManager));

        // get gameloot
        getCommand.then(Commands.literal(useFullName ? GAME_LOOT : GAME_LOOT_SHORT)
                .executes(context -> getGameLootManager(context, context.getSource().getPosition()))
                .then(Commands.argument(XYZ, Vec3Argument.vec3())
                        .executes(context -> getGameLootManager(context, Vec3Argument.getVec3(context, XYZ)))));
    }

    private static int getCommonLootManager(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugLoot.get().getCommonLoot(source);
        return Command.SINGLE_SUCCESS;
    }

    private static int getGameLootManager(CommandContext<CommandSourceStack> context, Vec3 pos) {
        CommandSourceStack source = context.getSource();
        if (!DebugManager.hasDebugPermission(source)) {
            context.getSource().sendFailure(Component.translatable("battleroyale.message.no_debug_permission"));
            return 0;
        }

        DebugLoot.get().getGameLoot(source, pos);
        return Command.SINGLE_SUCCESS;
    }
}
