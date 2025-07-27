package xiao.battleroyale.developer.gm.command.sub.original;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import static xiao.battleroyale.developer.gm.command.CommandArg.*;

public class VanillaJava {

    public static void addServer(LiteralArgumentBuilder<CommandSourceStack> gmCommand, boolean useFullName) {
        // 修改血量
        // /battleroyale original sethealth [id / entity]
        gmCommand.then(Commands.literal(useFullName ? SET_HEALTH : SET_HEALTH_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .then(Commands.argument(AMOUNT, IntegerArgumentType.integer())
                                .executes(VanillaJava::executeSetHealthById)))
                .then(Commands.argument(ENTITY, EntityArgument.entity())
                        .then(Commands.argument(AMOUNT, IntegerArgumentType.integer())
                                .executes(VanillaJava::executeSetHealthByEntity))));
        // 修改掉落高度
        // /battleroyale original falldistance [id / entity]
        gmCommand.then(Commands.literal(useFullName ? FALL_DISTANCE : FALL_DISTANCE_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .then(Commands.argument(AMOUNT, IntegerArgumentType.integer())
                                .executes(VanillaJava::executeFallDistanceById)))
                .then(Commands.argument(ENTITY, EntityArgument.entity())
                        .then(Commands.argument(AMOUNT, IntegerArgumentType.integer())
                                .executes(VanillaJava::executeFallDistanceByEntity))));
    }

    /**
     * 修改血量
     */
    private static int executeSetHealthById(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        final int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        context.getSource().sendSuccess(() -> Component.literal("Executing sethealth for ID " + id + " to: " + amount), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 修改血量
     */
    private static int executeSetHealthByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        final int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        context.getSource().sendSuccess(() -> Component.literal("Executing sethealth for entity " + entity.getName().getString() + " to: " + amount), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 修改掉落高度
     */
    private static int executeFallDistanceById(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        final int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        context.getSource().sendSuccess(() -> Component.literal("Executing falldistance for ID " + id + " to: " + amount), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 修改掉落高度
     */
    private static int executeFallDistanceByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        final int amount = IntegerArgumentType.getInteger(context, AMOUNT);
        context.getSource().sendSuccess(() -> Component.literal("Executing falldistance for entity " + entity.getName().getString() + " to: " + amount), false);
        return Command.SINGLE_SUCCESS;
    }
}