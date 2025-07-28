package xiao.battleroyale.developer.gm.command.sub.original;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
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

    public static void addServer(LiteralArgumentBuilder<CommandSourceStack> ogCommand, boolean useFullName) {
        // 修改血量
        // /battleroyale original sethealth [id / entity]
        ogCommand.then(Commands.literal(useFullName ? SET_HEALTH : SET_HEALTH_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .then(Commands.argument(AMOUNT, FloatArgumentType.floatArg())
                                .executes(VanillaJava::setHealthById)))
                .then(Commands.argument(ENTITY, EntityArgument.entity())
                        .then(Commands.argument(AMOUNT, FloatArgumentType.floatArg())
                                .executes(VanillaJava::setHealthByEntity))));
        // 修改掉落高度
        // /battleroyale original falldistance [id / entity]
        ogCommand.then(Commands.literal(useFullName ? FALL_DISTANCE : FALL_DISTANCE_SHORT)
                .then(Commands.argument(SINGLE_ID, IntegerArgumentType.integer())
                        .then(Commands.argument(AMOUNT, FloatArgumentType.floatArg())
                                .executes(VanillaJava::fallDistanceById)))
                .then(Commands.argument(ENTITY, EntityArgument.entity())
                        .then(Commands.argument(AMOUNT, FloatArgumentType.floatArg())
                                .executes(VanillaJava::fallDistanceByEntity))));
    }

    /**
     * 修改血量
     */
    private static int setHealthById(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        final float amount = FloatArgumentType.getFloat(context, AMOUNT);
        context.getSource().sendSuccess(() -> Component.literal("Executing sethealth for ID " + id + " to: " + amount), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 修改血量
     */
    private static int setHealthByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        final float amount = FloatArgumentType.getFloat(context, AMOUNT);
        context.getSource().sendSuccess(() -> Component.literal("Executing sethealth for entity " + entity.getName().getString() + " to: " + amount), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 修改掉落高度
     */
    private static int fallDistanceById(CommandContext<CommandSourceStack> context) {
        final int id = IntegerArgumentType.getInteger(context, SINGLE_ID);
        final float amount = FloatArgumentType.getFloat(context, AMOUNT);
        context.getSource().sendSuccess(() -> Component.literal("Executing falldistance for ID " + id + " to: " + amount), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 修改掉落高度
     */
    private static int fallDistanceByEntity(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(context, ENTITY);
        final float amount = FloatArgumentType.getFloat(context, AMOUNT);
        context.getSource().sendSuccess(() -> Component.literal("Executing falldistance for entity " + entity.getName().getString() + " to: " + amount), false);
        return Command.SINGLE_SUCCESS;
    }
}