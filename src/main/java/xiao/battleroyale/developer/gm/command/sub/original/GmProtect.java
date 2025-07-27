package xiao.battleroyale.developer.gm.command.sub.original;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import static xiao.battleroyale.developer.gm.command.CommandArg.*;

public class GmProtect {

    public static void addServer(LiteralArgumentBuilder<CommandSourceStack> gmCommand, boolean useFullName) {
        // 二次无敌
        // /battleroyale original muteki2
        gmCommand.then(Commands.literal(useFullName ? MUTEKI_2 : MUTEKI_2_SHORT)
                .executes(GmProtect::executeMuteki2));
        // 无摔传送
        // /battleroyale original saveteleport [id / entity / xyz]
        gmCommand.then(Commands.literal(useFullName ? SAVE_TELEPORT : SAVE_TELEPORT_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .executes(GmProtect::executeSaveTeleportById))
                .then(Commands.argument(ENTITY, EntityArgument.entity())
                        .executes(GmProtect::executeSaveTeleportByEntity))
                .then(Commands.argument(XYZ, Vec3Argument.vec3())
                        .executes(context -> executeSaveTeleportByXYZ(context, Vec3Argument.getVec3(context, XYZ)))));
    }

    /**
     * 二次无敌
     */
    private static int executeMuteki2(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Executing muteki2"), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 无摔传送
     */
    private static int executeSaveTeleportById(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        context.getSource().sendSuccess(() -> Component.literal("Executing saveteleport for ID: " + id), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 无摔传送
     */
    private static int executeSaveTeleportByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        context.getSource().sendSuccess(() -> Component.literal("Executing saveteleport for entity: " + entity.getName().getString()), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 无摔传送
     */
    private static int executeSaveTeleportByXYZ(CommandContext<CommandSourceStack> context, Vec3 pos) {
        context.getSource().sendSuccess(() -> Component.literal("Executing saveteleport to XYZ: " + pos.toString()), false);
        return Command.SINGLE_SUCCESS;
    }
}